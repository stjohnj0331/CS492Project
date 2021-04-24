package Server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class ClientHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter output;
    private BufferedReader input;
    FileWriter logs;
    File logsPath = new File("src/Server/logs.txt");
    List<String> loggedIn = new ArrayList<>();
    String username;
    LocalDateTime time = LocalDateTime.now();
    String address;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override public void run() {
        try {
            output = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            address = clientSocket.getInetAddress().toString();
            logs = new FileWriter(logsPath,true);
            try {
                boolean loginAttempt = login();
                output = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                if (loginAttempt) {
                    //Login
                    MultiClientServer.clientCount++;
                    loggedIn.add(username);
                    output.println(" Welcome to SecureChat");
                    System.out.println(address + " logged in @ " + time);
                    logs.write("\n"+address + " logged in @ " + time);
                    output.flush();
                    logs.flush();
                    //end login



                    /*
                    client handoff
                    */



                    //Logout
                    String inputLine;
                    while((inputLine = input.readLine()) != null){
                        if(inputLine.equals("end")){
                            MultiClientServer.clientCount--;
                            System.out.println(clientSocket.getInetAddress()+" logged out @ "+time);
                            logs.write("\n"+address + " logged out @ " + time);
                            logs.flush();
                            break;
                        }
                        System.out.println(inputLine);
                        output.flush();
                    }
                    //end logout
                } else {//for failed login
                    System.out.println(address + " attempted login @ " + time);
                    logs.write("\n"+address + " attempted login @ " + time);
                    output.println(" login attempt failed");
                    clientSocket.close();
                    output.flush();
                    logs.flush();
                }
                /*
                   for exceptions caused by login attempts. I want to ensure the connection to be dropped if a hack
                   is attempted that causes these exceptions.
                */
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(address + " attempted login @ " + time);
                logs.write("\n"+address + " attempted login @ " + time);
                clientSocket.close();
                output.flush();
                logs.flush();
            }

            input.close();
            output.close();
            clientSocket.close();
        }catch(Exception e){
            e.getMessage();
        }
    }
    public boolean login()throws Exception{
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        //expecting hashed username and password from client to check against the login file
        username = input.readLine();
        for(int i = 0; i < 2; i++){
            if(loggedIn.contains(username)) {
                output.println("user already logged in");
                return false;
            }
        }

        String inputStream = input.readLine();
        long hashedLogin = Long.parseLong(inputStream);
        if(loggedIn(hashedLogin, username))
            return true;
        return false;
    }


    public boolean loggedIn(Long hashedLogin, String username) throws IOException{
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
}

