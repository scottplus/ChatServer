//package chatserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

//ChatServer instance to hold host connections and synchronize data

public class ChatServer extends Thread implements ServerCallBack {
    ServerSocket hostServer;
    ArrayList<NetworkObject> connectedClients;
    boolean running = false;
    
    ChatServer(InetAddress ip, int port, int numberOfConnections) throws IOException {
        //initialize the objects
        hostServer = new ServerSocket(port, numberOfConnections, ip);       
    }
    
    public void run() {
        try {
            //keep connections alive and add to the ArrayList
            running = true; System.out.println("Listening for clients");
            listenForClients();
        } catch (InterruptedException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    public void pushDataToClients(String message) {
        //write the message to all clients in the arraylist
        try{
            for(NetworkObject current : connectedClients) {
                current.writeToByteStream(message);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    private void listenForClients() throws InterruptedException, IOException {
        while(running) {
            //add the client to the arraylist
            connectedClients.add(new NetworkObject(hostServer.accept(), this));
            System.out.println("Client connected");
        }
    }
    
    public static void main(String[] args) {
        //EXAMPLE INPUT == "128.67.80.24" "8080" "1000"
        try {
            if(args[0] != null && args[1] != null && args[2] != null) {
            
                //convert the names 
                InetAddress ip = InetAddress.getByName(args[0]);
                int port = Integer.parseInt(args[1]);
                int numberOfConnections = Integer.parseInt(args[2]);

                //start the chat server
                ChatServer server = new ChatServer(ip, port, numberOfConnections);
                server.start();
            }
        } catch (Exception e) {
            System.out.println("Failed to create the server instance... system exiting");
            System.out.println(e);
            System.exit(1);
        }
        
    }

 
}
