package Server;

import java.io.*;
import java.net.ServerSocket;

public class MultiClientServer {
    private ServerSocket serverSocket;
    static int port = 3000;
    static int clientCount = 0;

    /**
     *  starts the server when called and calls ClientHandler for new connections
     * @throws IOException
     */
    public void start(int port) throws IOException {
        System.out.println("Server Listening on port "+port);
        serverSocket = new ServerSocket(port);
        //only allowing two clients as that is the intended use of our app for now
        while (clientCount <= 2)
            new ClientHandler(serverSocket.accept()).start();
    }

    public void stop() throws IOException {
        serverSocket.close();
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
            try {
                server.stop();
                System.exit(1);
            }catch(IOException e1){
                e1.getMessage();
            }
        }
    }
}
