package service;

/**
 * Created by Thomas on 21.01.2016.
 */
import java.io.*;
import java.net.*;

public class Server {

    private ServerSocket sSocket;
    private Socket cSocket;

    private DataInputStream in;
    private DataOutputStream out;

    public Server() {
        try {
            this.sSocket = new ServerSocket(55555);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            cSocket = sSocket.accept();

            in = new DataInputStream(cSocket.getInputStream());
            out = new DataOutputStream(cSocket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public byte[] read() {
        try {
            int length = in.readInt();
            byte[] m = new byte[length];
            in.readFully(m, 0, m.length); // read the message
            return m;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            System.out.println("Serververbindung getrennt");

            cSocket.close();
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DataOutputStream getOut() {
        return out;
    }
}