package service;

/**
 * Created by Thomas on 21.01.2016.
 */
public class Client_1 {
    private LDAPConnector c;
    private Client cl;

    public Client_1(LDAPConnector c){
        this.c = c;
        this.cl = new Client();
        this.cl.start();
    }

    public void getPK(){
        System.out.println(this.c.getDescription("description", "service1"));
    }
}
