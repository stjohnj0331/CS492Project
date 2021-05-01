package Clients;

import java.io.Serializable;

public class DataTransfer implements Serializable {
    String username;
    Long nonce;
    byte[] dhPubKey;

    public DataTransfer(){}

    public DataTransfer(String username, Long nonce, byte[] dhPubKey) {
        this.username = username;
        this.nonce = nonce;
        this.dhPubKey = dhPubKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
