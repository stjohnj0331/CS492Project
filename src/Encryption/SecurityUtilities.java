package Encryption;

import java.security.*;

public class SecurityUtilities {

    public static int hash(String usernameAndPassword){
        return usernameAndPassword.hashCode();
    }
}
