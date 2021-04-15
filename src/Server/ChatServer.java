package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    private int currentTot;
    ServerSocket serverSocket;
    Socket client;
    int bytesRead;
    BufferedReader input;
    PrintWriter output;
    File logsPath = new File("src/Server/logs.txt");
    PrintWriter logs;
    private int port = 4135;


    public void start()throws IOException{
        System.out.println("Connection starting on port "+port);
        logs = new PrintWriter(logsPath);
        serverSocket =  new ServerSocket(port);

        client = serverSocket.accept();

        try{
            boolean loginAttempt = loginInfo();
            if(loginAttempt){
                /*
                client handoff
                 */
                System.out.println("login successful");
            }else{
                client.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean loginInfo()throws Exception{
        input = new BufferedReader(new InputStreamReader(client.getInputStream()));
        output = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

        String inputStream = input.readLine();
        //expecting hashed username and password from client to check against the login file
        int hashedLogin = Integer.parseInt(inputStream);

        if(validateLogin(hashedLogin))
            return true;
        return false;
    }


    public boolean validateLogin(int hashedLogin) throws IOException{
        File login = new File("/login.txt");
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


    public static void main(String[] args){
        ChatServer server = new ChatServer();
        try{
            while(true){
                server.start();
            }

        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }
}