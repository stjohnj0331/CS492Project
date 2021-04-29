package Authentication;

import javax.crypto.KeyAgreement;

public class MutAuthData {

    String username;
    Long nonce;
    byte[] dhPublicKey;
    KeyAgreement keyAgree;
    DiffieHellman dh = new DiffieHellman();

    public MutAuthData(byte[] dhPublicKey, KeyAgreement keyAgree) {
        this.nonce = dh.CryptoSecureRand();
        this.dhPublicKey = dhPublicKey;
        this.keyAgree = keyAgree;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void deleteData(){
        username = " ";
        nonce = (long)0;
        dhPublicKey = null;
    }
}
