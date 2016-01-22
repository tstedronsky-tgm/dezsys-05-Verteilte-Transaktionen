package service;

/**
 * Created by Thomas on 21.01.2016.
 */

import javax.crypto.*;
import javax.xml.bind.DatatypeConverter;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;


public class Client_1 {



    private Client client;
    private SecretKey sk;
    private PublicKey pk;
    private LDAPConnector c;

    /**
     * Creates connection to LDAP-Server; generates sym key; sets socket information
     */
    public Client_1() {
        this.c = new LDAPConnector("192.168.17.128", 389,"cn=admin,dc=nodomain,dc=com", "user");
        this.pk = null;
        this.sk = generateSymKey();

        this.client = new Client();
    }


    public PublicKey makePK() {
        try {
            String key = this.c.getDescription("description","service1");

            byte[] byteKey = hexStringToByteArray(key);
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(byteKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(pubKeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }

        return null;
    }

    public SecretKey generateSymKey() {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            return keygen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }


    public void sendEncryptSymKey() {
        try {
            System.out.println("Make Encr Sym Key");
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, this.pk);
            byte[] encoded = cipher.doFinal(this.sk.getEncoded());
            this.client.write(encoded);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    public void printDecryptedMessage() {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, this.sk);
            byte[] decoded = cipher.doFinal(this.client.read());
            System.out.println("Received encrypted message and decrypted it: " + new String(decoded));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch(NoSuchPaddingException e){
            e.printStackTrace();
        } catch(BadPaddingException e){
            e.printStackTrace();
        } catch(InvalidKeyException e){
            e.printStackTrace();
        } catch(IllegalBlockSizeException e){
            e.printStackTrace();
        }
    }


    private byte[] hexStringToByteArray(String s) {
        System.out.println(s);
        return DatatypeConverter.parseHexBinary(s);
    }

    public Client getClient() {
        return client;
    }

    public SecretKey getSymKey() {
        return sk;
    }

    public void setSymKey(SecretKey sk) {
        this.sk = sk;
    }

    public PublicKey getPublicKey() {
        return pk;
    }

    public void setPublicKey(PublicKey pk) {
        this.pk = pk;
    }

    public void setClient(Client client) {
        this.client = client;
    }

}