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
    
    //index
    int index;
    
    //username
    String username;
    
    //running program
    
    public NetworkObject(Socket connectionToClient) throws IOException {
        //handshake to the server, setup data streams
        this.connectionToClient = connectionToClient;
        
        //set the anonymous username
        username = "Anonymous"+index;
        
        //byte streams
        i = new DataInputStream(new BufferedInputStream(connectionToClient.getInputStream()));
        o = new DataOutputStream(new BufferedOutputStream(connectionToClient.getOutputStream()));
    }

    public void run() { 
        //index is the size of the array currently
        index = handlers.size();
        
        //add the current instance to the static vector
        handlers.insertElementAt(this, this.index);
        
        //set our boolean
        running = true;
        
        //listen for incomming messages
        listen();
    }
    
    //listen for incomming messages
    private synchronized void listen() {
        try {
            while(running) {
                String msg = i.readUTF(); //blocking IO call
                
                if(msg.substring(0,2).equals("./")) {
                    //if message is a command, call the api
                    control(msg);                    
                } else {
                    //else send the message
                    broadcast(this.username+": "+msg);                    
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
        
        NetworkObject current = (NetworkObject) handlers.get(index);
        
        try {
            //if the user types './quit'
            if(month.toLowerCase().contains("./quit")) {
                //quit the server
                
                //let the users know they have disconnected
                broadcast("Server: The user "+username+" has disconnected from the server");
                System.out.println("The user: "+username+" has disconnected from the server");
                
                //stop listening for messages
                running = false;
                
                //close the bytestreams
                current.i.close();
                current.o.close();
                
                //remove it from the vector
                handlers.remove(index);
                
            } else if(month.toLowerCase().contains("./username")) {
                //change the username
                String tempUsername = this.username;
                
                this.username = month.substring("./username ".length(),month.length());
                
                //broadcast to all users
                broadcast("Server: "+tempUsername + " has changed their username to "+this.username);
                System.out.println("Server: "+tempUsername + " has changed their username to "+this.username);
            } else {
                //if no API call available broadcast to the calling index
                NetworkObject.broadcast("Server: No API call under that name, please try again", index);
                System.out.println(this.username + " attempted an illegal API call");
            }
            
        } catch (IOException e) {
            System.out.println("Error with the API: " + e);
        }
    }

    //send to all the instances of NetworkObject
   private static void broadcast(String message) {
       //synchronize the vector
        synchronized (handlers) {
            Enumeration e = handlers.elements();
            while (e.hasMoreElements()) {
                NetworkObject c = (NetworkObject) e.nextElement();
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
        NetworkObject c = (NetworkObject) handlers.get(index);
        synchronized (c) {
            //write 
            try {
                c.o.writeUTF(message);
                c.o.flush();
            } catch (IOException ex) {
                   // control("./quit");
            }
         }
    }
}
