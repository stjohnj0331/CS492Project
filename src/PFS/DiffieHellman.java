package PFS;

public class DiffieHellman {
    public int dhHandshake(int a, int b, int g, int p){
        return (int)(Math.pow(g, a*b)%p);
    }
}
