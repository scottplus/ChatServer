//package chatserver;
import java.net.*;
import java.io.*;

public class ChatServer
{
    public ChatServer(int port) throws IOException
    {
        //create the server socket
        ServerSocket server = new ServerSocket(port);
        
        //index for aligning the array
        int index = 0;
        
        while(true)
        {
            Socket client = server.accept();
            System.out.println("Accepted from " + client.getInetAddress());
            NetworkObject netObject = new NetworkObject(client, index);
            
            //start the network object
            netObject.start();
            
            //increment the index
            index++;
        }
    }
    
    public static void main(String[] args) throws IOException 
    {
        //if(args.length != 1)
          //  throw new RuntimeException("Syntax: ChatServer <port>");
        new ChatServer(6666);
    }
}