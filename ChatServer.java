//package chatserver;
import java.net.*;
import java.io.*;

public class ChatServer
{
    public ChatServer(int port) throws IOException
    {
        //create the server socket
        ServerSocket server = new ServerSocket(port);

        System.out.println("Listening for clients");
        while(true)
        {
            //wait for clients
            Socket client = server.accept(); //blocking IO
            System.out.println("Accepted from " + client.getInetAddress());
            NetworkObject netObject = new NetworkObject(client);
            
            //start the network object
            netObject.start();
        }
    }
    
    public static void main(String[] args) throws IOException 
    {
        //if(args.length != 1)
          //  throw new RuntimeException("Syntax: ChatServer <port>");
        new ChatServer(6666);
    }
}