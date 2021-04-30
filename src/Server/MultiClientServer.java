package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MultiClientServer {
    private ServerSocket server;
    private static int port = 3000;
    static int clientCount = 0;
    static List<ClientHandler> loggedIn = new ArrayList<>();
    static List<String> clients = new ArrayList<>();

    public static int getClientCount(){return clientCount;}
    public static void decreaseClientCount(){clientCount--;}
    public static void increaseClientCount(){clientCount++;}

    /**
     *  starts the server when called and calls ClientHandler for new connections
     * @throws IOException
     */
    public void start(int port) throws IOException, InterruptedException {
        System.out.println("Server Listening on port "+port);
        server = new ServerSocket(port);
        //only allowing two clients as that is the intended use of our app for now
        while (loggedIn.size() <= 2) {
            Socket client = server.accept();
            ClientHandler c = new ClientHandler(client);
            loggedIn.add(c);
            System.out.println(loggedIn.size() +" clients logged in.");
            if(loggedIn.size() == 2){
                for(ClientHandler ch: loggedIn)
                    System.out.println(ch.client.getUsername());
                TimeUnit.SECONDS.sleep(2);
                broadcastToAll();
            }
        }

    }

    /**
     * Server uses this method to send a message to the username passed
     * client must be listening with an open message thread to get this message
     * @param user
     * @param message
     */
    public static void broadcast(String user, String message)  {
        for ( ClientHandler c : loggedIn )
            if ( ! c.client.getUsername().equals(user) )
                c.sendMessage(user,message);
    }
    public static void broadcastToAll(){
        System.out.println("Broadcasting to "+loggedIn.size()+" users");
        for(int i = 0 ; i <= loggedIn.size(); i++){
            System.out.println(loggedIn.get(i).client.getUsername());
            loggedIn.get(i).sendMessage("start");
        }
    }
    /**
     *
     * @param args
     */
    public static void main(String[] args){
        MultiClientServer server = new MultiClientServer();
        try{
            server.start(MultiClientServer.port);
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }
}
