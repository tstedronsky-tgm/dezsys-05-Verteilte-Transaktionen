package service;
import javax.naming.Context;
import javax.naming.*;
import javax.naming.directory.*;

import java.util.Hashtable;

/**
 * Demonstrates how to create an initial context to an LDAP server
 * using simple authentication.
 *
 * usage: java Simple
 */
public class LDAPConnector {
    private static DirContext ctx;

    public LDAPConnector(String host, int port,String auth_user,String auth_password) {
        // Set up environment for creating initial context
        Hashtable<String, Object> env = new Hashtable<String, Object>(11);

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put( Context.PROVIDER_URL, "ldap://" + host + ":" + port );

        // Authenticate
        env.put( Context.SECURITY_AUTHENTICATION, "simple" );
        env.put( Context.SECURITY_PRINCIPAL, auth_user );
        env.put( Context.SECURITY_CREDENTIALS, auth_password );
        try {
            this.ctx = new InitialDirContext(env);
        } catch (NamingException e) {
            e.printStackTrace();
        }


    }

    public static void printSearchResult( NamingEnumeration namingEnum ) {
        try {
            while (namingEnum.hasMore()) {
                SearchResult sr = (SearchResult) namingEnum.next();
                String name = sr.getName();
                String description = sr.getAttributes().get( "description" ) != null ? sr.getAttributes().get( "description" ).toString() : "";
                System.out.println(">>>" + name + " " + description );
            }
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public static NamingEnumeration search( String inBase, String inFilter ) throws NamingException {

        // Create default search controls
        SearchControls ctls = new SearchControls();

        // Specify the search filter to match
        // Ask for objects with attribute sn == Geisel and which have
        // the "mail" attribute.
        String filter = "";

        // Search for objects using filter
        return ctx.search( inBase, inFilter, ctls );

    }

    public static void updateAttribute( String inDN, String inAttribute, String inValue ) throws NamingException {
        try {
            ModificationItem[] mods = new ModificationItem[1];
            Attribute mod0 = new BasicAttribute( inAttribute, inValue );
            mods[0] = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, mod0 );
            ctx.modifyAttributes( inDN, mods );
        } catch (NamingException e) {
            System.out.println("Authentication nok");
        }
    }

    public void setDescription(String desc, String group){
        try {
            this.updateAttribute("cn=group."+group+",dc=nodomain,dc=com", "description", desc);
        } catch (NamingException e) {
            System.out.println("Authentication nok");
        }
    }

    public String getDescription(String desc, String group){
        try {
            NamingEnumeration result = this.search("dc=nodomain,dc=com", "(&(objectclass=PosixGroup)(cn=group." + group + "))");
            while (result.hasMore()) {
                SearchResult content = (SearchResult) result.next();
                if (content.getAttributes().get(desc) != null) {
                    return content.getAttributes().get(desc).get().toString();
                }
            }
        }catch(NamingException e) {
            System.out.println("Authentication nok");
        }
        return null;
    }

    public void closeCTX() {
        try {

            // Create initial context
            this.ctx.close();


        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    /*public static void main(String[] args) {
        LDAPConnector c= new LDAPConnector("192.168.17.128", 389,"cn=admin,dc=nodomain,dc=com", "user");
        System.out.println("Verbinde ...");
        Hashtable<String, Object> env = c.getHash();
	    try {
	    	
	        // Create initial context
	        ctx = new InitialDirContext(env);
	        
	        NamingEnumeration listName = search( "dc=nodomain,dc=com", "(&(objectclass=PosixGroup)(cn=group.service3))" );
		    
	        if ( listName.hasMoreElements() )
	        	updateAttribute( "cn=group.service1,dc=nodomain,dc=com", "description", "asdfg" );

            System.out.println("Authentication ok");
		    // Print the answwer
	        // printSearchResult( answer );
	
		    // Close the context when we're done
	        ctx.close();
	        
	    } catch (NamingException e) {
            System.out.println("Authentication nok");
	    }
    }*/
}