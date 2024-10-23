package mo.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoteClient {
    private String ip;
    private String multicastIP;
    private ArrayList<String> capturePlugins; // capture plugin's list listened
    private Socket socketTCP;
    private ObjectOutputStream outStream;
    private final int TIME_OUT = 50; // in ms (5 sec)
    private int portUDP;
    private InetAddress clientHost;
    
    public RemoteClient(){
        this.capturePlugins = new ArrayList<>();
    }

    public Socket getSocket() {
        return socketTCP;
    }
    
    public String getIPAddress(){
        return this.ip;
    }

    public int getPortUDP() {
        return portUDP;
    }
    
    public void setPortUDP(int port) {
        this.portUDP = port;
    }

    public InetAddress getClientHost() {
        if(clientHost == null)
            try {clientHost = InetAddress.getByName(ip);} catch (UnknownHostException ex) {}
        return clientHost;
    }
    
    
    
    public boolean setSocketTCP(ServerSocket ss) throws SocketException{
        try {
            this.socketTCP = ss.accept();
            SocketAddress socketAddress = socketTCP.getRemoteSocketAddress();

            if (socketAddress instanceof InetSocketAddress) {
                InetAddress inetAddress = ((InetSocketAddress)socketAddress).getAddress();
                if (inetAddress instanceof Inet4Address){
                    this.ip = ((Inet4Address) inetAddress).toString().substring(1);
                    //this.ip = (((Inet4Address) inetAddress).getAddress()).toString();
                    System.out.println("IPv4: " + inetAddress);
                }
                else if (inetAddress instanceof Inet6Address){
                    this.ip = ((Inet6Address) inetAddress).toString().substring(1);
                    //this.ip = (((Inet6Address) inetAddress).getAddress()).toString();
                    System.out.println("IPv6: " + inetAddress);
                }
                else
                    System.err.println("Not an IP address.");
            } else {
                System.err.println("Not an internet protocol socket.");
            }
            System.out.println("IP remota es: "+ip);
            return true;
        } catch (IOException ex) {}
        return false;
    }
    
    public boolean send(PetitionResponse response){
        try{
            ObjectOutputStream outputStream = new ObjectOutputStream(socketTCP.getOutputStream());
            outputStream.writeObject(response);
            return true;
        }catch(Exception ex){
            return false;
        }
        
    }
    
    public String receive() throws IOException{
        //DataInputStream in = new DataInputStream(connectionSocket.getInputStream());
        //String a = in.readUTF();
        //return a;
        BufferedReader reader = new BufferedReader(new InputStreamReader(socketTCP.getInputStream()));
        String a = reader.readLine();
        System.out.println("Servidor recibe: "+a);
        return a;
    }
    
    public void deletePlugin(String pluginID){
        if(capturePlugins.contains(pluginID)){
            capturePlugins.remove(pluginID);
        }
    }
    
    public void endConnection() throws IOException{
        if(!socketTCP.isClosed()){
            socketTCP.close();
        }
        
    }
}
