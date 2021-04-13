package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {


    /**
     * Server currently just listens on the specified port, prints that a new client has connected
     * prints a message from the client, and sends the server time to the client
     * @param args
     */
    public static void main(String[] args) {
        //Desired port for server to listen on
        int port = 443;
        //------------------------------------

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                
                String clientData = reader.readLine();
                System.out.println(clientData);
                
                if(comparator(clientData, "12"))
                    System.out.println("New client connected");

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                writer.println(new Date().toString());

            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public boolean validLogin(String hashPassword) throws IOException{
        File login = new File("login.txt");
        Scanner reader = new Scanner(login);
        String line;
        while(reader.hasNextLine()){
            if(comparator(hashPassword, line) == true)
                return true;
        }
        return false;
    }

    public static boolean comparator(String s1, String s2){
        return s1.equals(s2);
    }
}
