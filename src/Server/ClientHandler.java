package Server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;
import Authentication.MutAuthData;

class ClientHandler extends Thread {
    private PrintWriter output;
    private BufferedReader input;
    ObjectInputStream oIn;
    ObjectOutputStream oOut;
    private FileWriter logs;
    private File logsPath = new File("src/Server/logs.txt");
    private LocalDateTime time = LocalDateTime.now();
    private Socket clientSocket;
    Client client;

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
            output = new PrintWriter(clientSocket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            client.setIpAddress(clientSocket.getInetAddress().toString());
            logs = new FileWriter(logsPath,true);
            try {
                boolean loginAttempt = login();

                if (loginAttempt) {//verifying to the server, hashed username and password
                    //-----------------------Login--------------------------------//
                    MultiClientServer.clientCount++;
                    MultiClientServer.clients.add(client.getUsername());
                    //to this client
                    output.println(" Welcome to SecureChat");
                    //to the server terminal
                    System.out.println(client.getIpAddress() + " logged in @ " + time);
                    //to the logs
                    logs.write("\n"+client.getIpAddress() + " logged in @ " + time);
                    flush();


                    //------------------end login--------------------------------//

                    //------------------sending/receiving objects----------------//

                    //------------------sending/receiving objects end------------//

                    //-----------------messaging loop----------------------------//*/
                    try {
                        String inputLine;
                        while ((inputLine = input.readLine()) != null) {
                            if (inputLine.equals("end")) {
                                MultiClientServer.clientCount--;
                                System.out.println(client.getIpAddress() + " logged out @ " + time);
                                logs.write("\n" + client.getIpAddress() + " logged out @ " + time);
                                flush();
                                break;
                            }
                            MultiClientServer.broadcast(client.getUsername(), inputLine);
                            sendMessage("Me: ",inputLine);
                            flush();
                        }
                        //-----------------messaging loop end-------------------//
                    }catch(Exception e){
                            e.getMessage();
                    }
                    //end logout
                } else {//for failed login
                    output.println(" login attempt failed");
                    failed();
                }
                /*
                   for exceptions caused by login attempts. I want to ensure the connection is dropped if a hack
                   is attempted that causes any exceptions, sort of a redundancy.
                */
            } catch (Exception e) {
                System.out.println("Error --> " + e.getMessage());
                failed();
            }
            input.close();
            output.close();
            clientSocket.close();
        }catch(Exception e){
            e.getMessage();
        }
    }

    public void sendMessage(String uname,String  msg)  {
        output.println( uname + ": " + msg);
    }

    public void sendObject(String name, MutAuthData object) throws IOException {
        oOut = new ObjectOutputStream(clientSocket.getOutputStream());
        System.out.println("Sending object");
        oOut.writeObject(object);
    }

    public boolean login()throws Exception{
        client.setUsername(input.readLine());
        for(int i = 0; i < 2; i++)
            if(MultiClientServer.clients.contains(client.getUsername())) {
                output.println("user already logged in");
                return false;
            }
        String inputStream = input.readLine();
        long hashedLogin = Long.parseLong(inputStream);
        if(checkCredentials(hashedLogin, client.getUsername()))
            return true;
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
            clientSocket.close();
            flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

