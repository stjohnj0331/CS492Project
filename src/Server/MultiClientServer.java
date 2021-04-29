package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
    public void start(int port) throws IOException {
        System.out.println("Server Listening on port "+port);
        server = new ServerSocket(port);
        //only allowing two clients as that is the intended use of our app for now
        while (clientCount <= 2) {
            Socket client = server.accept();
            ClientHandler c = new ClientHandler(client);
            loggedIn.add(c);
            System.out.println("client count: "+(clientCount));
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
