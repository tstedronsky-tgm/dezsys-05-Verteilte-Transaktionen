package service;

/**
 * Created by Thomas on 15.01.2016.
 */
public class Service {
    private LDAPConnector c;
    private Server s;

    public Service(LDAPConnector c){
        this.c = c;
        this.s = new Server();
        this.s.start();
    }

    public void savePK(){
        this.c.setDescription("asdfklökjhfsdghkj", "service1");
    }
}
