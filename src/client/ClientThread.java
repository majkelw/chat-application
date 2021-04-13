package client;

import java.io.DataInputStream;
import java.io.IOException;

public class ClientThread extends Thread {
    private final DataInputStream dataInputStream;

    public ClientThread(DataInputStream dataInputStream) {
        this.dataInputStream = dataInputStream;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String message = dataInputStream.readUTF();
                if (message.equals("left"))
                    break;
                System.out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
