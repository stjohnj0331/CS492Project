package Client;
import java.net.*;
import java.io.*;
import java.security.*;


public class ChatClient {

    //for testing only
    private static String username = "user1";
    private static String password = "1234";
    //---------------------------------

    public static void main(String[] args) {
        
        //server info
        String hostname = "24.62.3.181";
        int port = 443;
        //end server info

        try (Socket socket = new Socket(hostname, port)) {

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            
            //access token: password hash in the future
            writer.println(password);

            String time = reader.readLine();

            System.out.println(time);


        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
