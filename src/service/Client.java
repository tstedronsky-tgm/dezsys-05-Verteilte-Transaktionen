package service;

/**
 * Created by Thomas on 15.01.2016.
 */
import java.io.*;
import java.net.*;

public class Client {

    private Socket socket;

    private DataInputStream in;
    private DataOutputStream out;


    public Client() {
    }


    public void start() {
        try {
            this.socket = new Socket("192.168.17.128", 55555);
            this.out = new DataOutputStream(this.socket.getOutputStream());
            this.in = new DataInputStream(this.socket.getInputStream());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public byte[] read() {
        try {
            int length = in.readInt();
            byte[] m = new byte[length];
            in.readFully(m, 0, m.length);
            return m;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void write(byte[] bytes) {
        try {
            out.writeInt(bytes.length);
            out.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            System.out.println("Trenne Verbindung mit Server");
            out.close();
            in.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}