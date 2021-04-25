package Server;

import java.net.Socket;

public class Client {

    String username;
    Long nonce;
    Long diffHell;
    String ipAddress;

    public String getUsername() {
        return username;
    }

    public Long getNonce() {
        return nonce;
    }

    public Long getDiffHell() {
        return diffHell;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    public void setDiffHell(Long diffHell) {
        this.diffHell = diffHell;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
