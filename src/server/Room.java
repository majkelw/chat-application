package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Room {
    private final String roomName;
    private final Map<String, DataOutputStream> usersMap = new HashMap<>();

    public String getRoomName() {
        return roomName;
    }

    public Map<String, DataOutputStream> getUsersMap() {
        return usersMap;
    }

    public Room(String roomName, String username, DataOutputStream creatorOutputStream) {
        this.roomName = roomName;
        usersMap.put(username, creatorOutputStream);
    }

    public void addUserOutputStream(String username, DataOutputStream dataOutputStream) {
        usersMap.put(username, dataOutputStream);
    }

    public void removeUser(String username){
        for (Map.Entry<String, DataOutputStream> entry : usersMap.entrySet()) {
            if (entry.getKey().equals(username))
                usersMap.remove(username);
        }
    }

    public void sendMessageChat(String username, String message, int operation) throws IOException {
        String messageToSend;
        if (operation == 1)
            messageToSend = username + " : " + message;
        else
            messageToSend = username + message;

        for (Map.Entry<String, DataOutputStream> entry : usersMap.entrySet()) {
            System.out.println(entry.getKey());
            if (!entry.getKey().equals(username)) {
                entry.getValue().writeUTF(messageToSend);
            }
        }
    }


}
