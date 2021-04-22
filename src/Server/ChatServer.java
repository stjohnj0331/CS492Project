package Server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.time.LocalTime;

public class ChatServer {

    //server settings
    private int currentTot;
    ServerSocket serverSocket;
    Socket client;
    int bytesRead;
    BufferedReader input;
    PrintWriter output;
    FileWriter logs;
    File logsPath = new File("src/Server/logs.txt");
    private final int port = 4135;
    LocalTime time = LocalTime.now();
    //end server settings
    /**
     * logs will be removed for final implementation
     * they are temporary.
     * @throws IOException
     */
    public void start()throws IOException{
        System.out.println("Server Listening on port "+port);
        logs = new FileWriter(logsPath,true);
        serverSocket =  new ServerSocket(port);
        while(true) {

            client = serverSocket.accept();
            output = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
            PrintWriter outputWriter = new PrintWriter(output, true);
            String address = client.getInetAddress().toString();

            try {

                boolean loginAttempt = loginInfo();
                output = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

                if (loginAttempt) {
                    System.out.println(address + " logged in @ " + time.toString());
                    outputWriter.println(" Welcome to SecureChat");
                    logs.write("\n"+address + " logged in @ " + time.toString());
                /*
                client handoff
                 */
                } else {
                    System.out.println(address + " attempted to log in @ " + time.toString());
                    outputWriter.println(" login attempt failed");
                    logs.write("\n"+address + " attempted to log in @ " + time.toString());
                    client.close();
                }
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println(address + " attempted to log in @ " + time.toString());
                outputWriter.println(" login attempt failed");
                logs.write("\n"+address + " attempted to log in @ " + time.toString());
                client.close();
            }
            output.flush();
            logs.flush();
        }
    }

    public boolean loginInfo()throws Exception{
        input = new BufferedReader(new InputStreamReader(client.getInputStream()));

        String inputStream = input.readLine();
        //expecting hashed username and password from client to check against the login file
        long hashedLogin = Long.parseLong(inputStream);

        if(validateLogin(hashedLogin))
            return true;
        return false;
    }


    public boolean validateLogin(Long hashedLogin) throws IOException{
        File login = new File("src/Server/login.txt");
        Scanner reader = new Scanner(login);
        boolean state = false;
        while(reader.hasNextLine()){
            String fileIn = reader.nextLine();
            Long loginOnFile = Long.parseLong(fileIn);
            //System.out.println("PW on file: "+loginOnFile+"\nInc PW: "+hashedLogin);
            if(loginOnFile.equals(hashedLogin)){
                state = true;
            }
        }
        //System.out.println(state);
        return state;
    }


    public static void main(String[] args){
        ChatServer server = new ChatServer();
        try{
            server.start();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }
}