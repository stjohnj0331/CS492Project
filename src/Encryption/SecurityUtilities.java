package Encryption;

public class SecurityUtilities {

    public static int hash(String usernameAndPassword){
        return usernameAndPassword.hashCode();
    }
}
