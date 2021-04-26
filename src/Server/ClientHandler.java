package Server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class ClientHandler extends Thread {
    private PrintWriter output;
    private BufferedReader input;
    FileWriter logs;
    File logsPath = new File("src/Server/logs.txt");
    static List<Client> loggedIn = new ArrayList<>();
    LocalDateTime time = LocalDateTime.now();
    Socket clientSocket;
    Client client;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        client = new Client();
    }

    /**
     *  validates clients created by MultiClientServer by checking login info, number of clients connected
     *  and writes to both the server logs and server terminal logins/logouts and attempted logins.
     *  Gathers all of the data required for mutual authentication, perfect forward security,
     */
    @Override public void run() {
        try {
            output = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            client.setIpAddress(clientSocket.getInetAddress().toString());
            logs = new FileWriter(logsPath,true);
            try {
                boolean loginAttempt = login();
                output = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                if (loginAttempt) {//verifying to the server, hashed username and password
                    //Login--------------------------------//
                    MultiClientServer.clientCount++;

                    loggedIn.add(client);

                    output.println(" Welcome to SecureChat");
                    System.out.println(client.getIpAddress() + " logged in @ " + time);
                    logs.write("\n"+client.getIpAddress() + " logged in @ " + time);
                    flush();

                    /*
                    client connection
                    need to get nonce
                    need to establish symmetric key
                    need to get and temp store g^ab mod p
                    */
                    mutualAuth();

                    //end login----------------------------//

                    String inputLine;
                    while((inputLine = input.readLine()) != null){
                        if(inputLine.equals("end")){
                            MultiClientServer.clientCount--;
                            System.out.println(client.getIpAddress()+" logged out @ "+time);
                            logs.write("\n"+client.getIpAddress() + " logged out @ " + time);
                            flush();
                            break;
                        }
                        System.out.println(inputLine);
                        output.flush();
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
    public boolean login()throws Exception{
        client.setUsername(input.readLine());
        for(int i = 0; i < 2; i++){
            if(loggedIn.contains(client.getUsername())) {
                output.println("user already logged in");
                return false;
            }
        }

        String inputStream = input.readLine();
        long hashedLogin = Long.parseLong(inputStream);
        if(checkCredentials(hashedLogin, client.getUsername()))
            return true;
        return false;
    }

    public boolean checkCredentials(Long hashedLogin, String username) throws IOException{
        //System.out.println("entering logged in  phase");
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

    /**
     * Once two users are connected the exchange begins
     *
     * User1 -> E(User1, 1nonce, g^a mod p, key) -> server -> User2
     *
     * User1 <- server <- E(User2, 2nonce, g^b mod p, 1Nonce+1, key) <- User2)
     *
     * User1 -> E(username, 2nonce, H(1nonce+1, 2nonce+1, g^ab mod p, key)) -> server -> User2
     *
     *
     * @throws IOException
     */
    public void mutualAuth() throws IOException {
        String temp = input.readLine();
        client.nonce = Long.parseLong(temp);
        temp = input.readLine();
        client.diffHell = Long.parseLong(temp);
        PFS.Authentication.authenticate(client.username, client.nonce, client.diffHell);
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

