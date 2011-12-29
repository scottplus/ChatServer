package chatserver;

public interface ServerCallBack {
    public void pushDataToClients(String message);
    
    public void serverControls(int command);
}
