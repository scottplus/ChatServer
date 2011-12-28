package chatserver;


import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

//ChatServer instance to hold host connections and synchronize data

public class ChatServer extends Thread {
    ServerSocket hostServer;
    ArrayList<NetworkObject> connectedClients;
    boolean running = false;
    
    ChatServer(InetAddress ip, int port, int numberOfConnections) throws IOException {
        //initialize the objects
        hostServer = new ServerSocket(port, numberOfConnections, ip);       
    }
    
    public void run() {
        //keep connections alive and add to the ArrayList
        
        
    }
    
    
    private void pushData() {
        //write data to all network objects
        for(NetworkObject client : connectedClients) {
            
        }
    }
    
    public static void main(String[] args) {
        //EXAMPLE INPUT == "128.67.80.24" "8080" "1000"
        try {
            if(args[0] != null && args[1] != null && args[2] != null) {
            
                //convert the names 
                InetAddress ip = InetAddress.getByAddress(args[0].getBytes());
                int port = Integer.parseInt(args[1]);
                int numberOfConnections = Integer.parseInt(args[2]);

                //start the chat server
                ChatServer server = new ChatServer(ip, port, numberOfConnections);
                server.start();
            }
        } catch (Exception e) {
            System.out.println("Failed to create the server instance... system exiting");
            System.exit(1);
        }
        
    }
}
