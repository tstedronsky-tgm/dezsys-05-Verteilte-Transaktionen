package service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.*;

/**
 * Created by Thomas on 15.01.2016.
 */
public class Service {
    private LDAPConnector c;
    private KeyPair keyPair;
    private SecretKey symKey;
    private Server s;


    public Service() {
        try {
            this.keyPair = generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }catch(NoSuchProviderException e){
            e.printStackTrace();
        }

        this.c = new LDAPConnector("192.168.17.128", 389,"cn=admin,dc=nodomain,dc=com", "user");

        this.s = new Server();

    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keypair = KeyPairGenerator.getInstance("RSA");
        SecureRandom secure = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keypair.initialize(1024, secure);
        KeyPair keyPair = keypair.generateKeyPair();
        return keyPair;
    }

    public void savePK() {
        System.out.println("Create PK");
        this.c.setDescription(toHex(this.keyPair.getPublic().getEncoded()), "service1");
    }


    public void decSK() {
        try {
            System.out.println("Decrypting SymKey with private key...");
            // Read the first message from the server
            byte[] encryptedSymKey = this.s.read();
            // set decryption algorithm
            Cipher cipher = Cipher.getInstance("RSA");
            // decrypt and set the sym key
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] decrypted = cipher.doFinal(encryptedSymKey);
            this.symKey = new SecretKeySpec(decrypted, 0, decrypted.length, "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendEncMes(String message) {
        if (this.symKey != null) {
            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, this.symKey);

                byte[] encrypted = cipher.doFinal((message).getBytes());
                this.s.getOut().writeInt(encrypted.length);
                this.s.getOut().write(encrypted);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new NullPointerException("SymKey is null");
        }
    }
    public Server getServer() {
        return s;
    }
    private String toHex(byte[] a) {
        String s = DatatypeConverter.printHexBinary(a);
        return s;
    }
}