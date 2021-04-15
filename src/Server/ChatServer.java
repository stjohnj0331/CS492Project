package Server;

import Connect.Connection;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    private int currentTot;
    ServerSocket serverSocket;
    Socket client;
    int bytesRead;
    Connection c = new Connection();
    BufferedReader input;
    PrintWriter output;
    File logs = new File("logs.txt");


    public void start()throws IOException{
        System.out.println("Connection starting on port "+c.getPORT());

        serverSocket =  new ServerSocket(c.getPORT());

        client = serverSocket.accept();

        try{
            boolean loginAttempt = loginInfo();
            if(loginAttempt){
                /*
                this is where I will either start a thread to connect the two clients or wait for the other client
                to connect and exchange ip and port info to allow for clients to create connection.
                 */
            }else{
                client.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean loginInfo()throws Exception{
        input = new BufferedReader(new InputStreamReader(client.getInputStream()));

        String username = input.readLine();
        String password = input.readLine();

        output = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
        int hashedLogin = Encryption.SecurityUtilities.hash("username"+"password");

        if(validateLogin(hashedLogin))
            return true;
        return false;
    }


    /**
     * Server currently just listens on the specified port, prints that a new client has connected
     * prints a message from the client, and sends the server time to the client
     * @param args
     */
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        try{
            while(true){
                server.start();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean validateLogin(int hashedLogin) throws IOException{
        File login = new File("login.txt");
        Scanner reader = new Scanner(login);
        while(reader.hasNextLine()){
            String fileIn = reader.nextLine();
            int loginOnFile = Integer.parseInt(fileIn);
            if(loginOnFile == hashedLogin){
                return true;
            }
        }
        return false;
    }

}