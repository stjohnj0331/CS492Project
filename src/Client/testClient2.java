package Client;

import PFS.DiffieHellman;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.lang.*;

public class  testClient2 extends JFrame implements ActionListener {
    String uname;
    PrintWriter pw;
    BufferedReader br;
    JTextArea  taMessages;
    JTextField tfInput;
    JButton btnSend,btnExit;
    Socket client;
    DiffieHellman cybSecTools = new DiffieHellman();

    public testClient2(String uname, String password, String serverName) throws Exception {
        super(uname);
        this.uname = uname;
        client  = new Socket(serverName,3000);
        br = new BufferedReader( new InputStreamReader( client.getInputStream()) ) ;
        pw = new PrintWriter(client.getOutputStream(),true);

        //-----------------sending mutual authentication info---------------------------//
        /*
        username and password will change to a hashed value
         */
        pw.println(uname);  // send name to server
        pw.println(password);
        pw.println(cybSecTools.CryptoSecureRand());//nonce
        pw.println("5678");//diffHell
        //-----------------sending mutual authentication info END-----------------------//

        buildInterface();
        new MessagesThread().start();
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

    public void actionPerformed(ActionEvent evt) {
        if ( evt.getSource() == btnExit ) {
            pw.println("end");  // send end to server so that server knows about the termination
            System.exit(0);
        } if(evt.getSource() == btnSend) {
            // send message to server
            pw.println(tfInput.getText());
            tfInput.setText("");
        }
    }

    public static void main(String ... args) {

        // take username from user
        String name = JOptionPane.showInputDialog(null,"Enter your username :", "Login",
                JOptionPane.PLAIN_MESSAGE);
        String password = JOptionPane.showInputDialog(null,"Enter your password :", "Password",
                JOptionPane.PLAIN_MESSAGE);
        String serverName = "192.168.1.10";
        try {
            new testClient( name, password, serverName);
        } catch(Exception ex) {
            System.out.println( "Error --> " + ex.getMessage());
        }

    }

    // inner class for Messages Thread
    class  MessagesThread extends Thread {
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