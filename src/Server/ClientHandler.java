package Server;

import Authentication.MutAuthData;
import Clients.DataTransfer;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;

class ClientHandler extends Thread {
    InputStream is;
    OutputStream os;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    private FileWriter logs;
    private File logsPath = new File("src/Server/logs.txt");
    private LocalDateTime time = LocalDateTime.now();
    private Socket clientSocket;
    DataTransfer dataObject;
    Client client;
    MutAuthData myObject;
    MultiClientServer server = new MultiClientServer();
    boolean loginAttempt;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        client = new Client();
        start();
    }

    /**
     *  validates clients created by MultiClientServer by checking login info, number of clients connected
     *  and writes to both the server logs and server terminal logins/logouts and attempted logins.
     *  Gathers all of the data required for mutual authentication, perfect forward security, and symmetric
     *  key encryption.
     */
    @Override public void run() {
        try {
            is = clientSocket.getInputStream();
            os = clientSocket.getOutputStream();
            oos = new ObjectOutputStream(os);
            ois = new ObjectInputStream(is);
            client.setIpAddress(clientSocket.getInetAddress().toString());
            logs = new FileWriter(logsPath, true);
            try {
                dataObject = new DataTransfer();
                dataObject = (DataTransfer) ois.readObject();
                if (dataObject.getState() == 1) {//login()
                    System.out.println("login packet");
                    loginAttempt = login(dataObject);
                }
                if (loginAttempt) {//verifying to the server, hashed username and password
                    System.out.println("Login successful");
                    //to the server terminal
                    System.out.println(client.getIpAddress() + " logged in @ " + time);
                    //to the logs
                    logs.write("\n" + client.getIpAddress() + " logged in @ " + time);
                    logs.flush();
                    //-----------------messaging/data transfer loop begins-------------------//
                    while (dataObject.getState() != 3) {
                        dataObject = (DataTransfer) ois.readObject();
                        System.out.println(dataObject.toString());//good to hear
                        //get in object check its state
                        if (dataObject.getState() == 2) {//mutAuth/PFS - needs to be broadcast
                            System.out.println("this is mutAuth/PFS");
                            MultiClientServer.broadcast(dataObject.getUsername(), dataObject);
                        }
                        if (dataObject.getState() == 4) {//message - needs to be broadcast
                            System.out.println("broadcasting message");
                            MultiClientServer.broadcast(dataObject.getUsername(), dataObject);
                        }
                    }
                    //-----------------messaging/data transfer loop ends--------------------//

                    if (dataObject.getState() == 3) {//logout
                        System.out.println("logging out");
                        for (int i = 0; i < 2; i++)
                            if ((!MultiClientServer.loggedIn.isEmpty()) && i < MultiClientServer.getClientCount() &&
                                    MultiClientServer.loggedIn.get(i).client.getUsername().equals(client.getUsername())) {
                                MultiClientServer.loggedIn.remove(i);
                                System.out.println(client.getIpAddress() + " logged out @ " + time);
                                logs.write("\n" + client.getIpAddress() + " logged out @ " + time);
                                logs.flush();
                            }
                        System.out.println("closing socket");
                        ois.close();
                        oos.close();
                        clientSocket.close();
                    }
                } else{//for failed login
                    failed();
                }
            } catch(Exception e) {
                System.out.println("Error in outer try of client handler\\login attempt");
                e.printStackTrace();
            }
        }catch(Exception ex){
            System.out.println("error in outer try of client handler");
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public void sendObject(String user, DataTransfer object) throws IOException {oos.writeObject(object);}

    public boolean login(DataTransfer dataObject)throws Exception{
        client.setUsername(dataObject.getUsername());//sets username for client
        for(int i = 0; i < 2; i++) {
            if ((!MultiClientServer.loggedIn.isEmpty()) && i < MultiClientServer.getClientCount() &&
                    MultiClientServer.loggedIn.get(i).client.getUsername().equals(client.getUsername())){
                return false;
            }
        }
        long hashedLogin = Long.parseLong(dataObject.getPassword());
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
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void failed(){
        try {
            System.out.println("login failed" +client.getIpAddress() + " attempted login @ " + time);
            logs.write("\n"+client.getIpAddress() + " attempted login @ " + time);
            server.decreaseClientCount();
            clientSocket.close();
            flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}