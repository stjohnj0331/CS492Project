package Client;
import java.net.*;
import java.io.*;


public class ChatClient {

    //for testing only
    private String username = "user1";
    private String password = "1234";
    //---------------------------------

    public static void main(String[] args) {
        
        //server info
        String hostname = "192.168.1.10";
        int port = 4135;
        //end server info

        try (Socket socket = new Socket(hostname, port)) {

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            writer.println("587104809");

            String serverMessage = reader.readLine();
            //System.out.println(input.toString());

            System.out.println(serverMessage);
            writer.close();

        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}