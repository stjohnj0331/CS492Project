package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class MultiClientServer {
    private ServerSocket serverSocket;
    static int port = 4135;
    //end server settings

    /**
     *
     * @throws IOException
     */
    public void start(int port) throws IOException {
        System.out.println("Server Listening on port "+port);
        serverSocket = new ServerSocket(port);
        while (true)
            new ClientHandler(serverSocket.accept()).start();
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    /**
     *
     */
    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter output;
        private BufferedReader input;
        FileWriter logs;
        File logsPath = new File("src/Server/logs.txt");
        List<String> loggedIn = new ArrayList<>();

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        /**
         *
         */
        @Override public void run() {
            try {
                output = new PrintWriter(clientSocket.getOutputStream(), true);
                input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String address = clientSocket.getInetAddress().toString();
                logs = new FileWriter(logsPath,true);
                LocalTime time = LocalTime.now();
                try {
                    boolean loginAttempt = loginInfo();
                    output = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                    if (loginAttempt) {
                        System.out.println(address + " logged in @ " + time.toString());
                        output.println(" Welcome to SecureChat");
                        logs.write("\n"+address + " logged in @ " + time.toString());
                    /*
                    client handoff
                    */
                        String inputLine;
                        while((inputLine = input.readLine()) != null){
                            if(inputLine.equals("end")){
                                System.out.println("client "+clientSocket.getInetAddress()+" has logged out");
                                break;
                            }
                            System.out.println(inputLine);
                        }
                    } else {
                        System.out.println(address + " attempted to log in @ " + time.toString());
                        output.println(" login attempt failed");
                        logs.write("\n"+address + " attempted to log in @ " + time.toString());
                        clientSocket.close();
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    System.out.println(address + " attempted to log in @ " + time.toString());
                    output.println(" login attempt failed");
                    logs.write("\n"+address + " attempted to log in @ " + time.toString());
                    clientSocket.close();
                }

                input.close();
                output.close();
                clientSocket.close();
            }catch(Exception e){
                e.getMessage();
            }
        }
        public boolean loginInfo()throws Exception{
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args){
        MultiClientServer server = new MultiClientServer();
        try{
            server.start(port);
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

}
