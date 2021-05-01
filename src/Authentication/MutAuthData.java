package Authentication;

import javax.crypto.KeyAgreement;

public class MutAuthData{

    private String username;
    private Long myNonce;
    private Long theirNonce;
    private byte[] dhPublicKey;
    private byte[] dhPrivateKey;
    private KeyAgreement keyAgree;
    private DiffieHellman dh = new DiffieHellman();

    public MutAuthData(byte[] dhPublicKey, KeyAgreement keyAgree) {
        this.dhPublicKey = dhPublicKey;
        this.keyAgree = keyAgree;
        myNonce = dh.CryptoSecureRand();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getDhPublicKey() {
        return dhPublicKey;
    }

    public void setDhPublicKey(byte[] dhPublicKey) {
        this.dhPublicKey = dhPublicKey;
    }

    public byte[] getDhPrivateKey() {
        return dhPrivateKey;
    }

    public void setDhPrivateKey(byte[] dhPrivateKey) {
        this.dhPrivateKey = dhPrivateKey;
    }

    public KeyAgreement getKeyAgree() {
        return keyAgree;
    }

    public void setKeyAgree(KeyAgreement keyAgree) {
        this.keyAgree = keyAgree;
    }
    public Long getMyNonce(){return myNonce;}

    public Long getTheirNonce() {
        return theirNonce;
    }

    public void setTheirNonce(Long theirNonce) {
        this.theirNonce = ++theirNonce;
    }

    public void deleteData(){
        username = " ";
        myNonce = (long)0;
        theirNonce = (long)0;
        dhPublicKey = null;
        dhPrivateKey = null;
    }
}
