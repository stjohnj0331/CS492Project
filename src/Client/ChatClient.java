package Client;
import java.net.*;
import java.io.*;


public class ChatClient {

    //---------------------------------

    public static void main(String[] args) {
        
        //server info
        String hostname = "73.253.7.146";
        int port = 3000;
        //end server info

        try (Socket socket = new Socket(hostname, port)) {

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println("Thisisuser1");
            writer.println("-559118235");
            //writer.println("testing from client");

            String serverMessage = reader.readLine();
            //System.out.println(input.toString());

            System.out.println(serverMessage);

            writer.println("end");
            writer.close();
            System.exit(1);

        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}