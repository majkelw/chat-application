package server;

import enums.ValidationInfo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Server {
    private static final List<Room> rooms = new ArrayList<>();


    public static Room createRoom(String roomName, String username, DataOutputStream creator) {
        Room room = new Room(roomName, username, creator);
        rooms.add(room);
        return room;
    }

    public static Room joinRoom(String roomName, String username, DataOutputStream userDataOutputStream) {
        for (Room room : rooms) {
            if (room.getRoomName().equals(roomName)) {
                room.addUserOutputStream(username, userDataOutputStream);
                return room;
            }
        }
        return null;
    }

    public static ValidationInfo validate(String username, String roomName, int option) {

        if (option == 1) {//create new room
            for (Room room : rooms)
                if (room.getRoomName().equals(roomName))
                    return ValidationInfo.ROOM_EXISTS;
        } else if (option == 2) {//join to the room
            boolean flag = false;
            Room roomFound = null;
            for (Room room : rooms) {
                if (room.getRoomName().equals(roomName)) {
                    flag = true;
                    roomFound = room;
                }
            }
            if (!flag)
                return ValidationInfo.ROOM_DOES_NOT_EXIST;

            Map<String, DataOutputStream> usersMap = roomFound.getUsersMap();
            for (Map.Entry<String, DataOutputStream> entry : usersMap.entrySet()) {
                if (entry.getKey().equals(username)) {
                    System.out.println("user exists in this room!");
                    return ValidationInfo.USER_EXISTS_IN_ROOM;
                }
            }
        }
        return ValidationInfo.SUCCESS;
    }

    private void connectWithNewClients(ServerSocket serverSocket) throws IOException {
        Socket socket;
        while (true) {
            socket = serverSocket.accept();
            System.out.println("New connection from the client " + socket);
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            ServerThread serverThread = new ServerThread(dataInputStream, dataOutputStream);
            serverThread.start();
        }
    }


    public static void main(String[] args) throws IOException {
        Server server = new Server();
        ServerSocket serverSocket = new ServerSocket(60);
        System.out.println("Server has started working...");
        server.connectWithNewClients(serverSocket);
    }
}
