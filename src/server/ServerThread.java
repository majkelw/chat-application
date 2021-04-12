package server;

import enums.ValidationInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class ServerThread extends Thread {
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;
    private String username = null;
    private Room room;

    public ServerThread(DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
    }


    private boolean createRoom(String username, String roomName) throws IOException {
        ValidationInfo x = Server.validate(username, roomName, 1);
        if (x == ValidationInfo.ROOM_EXISTS) {
            dataOutputStream.writeInt(ValidationInfo.ROOM_EXISTS.getValue());
            return false;
        } else if (x == ValidationInfo.SUCCESS) {
            this.username = username;
            System.out.println(username + " to room " + roomName);
            room = Server.createRoom(roomName, username, this.dataOutputStream);
            dataOutputStream.writeInt(ValidationInfo.SUCCESS.getValue());
        }
        return true;
    }

    private boolean joinRoom(String username, String roomName) throws IOException {
        ValidationInfo x = Server.validate(username, roomName, 2);
        if (x == ValidationInfo.ROOM_DOES_NOT_EXIST) {
            dataOutputStream.writeInt(ValidationInfo.ROOM_DOES_NOT_EXIST.getValue());
            return false;
        } else if (x == ValidationInfo.USER_EXISTS_IN_ROOM) {
            dataOutputStream.writeInt(ValidationInfo.USER_EXISTS_IN_ROOM.getValue());
            return false;
        } else if (x == ValidationInfo.SUCCESS) {
            this.username = username;
            room = Server.joinRoom(roomName, username, this.dataOutputStream);
            dataOutputStream.writeInt(ValidationInfo.SUCCESS.getValue());
            room.sendMessageChat(username, " joined to the room", 2);
        }

        return true;
    }



    private void startConversation() throws IOException {
        boolean flag = true;
        while (flag) {
            String message = dataInputStream.readUTF();
            if (message.length() == 0)
                continue;
            if(message.equals("<-leave")){
                room.sendMessageChat(username, " left the room",2);
                room.removeUser(username);
                dataOutputStream.writeUTF("left");
                flag = false;
            }
            room.sendMessageChat(username, message, 1);
        }
    }

    @Override
    public void run() {
        try {
            while(true) {
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
        } catch (IOException e) {
            System.out.println("error");
            e.printStackTrace();
        }

    }
}
