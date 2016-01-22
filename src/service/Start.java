package service;

/**
 * Created by Thomas on 21.01.2016.
 */
public class Start {
    public static void main(String[] args) {

        Service s = new Service();
        Client_1 c = new Client_1();
        s.savePK();
        c.setPublicKey(c.makePK());
        s.getServer().start();
        c.getClient().start();
        c.sendEncryptSymKey();
        s.decSK();
        s.sendEncMes("Hallo Hallo");
        c.printDecryptedMessage();
    }
}
