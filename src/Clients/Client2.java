package Clients;

import Authentication.DiffieHellman;
import Authentication.MutAuthData;
import Encryption.AES2;
import Utilities.HMAC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Base64;

public class Client2 extends JFrame implements ActionListener {
    private static String uname;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    JTextArea  taMessages;
    JTextField tfInput;
    JButton btnSend,btnExit;
    Socket client;
    static DiffieHellman dh = new DiffieHellman();
    AES2 aes = new AES2();
    static MutAuthData mydataObject = new MutAuthData();
    static DataTransfer dataObject = new DataTransfer();
    static DataTransfer theirDataObject = new DataTransfer();

    public Client2(String uname, String password, String serverName) throws Exception {
        super(uname);
        this.uname = uname;
        client = new Socket(serverName, 3000);
        oos = new ObjectOutputStream(client.getOutputStream());
        ois = new ObjectInputStream(client.getInputStream());

        //send our first data packet - login data
        oos.writeObject(dataObject);
        oos.reset();

        //start the auth data and message data thread
        new MessagesThread().start();
        buildInterface();
    }

    public static void main(String ... args) throws Exception {
        dataObject = new DataTransfer(1);
        //take username from user
        dataObject.setUsername(JOptionPane.showInputDialog(null,"Enter your username :", "Login",
                JOptionPane.PLAIN_MESSAGE));
        uname = dataObject.getUsername();
        //take password from user
        dataObject.setPassword(JOptionPane.showInputDialog(null,"Enter your password :", "Password",
                JOptionPane.PLAIN_MESSAGE));
        String serverName = "192.168.1.10";
        //create new client
        try {
            new Client2(dataObject.getUsername(), dataObject.getPassword(), serverName);
        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.println( "Error in main of client--> " + ex.getMessage());
            System.exit(1);
        }
    }

    public void actionPerformed(ActionEvent evt) {
        if ( evt.getSource() == btnExit ) {
            dataObject.setState(3);  // send end to server so that server knows to terminate connection

            try {/*----------------------clear all data from user--------------*/
                System.out.println("logging out");
                //System.out.println(dataObject.toString());
                oos.writeObject(dataObject);
                oos.reset();
                System.exit(0);
                /*----------------------clear all data from user--------------*/

            } catch (IOException e) {
                e.printStackTrace();
            }
        } if(evt.getSource() == btnSend) {
            try {
                sendMessage();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                    try {
                        sendMessage();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
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
    public void sendMessage() throws Exception {
        dataObject.setState(4);
        String line = tfInput.getText();
        dataObject.setEncryptedPayload(aes.encrypt( line, dataObject.getSessionKey()));// sends message to clientHandler by printing to outputStream

        try {
            oos.writeObject(dataObject);
            oos.reset();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        taMessages.append(uname+": "+line+"\n");
        tfInput.setText("");
    }

    // sends received messages to the chatbox
    class MessagesThread extends Thread {
        public void run() {
            int count = 0;
            try {
                while(true) {
                    theirDataObject = (DataTransfer) ois.readObject();
                    if(theirDataObject.getState() == 4 && verify(theirDataObject)) {

                        String plaintext = aes.decrypt(theirDataObject.getEncryptedPayload(), dataObject.getSessionKey());
                        taMessages.append(theirDataObject.getUsername()+":"
                                + plaintext + "\n");

                    }else if(theirDataObject.getState() == 2){
                        if(count == 1){
                            verify(theirDataObject);
                            count++;
                        }if(count == 0) {
                            mutualAuth(theirDataObject);
                            count++;
                        }
                    }
                } // end of while
            } catch(Exception ex) {ex.getMessage();}
        }
    }

    //bob
    public void mutualAuth(DataTransfer theirDataObject) throws Exception {
        //receives alice's message
        //generate bob's public key from alice's
        mydataObject = dh.DHBobPubKeyGenerator(theirDataObject.getDhPubKey());

        //generate private key
        mydataObject.setDhPrivateKey(dh.DHPrivKey(mydataObject.getKeyAgree(), theirDataObject.getDhPubKey()));


        dataObject.setDhPubKey(mydataObject.getDhPublicKey());//bobs public key
        dataObject.setNonce(mydataObject.getMyNonce());//bobs nonce
        dataObject.setUsername(uname);//bob
        dataObject.setTheirNonce(theirDataObject.getNonce()+1);//alice's incremented nonce


        String sessionKey = Base64.getEncoder().encodeToString(mydataObject.getDhPrivateKey());
        sessionKey += (mydataObject.getMyNonce()+1) + (theirDataObject.getNonce()+1) +
                theirDataObject.getUsername()+uname;
        String hashKey = "0123456789";
        byte[] hashSessionKey = HMAC.hmac2561(sessionKey, hashKey);
        dataObject.setSessionKey(createEncryptionKey(hashSessionKey));

        dataObject.setState(2);
        oos.writeObject(dataObject);
        oos.reset();
    }

    public boolean verify(DataTransfer theirDataObject){
        long newNonce = mydataObject.getMyNonce()+1;
        if(newNonce == theirDataObject.getTheirNonce()){
            if(dataObject.getSessionKey().equals(theirDataObject.getSessionKey()))
                return true;
        }
        return false;
    }

    public String createEncryptionKey(byte[] sessionKey){
        byte[] temp = new byte[sessionKey.length/2];
        for(int i = sessionKey.length/2 ; i < sessionKey.length/2 ;i++){
            temp[i] = sessionKey[i];
        }
        String key = new String(temp);
        return key;
    }
}