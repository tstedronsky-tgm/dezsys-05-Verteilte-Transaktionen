package service;

/**
 * Created by Thomas on 21.01.2016.
 */
public class Start {
    public static void main(String[] args) {
        LDAPConnector conn= new LDAPConnector("192.168.17.128", 389,"cn=admin,dc=nodomain,dc=com", "user");
        Service s = new Service(conn);
        Client_1 c = new Client_1(conn);
        s.savePK();
        c.getPK();
    }
}
