package chatserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetworkObject extends Thread {
    String message;
    Socket connectionToClient;
    DataInputStream input;
    DataOutputStream output;
    
    NetworkObject(Socket connectionToClient) throws IOException {
        //handshake to the server, setup data streams
        this.connectionToClient = connectionToClient;        
        input = new DataInputStream(connectionToClient.getInputStream());
        output = new DataOutputStream(connectionToClient.getOutputStream());
    }
    
    public void run() {
        
    }
    
}
