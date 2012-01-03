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
    
    public NetworkObject(Socket connectionToClient, int index) throws IOException {
        //handshake to the server, setup data streams
        this.connectionToClient = connectionToClient;
        
        //keep a copy of the index so we can use the API
        this.index = index;
        
        //set the anonymous username
        username = "Anonymous";
        
        //byte streams
        i = new DataInputStream(new BufferedInputStream(connectionToClient.getInputStream()));
        o = new DataOutputStream(new BufferedOutputStream(connectionToClient.getOutputStream()));
    }

    public void run() {
        //add the current instance to the static vector
        handlers.insertElementAt(this, this.index);
        
        //set our boolean
        running = true;
        
        //listen for incomming messages
        listen();
    }
    
    private synchronized void listen() {
        try {
            while(running) {
                String msg = i.readUTF(); //blocking IO call
                
                if(msg.substring(0,2).equals("./")) {
                    //if message is a command, call the api
                    userControl(msg);                    
                } else {
                    //else send the message
                    broadcast(this.username+": "+msg);                    
                }
            }
        } catch (IOException e){
            e.printStackTrace();            
        }        
    }
    
    //API, decode text from the user input and drop connections safely from the vector
    
    //ways to decode the user's message
    
    public void userControl(String month) {
        //need to change to switch statement, JDK 7 required ***************************************************************************
        
        NetworkObject current = (NetworkObject) handlers.get(index);
        
        try {
            //if the user types './quit'
            if(month.toLowerCase().equals("./quit")) {
                //quit the server
                
                //let the users know they have disconnected
                broadcast("The user: "+username+" has disconnected from the server");
                
                //stop listening for messages
                running = false;
                
                //close the bytestreams
                current.i.close();
                current.o.close();
                
                //remove it from the vector
                handlers.remove(index);
                
                //exit the network object
                //System.exit(1);
            } else if(month.toLowerCase().substring(0,10).equals("./username")) {
                System.out.println("Entered the method");
                this.username = month.substring(10,month.length());
            } else {
                
            }
            
        } catch (IOException e) {
            System.out.println("Error with the API: " + e);
        }
    }

    //send to all the instances of NetworkObject
   private static void broadcast(String message) {
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
    
  /*  //send to specific instance of NetworkObject
    private static void broadcast(String message, int index) {
        //due to static method, synchronize the block
        NetworkObject c = (NetworkObject) handlers.get(index);
        synchronized (c) {
            //write 
            try {
                c.o.writeUTF(message);
                c.o.flush();
            } catch (IOException ex) {
                    c.stop();
                }
         }
    } */
}
