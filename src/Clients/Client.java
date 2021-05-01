package Clients;

import Authentication.DiffieHellman;
import Authentication.MutAuthData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;

public class Client extends JFrame implements ActionListener {
    String uname;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    PrintWriter pw;
    BufferedReader br;
    JTextArea  taMessages;
    JTextField tfInput;
    JButton btnSend,btnExit;
    Socket client;
    DiffieHellman dh = new DiffieHellman();
    MutAuthData mydataObject;
    MutAuthData theirDataObject;
    DataTransfer dataObject;
    static int wait = 0;

    public Client(String uname, String password, String serverName) throws Exception {
        super(uname);
        this.uname = uname;
        client = new Socket(serverName, 3000);
        oos = new ObjectOutputStream(client.getOutputStream());
        ois = new ObjectInputStream(client.getInputStream());


        //get authentication data
        mydataObject = dh.DHPubKeyGenerator();
        dataObject = new DataTransfer(uname, dh.CryptoSecureRand(), mydataObject.getDhPublicKey());
        oos.writeObject(dataObject);
        //-----------------Symmetric key creation and distribution----------------------//
        //-----------------generating public mutual authentication info-----------------//
        //mydataObject = dh.DHPubKeyGenerator(mydataObject.getDhPublicKey())DataObject.getDhPublicKey());
        //mydataObject.setTheirNonce(theirDataObject.getTheirNonce() + 1);
        //-----------------receiving public mutual authentication info -----------------///
        //-----------------sending public mutual authentication info -------------------///
        //if ((mydataObject.getMyNonce() + 1) == theirDataObject.getMyNonce()) {
        //-----------------generating private mutual authentication info----------------//
        //mydataObject.setDhPrivateKey(dh.buildKey(mydataObject.getKeyAgree(), mydataObject.getDhPublicKey()));
        //----------------------------------Authenticate--------------------------------//

        //-----------------if authenticated, build out messenger interface -------------//

        //}

        //now get the rest
        br = new BufferedReader(new InputStreamReader(client.getInputStream()));
        pw = new PrintWriter(client.getOutputStream(), true);
        pw.println(uname);
        pw.println(password);


        new MessagesThread().start();//needs to be encrypted
        buildInterface();

    }
    public static void main(String ... args) {
        //take username from user
        String name = JOptionPane.showInputDialog(null,"Enter your username :", "Login",
                JOptionPane.PLAIN_MESSAGE);
        //take password from user
        String password = JOptionPane.showInputDialog(null,"Enter your password :", "Password",
                JOptionPane.PLAIN_MESSAGE);
        String serverName = "192.168.1.10";
        //create new client
        try {
            new Client( name, password, serverName);
        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.println( "Error in main of client--> " + ex.getMessage());
            System.exit(1);
        }
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

    // sends received messages to the chatbox
    class MessagesThread extends Thread {
        public void run() {
            String line;
            try {
                while(true) {
                    line = br.readLine();
                    if(line.equals("start")&&(wait == 0)) {
                        System.out.println("response received");
                        wait++;
                    }if(wait >= 1)
                        taMessages.append(line + "\n");
                } // end of while
            } catch(Exception ex) {ex.getMessage();}
        }
    }
}