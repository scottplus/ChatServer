package chatserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements Runnable {
	Socket socket;
        BufferedReader userInput;
        DataInputStream input;
        DataOutputStream output;
        
        String message, username;
        
        boolean running = false;
	
	Client() throws IOException {
            //setup byte streams and connect to server
            socket = new Socket("localhost",8080);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            
            //set up scanner to take user input
            userInput = new BufferedReader(new InputStreamReader(System.in));
        }
        
        public void run() {
            try {
                System.out.println("Connected to the server: "+socket.getInetAddress() + " On the port" + socket.getPort());
                System.out.println("Please enter your username: ");
                username = userInput.readLine();
                pushDataToServer(username);
                
                running = true;
                readUserInput();
                listenForMessages();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        private void readUserInput() throws IOException {
            while((message = userInput.readLine()) != null) {
                pushDataToServer(message);
            }
        }
        
        private void listenForMessages() throws IOException {
            while (running){
                System.out.println(message); 
            }
        }
        
        private void pushDataToServer(String message) throws IOException {
            output.writeUTF(message);
            output.flush();
        }
        
	public static void main(String[] args)  throws IOException {
		Client client = new Client();
                client.run();
	}
}
