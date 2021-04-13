package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;


public class ServerThread extends Thread {
    private final Socket socket;
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;
    private String username = null;
    private Room room = null;


    public ServerThread(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        this.socket = socket;
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
    }


    private boolean createRoom(String username, String roomName) throws IOException {
        room = Server.createRoom(roomName, username, dataOutputStream);
        if (room == null) {
            dataOutputStream.writeUTF("Room exists");
            return false;
        }
        this.username = username;
        dataOutputStream.writeUTF("Success");
        return true;
    }

    private boolean joinRoom(String username, String roomName) throws IOException {

        if (!Server.roomCreated(roomName)) {
            dataOutputStream.writeUTF("Room does not exist");
            return false;
        }
        room = Server.joinRoom(roomName, username, dataOutputStream);
        if (room == null) {
            dataOutputStream.writeUTF("This username is taken...");
            return false;
        } else {
            this.username = username;
            dataOutputStream.writeUTF("Success");
            room.sendMessage(username, username + " joined to the room");
        }
        return true;
    }


    private void startConversation() throws IOException {
        boolean flag = true;
        while (flag) {
            String message = dataInputStream.readUTF();
            if (message.equals("<-leave")) {
                room.sendMessage(username, username + " left the room");
                room.removeUser(username);
                dataOutputStream.writeUTF("left");
                room = null;
                username = null;
                flag = false;
            } else
                room.sendMessage(username, username + " : " + message);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                boolean flag = true;
                while (flag) {
                    int choice = dataInputStream.readInt();
                    String username = dataInputStream.readUTF();
                    String roomName = dataInputStream.readUTF();
                    if (choice == 1) {
                        if (createRoom(username, roomName))
                            flag = false;
                    } else if (choice == 2)
                        if (joinRoom(username, roomName))
                            flag = false;
                }
                startConversation();
            }
        } catch (ConnectException e) {
            System.out.println("Connect exception occurred");
        } catch (SocketException e) {
            System.out.println("Connection with client lost " + socket);
        } catch (IOException e) {
            System.out.println("IOException occurred");
        } finally {
            try {
                socket.close();
                System.out.println("socket closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
