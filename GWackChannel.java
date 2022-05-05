import java.io.IOException;
import java.net.*;
import java.util.*;

public class GWackChannel {

    private final List<ClientConnection> connectedUsers;

    public GWackChannel(int PORT) {
        connectedUsers = new LinkedList<>();
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            return;
        }
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {

            }
            // new thread for a client
            if(socket!= null){
                ClientConnection newClient = new ClientConnection(this, socket);
                connectedUsers.add(newClient);
                newClient.start();
            }
        }
    }
    public static void main(String[] args) {
        GWackChannel channel = new GWackChannel(Integer.parseInt(args[0]));
    }

    public void setMessage(ClientConnection c, String message) throws IOException{
        for (ClientConnection user : connectedUsers)
            user.rcv("[" + c.getUser() + "] " + message);
    }

    void rmSingleUser(ClientConnection u) throws IOException{
        connectedUsers.remove(u);
        userList();
    }

     void userList() throws IOException {
         for (ClientConnection user : connectedUsers) {
             user.rcv("START_CLIENT_LIST");
             for (ClientConnection u : connectedUsers)
                user.rcv(u.getUser());
             user.rcv("END_CLIENT_LIST");
         }

    }

}
