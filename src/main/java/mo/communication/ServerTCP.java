package mo.communication;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerTCP {
    
    private ServerSocket serverSocket;
  
  
    public ServerTCP(int port) throws IOException{
        System.out.println("INICIANDO SERVER TCP");
        serverSocket = new ServerSocket(port);
    }

    public boolean accept(RemoteClient rc) throws IOException, SocketException{
        rc.setSocketTCP(serverSocket);
        System.out.println("aceptado");
        return true;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
    
    public PetitionResponse receive(Socket client) throws IOException{
        ObjectInputStream inputStream;
        try {
            if(client != null && client.getInputStream().available() > 0) {
                inputStream = new ObjectInputStream(client.getInputStream());
                PetitionResponse petition = (PetitionResponse)inputStream.readObject();
                if(petition != null){
                    System.out.println(petition);
                    return petition;
                }
            }

        } catch (IOException ex) {
            return new PetitionResponse(Command.END_CONNECTION,null);
        } catch (ClassNotFoundException ex) {
            System.out.print("ClassNotFoundException ex");
        }
        return null;
    }


    public void send(Socket client, PetitionResponse response) throws IOException{
        
    }

    public void endConnection() throws IOException {
        serverSocket.close();
    }
}
