package Authentication;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

public class DiffieHellman {

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
     * This constitutes picking the public DH values and the private value to create g^x mod p
     */

    //first key to be generated
    //alice generates her key and creates an object to store her data

    /**
     *
     * @return
     * @throws Exception
     */
    public MutAuthData DHPubKeyGenerator() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("DH");
        keyPairGen.initialize(2048);
        KeyPair DHkeyPair = keyPairGen.generateKeyPair();

        // User creates and initializes their DH KeyAgreement object
        KeyAgreement DHKeyAgree = KeyAgreement.getInstance("DH");
        DHKeyAgree.init(DHkeyPair.getPrivate());
        return new MutAuthData(DHkeyPair.getPublic().getEncoded(), DHKeyAgree);
    }

    //second key to be generated
    //bob uses alice's key to generate an object that stores his agreement and public key

    /**
     *
     * @param DHInitPubKeyEnc
     * @return
     * @throws Exception
     */
    public MutAuthData DHPubKeyGenerator(byte[] DHInitPubKeyEnc) throws Exception {
        KeyFactory KeyFac = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(DHInitPubKeyEnc);
        PublicKey initPubKey = KeyFac.generatePublic(x509KeySpec);
        DHParameterSpec dhParamFromInitPubKey = ((DHPublicKey)initPubKey).getParams();

        // User creates their own DH key pair
        KeyPairGenerator derivedKeyPairGen = KeyPairGenerator.getInstance("DH");
        derivedKeyPairGen.initialize(dhParamFromInitPubKey);
        KeyPair derivedKeyPair = derivedKeyPairGen.generateKeyPair();

        // User creates and initializes their owen DH KeyAgreement object
        KeyAgreement derivedDHKeyAgree = KeyAgreement.getInstance("DH");
        derivedDHKeyAgree.init(derivedKeyPair.getPrivate());

        return new MutAuthData(derivedKeyPair.getPublic().getEncoded(), derivedDHKeyAgree );

    }
    //returns the shared secret key for PFS
    public byte[] DHPrivKey(KeyAgreement keyAgree, byte[] pubKeyEnc) throws Exception {
        KeyFactory DHPrivateKey = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKeyEnc);
        PublicKey pubKey = DHPrivateKey.generatePublic(x509KeySpec);
        keyAgree.doPhase(  pubKey , true);
        return keyAgree.generateSecret();
    }

    public Long CryptoSecureRand(){
        SecureRandom random = new SecureRandom();
        return random.nextLong();
    }
}
