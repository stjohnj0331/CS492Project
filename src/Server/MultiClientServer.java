package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MultiClientServer {
    private ServerSocket server;
    private static int port = 3000;
    static int clientCount = 0;
    static List<ClientHandler> loggedIn = new ArrayList<>(2);
    static List<String> clients = new ArrayList<>();
    private static boolean broadcast = false;

    public static void removeUser(String username){
        for(int i = 0 ; i < loggedIn.size() ; i++){
            if(loggedIn.get(i).client.getUsername().equals(username))
                loggedIn.remove(i);
        }
    }
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
        while (true) {
            if(loggedIn.size() <= 2) {
                Socket client = server.accept();
                ClientHandler c = new ClientHandler(client);
                System.out.println((loggedIn.size()+1) + " clients logged in.");
            }else
                System.out.println((loggedIn.size())+" clients logged in");
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
    public static void broadcast1(String user, String message)  {
        for ( ClientHandler c : loggedIn )
            if ( ! c.client.getUsername().equals(user) )
                c.sendMessage1(message);
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