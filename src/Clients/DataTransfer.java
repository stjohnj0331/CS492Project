package Clients;

import java.io.Serializable;
import java.util.Arrays;

public class DataTransfer implements Serializable {
    String username;
    int state;
    String encryptedPayload;
    String sessionKey;
    Long nonce;
    Long theirNonce;
    byte[] dhPubKey;
    byte[] dhPrivKey;
    String message;
    String password;

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public byte[] getDhPrivKey() {
        return dhPrivKey;
    }

    public void setDhPrivKey(byte[] dhPrivKey) {
        this.dhPrivKey = dhPrivKey;
    }

    public String getEncryptedPayload() {
        return encryptedPayload;
    }

    public void setEncryptedPayload(String encryptedPayload) {
        this.encryptedPayload = encryptedPayload;
    }

    public DataTransfer(){}

    public DataTransfer(int state){this.state = state;}

    public Long getTheirNonce() {
        return theirNonce;
    }

    public void setTheirNonce(Long theirNonce) {
        this.theirNonce = theirNonce;
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
        nonce = null;
        theirNonce = null;
        dhPubKey = null;
        dhPrivKey = null;
        password = null;
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


