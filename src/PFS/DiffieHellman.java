package PFS;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class DiffieHellman {

    public Long CryptoSecureRand(){
        SecureRandom random = new SecureRandom();
        Long nonce;
        nonce = random.nextLong();
        return nonce;
    }

    /*
     * Copyright (c) 1997, 2017, Oracle and/or its affiliates. All rights reserved.
     *
     * Redistribution and use in source and binary forms, with or without
     * modification, are permitted provided that the following conditions
     * are met:
     *
     *   - Redistributions of source code must retain the above copyright
     *     notice, this list of conditions and the following disclaimer.
     *
     *   - Redistributions in binary form must reproduce the above copyright
     *     notice, this list of conditions and the following disclaimer in the
     *     documentation and/or other materials provided with the distribution.
     *
     *   - Neither the name of Oracle nor the names of its
     *     contributors may be used to endorse or promote products derived
     *     from this software without specific prior written permission.
     *
     * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
     * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
     * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
     * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
     * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
     * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
     * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
     * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
     * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
     * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
     * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
     */

    /*
     * One user generates this encoded public key and sends it to the other
     * the other user uses the encoded public key to generate the parameters for their public key.
     * This method looks for a passed key and generates one from its parameters or generates a new key
     * which can be passed to this method to generate another key for DH exchange and subsequent encryption
     */
    public byte[] DHKeyGenerator(byte[] DHInitPubKeyEnc) throws Exception {
        if(DHInitPubKeyEnc.length != 0){
            /*
                generates a key from an existing public keys parameters
             */
            KeyFactory bobKeyFac = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(DHInitPubKeyEnc);

            PublicKey initPubKey = bobKeyFac.generatePublic(x509KeySpec);

            DHParameterSpec dhParamFromInitPubKey = ((DHPublicKey)initPubKey).getParams();

            // User creates their own DH key pair
            KeyPairGenerator derivedKeyPairGen = KeyPairGenerator.getInstance("DH");
            derivedKeyPairGen.initialize(dhParamFromInitPubKey);
            KeyPair derivedKeyPair = derivedKeyPairGen.generateKeyPair();

            // User creates and initializes their owen DH KeyAgreement object
            KeyAgreement derivedDHKeyAgree = KeyAgreement.getInstance("DH");
            derivedDHKeyAgree.init(derivedKeyPair.getPrivate());

            // User encodes their public key, and sends it to the other user.
            byte[] derivedDHPubKeyEnc = derivedKeyPair.getPublic().getEncoded();
            return derivedDHPubKeyEnc;
        }else {
            /*
                Generates the key if no previous key was passed
             */
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("DH");
            keyPairGen.initialize(2048);
            KeyPair DHkeyPair = keyPairGen.generateKeyPair();

            // User creates and initializes their DH KeyAgreement object
            KeyAgreement DHKeyAgree = KeyAgreement.getInstance("DH");
            DHKeyAgree.init(DHkeyPair.getPrivate());

            // User encodes their public key, and sends it to the other user.
            byte[] DHPubKeyEnc = DHkeyPair.getPublic().getEncoded();
            return DHPubKeyEnc;
        }
    }
}
