package Clients;

import Authentication.DiffieHellman;
import Authentication.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.lang.*;

public class Client extends JFrame implements ActionListener {
    private String uname;
    private PrintWriter pw;
    private BufferedReader br;
    private JTextArea  taMessages;
    private JTextField tfInput;
    private JButton btnSend,btnExit;
    private Socket client;
    private DiffieHellman dh = new DiffieHellman();
    private MutAuthData mydataObject;
    private MutAuthData theirDataObject;
    private boolean start = false;

    public Client(String uname, String password, String serverName) throws Exception {
        super(uname);
        client  = new Socket(serverName,3000);
        br = new BufferedReader( new InputStreamReader( client.getInputStream()) ) ;
        pw = new PrintWriter(client.getOutputStream(),true);

        System.out.println("Authenticating to server");
        //-----------------Clients to server authentication------------------------------//
        pw.println(uname);
        pw.println(password);


        new DataThread().start();//waiting for start signal
        //System.out.println("2 clients connected");
        /*
            //-----------------Symmetric key creation and distribution----------------------//

            //-----------------generating public mutual authentication info-----------------//
            mydataObject = dh.DHKeyGenerator(theirDataObject.getDhPublicKey());
            mydataObject.setTheirNonce(theirDataObject.getTheirNonce() + 1);
            //-----------------receiving public mutual authentication info -----------------//
            new DataThreadReceive().start();
            //-----------------sending public mutual authentication info -------------------//
            new DataThreadSend().start();
            if ((mydataObject.getMyNonce() + 1) == theirDataObject.getMyNonce()) {
                //-----------------generating private mutual authentication info----------------//
                mydataObject.setDhPrivateKey(dh.buildKey(mydataObject.getKeyAgree(), mydataObject.getDhPublicKey()));
                //----------------------------------Authenticate--------------------------------//

                //-----------------if authenticated, build out messenger interface -------------//
*/
            buildInterface();
            new MessagesThread().start();//needs to be encrypted
    }

    public void actionPerformed(ActionEvent evt) {
        if ( evt.getSource() == btnExit ) {
            pw.println("end");  // send end to server so that server knows to terminate connection
            System.exit(0);
        } if(evt.getSource() == btnSend) {
            pw.println(tfInput.getText());// sends message to clientHandler by printing to outputStream
            tfInput.setText("");
        }
    }
    public void buildInterface() {
        btnSend = new JButton("Send");
        btnExit = new JButton("Exit");
        taMessages = new JTextArea();
        taMessages.setRows(10);
        taMessages.setColumns(50);
        taMessages.setEditable(false);
        tfInput  = new JTextField(50);
        tfInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    pw.println(tfInput.getText());
                    tfInput.setText("");
                }
            }
        });
        JScrollPane sp = new JScrollPane(taMessages, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(sp,"Center");
        JPanel bp = new JPanel( new FlowLayout());
        bp.add(tfInput);
        bp.add(btnSend);
        bp.add(btnExit);
        add(bp,"South");
        btnSend.addActionListener(this);
        btnExit.addActionListener(this);
        setLocationRelativeTo(null);
        /*
           Disabled close button for now so that I can control shutdown operations for clients such as the
           disposal of the DH key and hashed session key.
         */
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(500,300);
        setVisible(true);
        pack();
    }

    public static void main(String ... args) {
        //take username from user
        String name = JOptionPane.showInputDialog(null,"Enter your username :", "Login",
                JOptionPane.PLAIN_MESSAGE);
        //take password from user
        String password = JOptionPane.showInputDialog(null,"Enter your password :", "Password",
                JOptionPane.PLAIN_MESSAGE);
        String serverName = "192.168.1.10";
        try {
            new Client( name, password, serverName);
        } catch(Exception ex) {
            System.out.println( "Error --> " + ex.getMessage());
        }
    }
    class DataThread extends Thread{
            public void run () {
                System.out.println("yay");
            String line;
            int count = 0;
            try {
                while (count == 0) {
                    line = br.readLine();
                    if (line.equals("start")) {
                        start = true;
                        count++;
                    }
                }
                System.out.println("server said to start");
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

    }
    // sends received messages to the chatbox
    class MessagesThread extends Thread {
        public void run() {
            String line;
            try {
                while(true) {
                    line = br.readLine();
                    taMessages.append(line + "\n");
                } // end of while
            } catch(Exception ex) {ex.getMessage();}
        }
    }
}