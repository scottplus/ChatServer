//package chatserver;

import java.net.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame implements Runnable, ActionListener 
{
    private DataInputStream i = null;
    private DataOutputStream o = null;
    private JTextArea enteredText;
    private JScrollPane scrollPane;
    private JTextField typedText;
    private JButton send;
    private Thread listener = null;
    private Socket socket = null;
    private String hostName = "";
    private int port = 0;
    private LoginDB login = new LoginDB();
    private String username = "";
    private String password = "";
    
    public Client(String hostName, int port) 
    {
        this.hostName = hostName;
        this.port = port;
        createGUI();
        connect();
    }
    
    private boolean login()
    {
        System.out.println("Enter your username and password.");
        return true;
    }
    private void createGUI()
    {
           enteredText = new JTextArea();
           scrollPane = new JScrollPane();
           typedText = new JTextField();
           send = new JButton();
           
           setTitle("Chat");
           setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
           
           enteredText.setColumns(20);
           enteredText.setRows(5);
           scrollPane.setViewportView(enteredText);
           send.setText("Send");
           send.addActionListener(this);
           enteredText.setEditable(false);
           
           
           GroupLayout layout = new GroupLayout(getContentPane());
           getContentPane().setLayout(layout);
           layout.setHorizontalGroup(
           layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(typedText, GroupLayout.Alignment.LEADING, 
                                GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                    .addComponent(scrollPane, GroupLayout.Alignment.LEADING, 
                                GroupLayout.PREFERRED_SIZE, 357, 
                                GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(send, GroupLayout.PREFERRED_SIZE, 75, 
                                GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 241, 
                            GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(typedText, GroupLayout.DEFAULT_SIZE, 26, 
                            Short.MAX_VALUE)
                    .addComponent(send))
                .addContainerGap())
        );
        
        pack();
        typedText.requestFocusInWindow();
        setVisible(true);
    }
    
    private boolean connect()
    {
        // connect to server
        try 
        {
            socket = new Socket(hostName, port);
            i = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            o = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            listener = new Thread(this);
            listener.start();
            return true;
        } catch(IOException ioe) {ioe.printStackTrace();}
        
        return false;
    }
    public void run() 
    {
        try 
        {
            while (true) 
            {
                String line = i.readUTF();
                enteredText.append(line + "\n");
                enteredText.setCaretPosition(enteredText.getText().length());             
            }
        } catch (IOException ioe) {ioe.printStackTrace();} 
        finally 
        {
            listener = null;
            typedText.hide();
            validate();
            try 
            {
                o.close();
            } catch (IOException ioe) {ioe.printStackTrace();}
        }
    }
    
    public void actionPerformed(ActionEvent ae)
    {        
        if(ae.getActionCommand().equals("Send"))
        {
            try 
            {
                if(!typedText.getText().isEmpty())
                {
                    o.writeUTF(typedText.getText());
                    o.flush();
                }
            } catch (IOException ex) {
                //ex.printStackTrace();
                System.out.println("No working");
                listener.stop();
                }
        }
           typedText.setText("");
           typedText.requestFocusInWindow();
    }
    
    public static void main(String[] args) throws IOException 
    {
        new Client("localhost", 6666);
    }
}