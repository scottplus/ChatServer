//package chatserver;

import java.net.*;
import java.io.*;
import java.util.*;

public class NetworkObject extends Thread {

    Socket connectionToClient;
    DataInputStream i;
    DataOutputStream o;
    boolean running = false;
    private static Vector handlers = new Vector();

    public NetworkObject(Socket connectionToClient) throws IOException {
        //handshake to the server, setup data streams
        this.connectionToClient = connectionToClient;
        //reference to the server, this is the callback method        
        //byte streams
        i = new DataInputStream(new BufferedInputStream(connectionToClient.getInputStream()));
        o = new DataOutputStream(new BufferedOutputStream(connectionToClient.getOutputStream()));
    }

    public void run() {
        try {
            handlers.addElement(this);
            while (true) {
                String msg = i.readUTF();
                broadcast(msg);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            handlers.removeElement(this);
            try {
                connectionToClient.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void broadcast(String message) {
        synchronized (handlers) {
            Enumeration e = handlers.elements();
            while (e.hasMoreElements()) {
                NetworkObject c = (NetworkObject) e.nextElement();
                try {
                    synchronized (c.o) {
                        c.o.writeUTF(message);
                    }
                    c.o.flush();
                } catch (IOException ex) {
                    c.stop();
                }
            }
        }
    }
}
