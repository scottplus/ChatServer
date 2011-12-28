package chatserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetworkObject extends Thread {
    String message;
    Socket connectionToClient;
    ServerCallBack server;
    DataInputStream input;
    DataOutputStream output;
    
    
    NetworkObject(Socket connectionToClient, ServerCallBack server) throws IOException {
        //handshake to the server, setup data streams
        this.connectionToClient = connectionToClient; 
        //reference to the server, this is the callback method
        this.server = server;
        
        server.pushDataToClients("Test message");
        
        //byte streams
        input = new DataInputStream(connectionToClient.getInputStream());
        output = new DataOutputStream(connectionToClient.getOutputStream());
    }
    
    public void run() {
        System.out.println("Hello World");
    }
    
    public void writeToByteStream(String message) {
        //write to the connected socket
    }
    
    public void listenForNewMessages() {
        //listen for new messages, call back the server when a new message arrives
        
    }
    
    
}
