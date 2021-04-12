package client;

import enums.ValidationInfo;

import java.io.*;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {

    private final DataOutputStream dataOutputStream;
    private final DataInputStream dataInputStream;

    public Client(DataOutputStream dataOutputStream, DataInputStream dataInputStream) {
        this.dataOutputStream = dataOutputStream;
        this.dataInputStream = dataInputStream;
    }

    private void sendUserRoomInfoToServer(String name, String roomName) throws IOException {
        dataOutputStream.writeUTF(name);
        dataOutputStream.writeUTF(roomName);
    }

    private int askToCreateRoom(String name, String roomName) throws IOException {
        sendUserRoomInfoToServer(name, roomName);
        return dataInputStream.readInt();
    }

    private int askToJoinRoom(String name, String roomName) throws IOException {
        sendUserRoomInfoToServer(name, roomName);
        return dataInputStream.readInt();
    }

    private void startConversation() throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String message = scanner.nextLine();
            dataOutputStream.writeUTF(message);
            if (message.equals("<-leave"))
                break;
        }
    }

    private void getUsernameAndRoomName() throws IOException {
        System.out.println("Create a new room, type 1");
        System.out.println("Join to the room, type 2");
        Scanner scanner = new Scanner(System.in);
        boolean flag = true;
        while (flag) {
            System.out.println("Make your choice ");
            int x;
            try {
                x = scanner.nextInt();
                if (x != 1 && x != 2) {
                    System.out.println("Input must be digit 1 or 2");
                    continue;
                }
            } catch (InputMismatchException e) {
                System.out.println("Input must be digit 1 or 2");
                continue;
            } finally {
                scanner.nextLine();
            }

            System.out.println("Enter your username");
            String name = scanner.nextLine();
            System.out.println("Enter room name");
            String roomName = scanner.nextLine();
            dataOutputStream.writeInt(x);
            if (x == 1) {

                if (askToCreateRoom(name, roomName) == ValidationInfo.ROOM_EXISTS.getValue()) {
                    System.out.printf("Room %s already exists...\n", roomName);
                } else {
                    System.out.printf("Welcome in %s\n", roomName);
                    flag = false;
                }
            } else {
                x = askToJoinRoom(name, roomName);
                if (x == ValidationInfo.ROOM_DOES_NOT_EXIST.getValue())
                    System.out.printf("Room %s does not exist...\n", roomName);
                else if (x == ValidationInfo.USER_EXISTS_IN_ROOM.getValue())
                    System.out.printf("Username %s exits in the room...\n", name);
                else {
                    System.out.printf("Welcome in %s\n", roomName);
                    flag = false;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("localhost", 60);
        DataInputStream input = new DataInputStream(socket.getInputStream());
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        Client client = new Client(output, input);


        while (true) {
            client.getUsernameAndRoomName();
            ClientThread clientThread = new ClientThread(input);
            clientThread.start();
            client.startConversation();
            System.out.println("RYA ");

        }
    }
}
