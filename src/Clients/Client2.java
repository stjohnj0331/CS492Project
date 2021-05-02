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

public class Client2 extends JFrame implements ActionListener {
    private static String uname;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    JTextArea  taMessages;
    JTextField tfInput;
    JButton btnSend,btnExit;
    Socket client;
    static DiffieHellman dh = new DiffieHellman();
    static MutAuthData mydataObject;
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
        mydataObject = dh.DHPubKeyGenerator();//private object
        dataObject = new DataTransfer(1);
        //take username from user
        dataObject.setUsername(JOptionPane.showInputDialog(null,"Enter your username :", "Login",
                JOptionPane.PLAIN_MESSAGE));
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
            try {
                System.out.println("logging out");
                //System.out.println(dataObject.toString());
                oos.writeObject(dataObject);
                oos.reset();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } if(evt.getSource() == btnSend) {
            dataObject.setState(4);
            dataObject.setMessage(tfInput.getText());// sends message to clientHandler by printing to outputStream
            try {
                oos.writeObject(dataObject);
                oos.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
            tfInput.setText("");
            taMessages.append(uname+": "+dataObject.getMessage()+"\n");
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
                    dataObject.setState(4);
                    dataObject.setMessage(tfInput.getText());// sends message to clientHandler by printing to outputStream
                    try {
                        oos.writeObject(dataObject);
                        oos.reset();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    tfInput.setText("");
                    taMessages.append(uname+": "+dataObject.getMessage()+"\n");
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
           int count = 0;
            try {
                while(true) {
                    theirDataObject = (DataTransfer) ois.readObject();
                    if(theirDataObject.getState() == 4) {
                        taMessages.append(theirDataObject.getUsername()+":"
                                +theirDataObject.getMessage() + "\n");
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
        System.out.println("generating public key");
        //generate bob's public key from alice's
        mydataObject = dh.DHPubKeyGenerator(theirDataObject.getDhPubKey());//private object
        //generate secret key for hash
        mydataObject.setDhPrivateKey(dh.DHPrivKey(mydataObject.getKeyAgree(), mydataObject.getDhPublicKey()));//private object


        dataObject.setDhPubKey(mydataObject.getDhPublicKey());//bobs public key
        dataObject.setNonce(mydataObject.getMyNonce());//bobs nonce
        dataObject.setUsername(uname);//bob
        dataObject.setTheirNonce(theirDataObject.getNonce()+1);//alice's incremented nonce
        dataObject.setState(2);
        System.out.println("alice's nonce "+dataObject.getTheirNonce());
        System.out.println("sending bob's key");
        oos.writeObject(dataObject);
        oos.reset();
    }

    public void verify(DataTransfer theirDataObject){
        long newNonce = mydataObject.getMyNonce()+1;
        System.out.println("checking nonce");
        System.out.println(newNonce +"\n"+theirDataObject.getTheirNonce());
        if(newNonce == theirDataObject.getTheirNonce()){
            System.out.println("Bob has verified Alice");

        }
    }
}





























