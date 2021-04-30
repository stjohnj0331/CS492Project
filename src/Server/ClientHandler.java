package Server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;

class ClientHandler extends Thread {
    private PrintWriter output;
    private BufferedReader input;
    InputStream is;
    OutputStream os;
    ObjectInputStream ois;
    ObjectOutputStream oos;
    private FileWriter logs;
    private File logsPath = new File("src/Server/logs.txt");
    private LocalDateTime time = LocalDateTime.now();
    private Socket clientSocket;
    Client client;
    MultiClientServer server = new MultiClientServer();

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        client = new Client();
        start();
    }

    /**
     *  validates clients created by MultiClientServer by checking login info, number of clients connected
     *  and writes to both the server logs and server terminal logins/logouts and attempted logins.
     *  Gathers all of the data required for mutual authentication, perfect forward security, and symmetric
     *  key encryption
     */
    @Override public void run() {
        try {
            is = clientSocket.getInputStream();
            os = clientSocket.getOutputStream();
            output = new PrintWriter(os, true);
            input = new BufferedReader(new InputStreamReader(is));
            client.setIpAddress(clientSocket.getInetAddress().toString());
            logs = new FileWriter(logsPath,true);
            try {
                boolean loginAttempt = login();

                if (loginAttempt) {//verifying to the server, hashed username and password

                    //-----------------messaging loop begins-------------------//
                    //output.println(" Welcome to SecureChat");
                    //to the server terminal
                    System.out.println(client.getIpAddress() + " logged in @ " + time);
                    //to the logs
                    logs.write("\n"+client.getIpAddress() + " logged in @ " + time);
                    flush();
                    try {
                        String inputLine;
                        while ((inputLine = input.readLine()) != null) {
                            if (inputLine.equals("end")) {
                                MultiClientServer.decreaseClientCount();
                                MultiClientServer.removeUser(this.client.getUsername());
                                System.out.println(client.getIpAddress() + " logged out @ " + time);
                                logs.write("\n" + client.getIpAddress() + " logged out @ " + time);
                                flush();
                                break;
                            }else {
                                if(inputLine.equals("start")){
                                    MultiClientServer.broadcast1(client.getUsername(), inputLine);
                                }else {
                                    MultiClientServer.broadcast(client.getUsername(), inputLine);//sends message to server
                                    sendMessage("Me: ", inputLine);//prints message to this users chatbox
                                }
                                flush();
                            }
                        }
                        //-----------------messaging loop end-------------------//
                    }catch(Exception e){
                        MultiClientServer.decreaseClientCount();
                        e.getMessage();
                    }
                } else {//for failed login
                    output.println(" login attempt failed");
                    failed();
                }
                /*
                   for exceptions caused by login attempts. I want to ensure the connection is dropped if a hack
                   is attempted that causes any exceptions, sort of a redundancy.
                */
            } catch (Exception e) {
                System.out.println("Error in client handler--> " + e.getMessage());
                e.printStackTrace();
                failed();
            }
            input.close();
            output.close();
            clientSocket.close();
        }catch(Exception e){
            e.getMessage();
        }
    }

    public void sendMessage1(String msg){output.println(msg);}
    public void sendMessage(String uname, String  msg)  { output.println( uname + ": " + msg); }

    public boolean login()throws Exception{
        client.setUsername(input.readLine());//sets username for client
        for(int i = 0; i < 2; i++) {
            if ((!MultiClientServer.loggedIn.isEmpty()) && i < MultiClientServer.getClientCount() &&
                    MultiClientServer.loggedIn.get(i).client.getUsername().equals(client.getUsername())){
                output.println("user already logged in");
                return false;
            }
        }
        String password = input.readLine();
        long hashedLogin = Long.parseLong(password);
        if(checkCredentials(hashedLogin, client.getUsername())) {
            MultiClientServer.loggedIn.add(this);
            MultiClientServer.increaseClientCount();
            return true;
        }
        return false;
    }

    public boolean checkCredentials(Long hashedLogin, String username) throws IOException{
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

    /* Utility functions that just serve to clean up my code*/
    public void flush(){
        try {
            logs.flush();
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void failed(){
        try {
            System.out.println(client.getIpAddress() + " attempted login @ " + time);
            logs.write("\n"+client.getIpAddress() + " attempted login @ " + time);
            server.decreaseClientCount();
            clientSocket.close();
            flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}