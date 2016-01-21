package service;

/*
 * Java Security Documentation:
 *
 * - Java Security Overview 
     https://docs.oracle.com/javase/8/docs/technotes/guides/security/overview/jsoverview.html
 *
 * - Security Architecture
     https://docs.oracle.com/javase/8/docs/technotes/guides/security/spec/security-spec.doc.html
 *
 * - Java Cryptography Architecture (JCA) Reference Guide
     https://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html
 *
 * Read the Java Security Documentation and focus on following Classes:
 * - KeyPairGenerator
 * - SecureRandom
 * - KeyFactory
 * - X509EncodedKeySpec
 * - Cipher 
 *
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

import javax.xml.bind.DatatypeConverter;



public class ASymCrypto {

    public static Object getPublicKey( Attributes attrs ) {

    if (attrs == null) {
    	return null;
    } else {
        /* Print each attribute */
        try {
        	for (NamingEnumeration ae = attrs.getAll(); ae.hasMore(); ) {
        		Attribute attr = (Attribute)ae.next();
        		System.out.println( "attribute: " + attr.getID() );
 
        		/* print each value */
        		for (NamingEnumeration e = attr.getAll(); e.hasMore(); ) {
        			if ( attr.getID().equals( "description" ) )
        				return e.next();
        			e.next();
        		}
        	}
        } catch (NamingException e) {
        	e.printStackTrace();
        }
        return null;
    }
    }


	public static String toHexString(byte[] array) {
	    return DatatypeConverter.printHexBinary(array);
	}

	public static byte[] toByteArray(String s) {
	    return DatatypeConverter.parseHexBinary(s);
	}

    public static void main(String[] args) {

    	byte[] msg = "Das ist keine Nachricht!".getBytes();
        KeyPair keyPair = null;

        /* Generate a DSA signature */
    	try {

    		// create public key
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            generator.initialize(1024, random);
            keyPair = generator.generateKeyPair();

            byte[] key = keyPair.getPublic().getEncoded();



            FileOutputStream keyfos = new FileOutputStream("suepk");
            keyfos.write(key);
            keyfos.close();
            
            /*
            File file = new File("suepk");
            FileInputStream keyfis = new FileInputStream( file );
            byte encodedKey[] = new byte[ (int)file.length() ];
            keyfis.read( encodedKey );
            keyfis.close();
            */
            
            /* Store Public Key in Naming Directory */
            LDAPConnector ldapConnector = new LDAPConnector("192.168.17.128", 389,"cn=admin,dc=nodomain,dc=com", "user");
            ldapConnector.updateAttribute( "cn=group.service1,dc=nodomain,dc=com", "description", toHexString( key ) );

            /* Read Public Key from Naming Directory */
            String ldapKey = null;
            NamingEnumeration listName =ldapConnector.search( "dc=nodomain,dc=com", "(&(objectclass=PosixGroup)(cn=group.service1))" );
            while ( listName.hasMore() ) {
                SearchResult sr = (SearchResult) listName.next();
                ldapKey = getPublicKey( sr.getAttributes() ).toString();
            }

            key = toByteArray( ldapKey );
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec( key );
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic( pubKeySpec );

            System.out.println( new String( msg ) );

            Cipher cipher = Cipher.getInstance( "RSA" );
            cipher.init( Cipher.ENCRYPT_MODE, pubKey );
            byte[] encrypted = cipher.doFinal( msg );

            System.out.println( new String( encrypted ) );

            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate() );
            byte[] decrypted = cipher.doFinal( encrypted );

            System.out.println( new String( decrypted ) );

        } catch (Exception e) {
            System.err.println("Caught exception " + e.toString());
        }
    }
}
