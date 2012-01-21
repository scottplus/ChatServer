import java.net.*;
import java.io.*;
import java.util.*;

public class ChatHandler extends Thread 
{
    private Socket connectionToClient;
    private DataInputStream i;
    private DataOutputStream o;
    private boolean running = false;
    private static ArrayList<ChatHandler> handler = new ArrayList<ChatHandler>();
    private int index = 0;
    //commands available to the API
    private String commands[] = {"./quit","./username", "./help"};   
    //username
    private String username;
    
    public static ChatHandler add(Socket connection) throws IOException
    {
        return new ChatHandler(connection);
    }
    private ChatHandler(Socket connection) throws IOException 
    {
        //handshake to the server, setup data streams
        this.connectionToClient = connection;
                
        //byte streams
        i = new DataInputStream(new BufferedInputStream(connectionToClient.getInputStream()));
        o = new DataOutputStream(new BufferedOutputStream(connectionToClient.getOutputStream()));
    }

    public void run() 
    {     
        //add the current instance to the static vector
        handler.add(this);
        
        //set the anonymous username
        username = "guest" + index;
        
        
        //listen for incomming messages
        listen();
    }
    
    //listen for incomming messages
    private synchronized void listen() 
    {
        try {
            while(true) {
                String msg = i.readUTF(); //blocking IO call
                
                if(msg.substring(0,2).equals("./")) {
                    //if message is a command, call the api
                    control(msg);                    
                } else {
                    //else send the message
                    broadcast(username+": "+msg);                    
                }
            }
        } catch (IOException e){
            //catch the IOException to handle the dropped byte stream when a user disconnects
            broadcast("Server the user "+username+" has disconnected from the server");
            System.out.println("The user: "+username+" has disconnected from the server");     
        }
    }
    
    //control method for user-server interaction
    public void control(String month) {
        //need to change to switch statement, JDK 7 required ***************************************************************************
        
        ChatHandler current = (ChatHandler) handler.get(index);

        try {
            //if the user types './quit'
            if(month.toLowerCase().contains("./quit")) {
                //quit the server
                
                //let the users know they have disconnected
                broadcast("Server: The user "+username+" has disconnected from the server");
                
                //stop listening for messages
                running = false;
                
                //close the bytestreams
                current.i.close();
                current.o.close();
                
                //remove it from the vector
                handler.remove(index);
                
            } else if(month.toLowerCase().contains("./username")) {
                //change the username
                String tempUsername = username;
                
                this.username = month.substring("./username ".length(),month.length());
                
                //broadcast to all users
                broadcast("Server: "+tempUsername + " has changed their username to "+username);
            } else if(month.toLowerCase().contains("./help")) {
                //display commands available to the user
                
                //display string
                String output = "Server: Commands available are \n ";
                
                //append the strings
                for(String append: commands) {
                    output += append + "\n";
                }
                
                //broadcast to the user
                ChatHandler.broadcast(output, index);
                
            }else {
                //if no API call available broadcast to the calling index
                ChatHandler.broadcast("Server: No API call under that name, please try again", index);
            }
            
        } catch (IOException e) {
            System.out.println("Error with the API: " + e);
        }
    }

    //send to all the instances of NetworkObject
   private static void broadcast(String message) {
       //synchronize the vector
        synchronized (handler) {
            Enumeration e = Collections.enumeration(handler);
            while (e.hasMoreElements()) {
                ChatHandler c = (ChatHandler) e.nextElement();
                try {
                    c.o.writeUTF(message);
                    c.o.flush();
                } catch (IOException ex) {
                    c.stop();
                }
            }
        }
    }
    
    //send to specific instance of NetworkObject
    private static void broadcast(String message, int index) {
        //static method synchronize the NetworkObject
        ChatHandler c = (ChatHandler) handler.get(index);
        synchronized (c) {
            //write 
            try {
                c.o.writeUTF(message);
                c.o.flush();
            } catch (IOException ex) {
                   //control("./quit");
            }
         }
    }
}