package client;

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

    private String askToCreateRoom(String name, String roomName) throws IOException {
        sendUserRoomInfoToServer(name, roomName);
        return dataInputStream.readUTF();
    }

    private String askToJoinRoom(String name, String roomName) throws IOException {
        sendUserRoomInfoToServer(name, roomName);
        return dataInputStream.readUTF();
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

    private boolean manageData(int choice, String name, String roomName) throws IOException {
        dataOutputStream.writeInt(choice);
        if (choice == 1) {
            if (askToCreateRoom(name, roomName).equals("Room exists")) {
                System.out.printf("Room %s already exists...\n", roomName);
            } else {
                System.out.printf("Welcome in %s\n", roomName);
                return true;
            }
        } else {
            String receivedMessage = askToJoinRoom(name, roomName);
            if (receivedMessage.equals("Room does not exist"))
                System.out.printf("Room %s does not exist...\n", roomName);
            else if (receivedMessage.equals("This username is taken..."))
                System.out.printf("Username %s is taken in this room...\n", name);
            else {
                System.out.printf("Welcome in %s\n", roomName);
                return true;
            }
        }
        return false;
    }

    private void getDataFromUserInput() throws IOException {
        System.out.println("Create a new room, type 1");
        System.out.println("Join to the room, type 2");
        Scanner scanner = new Scanner(System.in);
        boolean flag = true;
        while (flag) {
            System.out.println("Make your choice ");
            int choice;
            try {
                choice = scanner.nextInt();
                if (choice != 1 && choice != 2) {
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
            if (manageData(choice, name, roomName))
                flag = false;
        }
    }

    public static void main(String[] args) {

        try {
            Socket socket = new Socket("localhost", 60);
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            Client client = new Client(output, input);
            while (true) {
                client.getDataFromUserInput();
                ClientThread clientThread = new ClientThread(input);
                clientThread.start();
                client.startConversation();
            }
        } catch (IOException e) {
            System.out.println("IO Exception");
            e.printStackTrace();
        }
    }
}
