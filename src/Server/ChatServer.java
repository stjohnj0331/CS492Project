package Server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.time.LocalTime;

public class ChatServer {

    //server settings
    private ServerSocket serverSocket;
    Socket client;
    BufferedReader input;
    PrintWriter output;
    FileWriter logs;
    File logsPath = new File("src/Server/logs.txt");
    private final int port = 4135;
    LocalTime time = LocalTime.now();
    List<String> loggedIn = new ArrayList<>();
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
                    String inputLine;
                    while((inputLine = input.readLine()) != null){
                        if(inputLine.equals("end")){
                            System.out.println("client "+client.getInetAddress()+" has logged out");
                            break;
                        }
                        System.out.println(inputLine);
                    }
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
        //expecting hashed username and password from client to check against the login file
        String username = input.readLine();
        for(int i = 0; i < 2; i++){
            if(loggedIn.contains(username)) {
                output.println("user already logged in");
                return false;
            }
        }
        loggedIn.add(username);
        String inputStream = input.readLine();
        long hashedLogin = Long.parseLong(inputStream);
        if(validateLogin(hashedLogin, username))
            return true;
        return false;
    }


    public boolean validateLogin(Long hashedLogin, String username) throws IOException{
        File login = new File("src/Server/login.txt");
        Scanner reader = new Scanner(login);
        boolean state = false;
        while(reader.hasNextLine()){
            String nextLine = reader.nextLine();
            if(nextLine.equals(username)) {
                nextLine = reader.nextLine();
                Long loginOnFile = Long.parseLong(nextLine);
                if (loginOnFile.equals(hashedLogin)) {
                    state = true;
                }
            }else{
                reader.nextLine();
            }
        }
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