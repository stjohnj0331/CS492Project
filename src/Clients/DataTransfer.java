package Clients;

import java.io.Serializable;
import java.util.Arrays;

public class DataTransfer implements Serializable {
    String username;
    String password;
    Long nonce;
    Long theirNonce;
    byte[] dhPubKey;
    String message;
    int state;
    boolean read;


    public DataTransfer(){}

    public DataTransfer(int state){this.state = state;}

    public DataTransfer(String username, Long nonce, byte[] dhPubKey, int state) {
        this.username = username;
        this.nonce = nonce;
        this.dhPubKey = dhPubKey;
        this.state = state;
    }

    public Long getTheirNonce() {
        return theirNonce;
    }

    public void setTheirNonce(Long theirNonce) {
        this.theirNonce = theirNonce;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    public byte[] getDhPubKey() {
        return dhPubKey;
    }

    public void setDhPubKey(byte[] dhPubKey) {
        this.dhPubKey = dhPubKey;
    }

    public void reset(){
         username = null;
         password = null;
         nonce = null;
         dhPubKey = null;
         message = null;
         state = 0;
         read = false;
    }

    @Override
    public String toString() {
        return "DataTransfer{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nonce=" + nonce +
                ", dhPubKey=" + Arrays.toString(dhPubKey) +
                ", message='" + message + '\'' +
                ", state=" + state +
                '}';
    }
}


