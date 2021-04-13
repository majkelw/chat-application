package server;

import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Server {
    private static final List<Room> rooms = new ArrayList<>();

    public static Room createRoom(String roomName, String username, DataOutputStream creator) {
        if (roomCreated(roomName))
            return null;
        Room room = new Room(roomName, username, creator);
        rooms.add(room);
        return room;
    }

    public static Room joinRoom(String roomName, String username, DataOutputStream userDataOutputStream) {
        for (Room room : rooms) {
            if (room.getRoomName().equals(roomName)) {
                Map<String, DataOutputStream> users = room.getUsersMap();
                for (Map.Entry<String, DataOutputStream> entry : users.entrySet()) {
                    if (entry.getKey().equals(username))
                        return null;
                }
                room.addUserOutputStream(username, userDataOutputStream);
                return room;
            }
        }
        return null;
    }

    public static boolean roomCreated(String roomName) {
        for (Room room : rooms) {
            if (room.getRoomName().equals(roomName))
                return true;
        }
        return false;
    }

    private void connectWithNewClients(ServerSocket serverSocket) throws IOException {
        Socket socket;
        while (true) {
            socket = serverSocket.accept();
            System.out.println("New connection from the client " + socket);
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            ServerThread serverThread = new ServerThread(socket, dataInputStream, dataOutputStream);
            serverThread.start();
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            ServerSocket serverSocket = new ServerSocket(60);
            System.out.println("Server has started working...");
            server.connectWithNewClients(serverSocket);
        } catch (ConnectException e) {
            System.out.println("ConnectException occurred");
        } catch (SocketException e) {
            System.out.println("Cannot create server");
        } catch (IOException e) {
            System.out.println("IOException occurred");
        }
    }
}
