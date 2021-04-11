package PFS;
import java.security.*;
import javax.crypto.*;
public class DiffieHellman {

    public int getNonce(){
        SecureRandom random = new SecureRandom();
        int nonce;
        nonce = random.nextInt();
        return nonce;
    }


    public int dhHandshake(int a, int b, int g, int p){
        return (int)(Math.pow(g, a*b)%p);
    }
}
