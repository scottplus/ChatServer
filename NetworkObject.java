package chatserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkObject extends Thread {
    String username;
    String message;
    Socket connectionToClient;
    ServerCallBack server;
    DataInputStream input;
    DataOutputStream output;
    boolean running = false;
    
    NetworkObject(Socket connectionToClient, ServerCallBack server) throws IOException {
        //handshake to the server, setup data streams
        this.connectionToClient = connectionToClient; 
        //reference to the server, this is the callback method
        this.server = server;
        
        //byte streams
        input = new DataInputStream(connectionToClient.getInputStream());
        output = new DataOutputStream(connectionToClient.getOutputStream());
    }
    
    public void run() {
        try {
            //send the client a welcome message
            username = input.readUTF();
            message = "Welcome to the server, "+username+"!";
            writeToByteStream(message);
            
            //start listening for client messages
            running = true;
            listenForNewMessages();
        } catch (IOException ex) {
            Logger.getLogger(NetworkObject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void writeToByteStream(String message) throws IOException {
        //write to the connected client
        System.out.println("Writing data: "+message);
        output.writeUTF(message);
        output.flush();
    }
    
    public void listenForNewMessages() throws IOException {
        while(running) {
            System.out.println("Listening for messages");
            //listen for new messages, call back the server when a new message arrives
            message = input.readUTF();
            //assemble the message and send it to the server
            server.pushDataToClients(this.username+": "+this.message);
        }
    }
}
