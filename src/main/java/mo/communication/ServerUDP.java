/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.communication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlo
 */
public class ServerUDP {
    DatagramSocket socketUDP;
    byte[] buffer;
    DatagramPacket packetToSend;
    InetAddress host;
    int port;
    
    public ServerUDP(String hostIP, int port, int tamBuffer) throws SocketException, UnknownHostException{
        socketUDP = new DatagramSocket();
        buffer = new byte[tamBuffer];
        this.port = port;
        host = InetAddress.getByName(hostIP);       
    }
    
    //transmite datos al cliente

    public void send(PetitionResponse response) {
        try {
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ObjectOutput oo = new ObjectOutputStream(bStream);
            oo.writeObject(response);
            oo.close();
            byte[] serializedMessage = bStream.toByteArray();
            
            if(serializedMessage != null){
                DatagramPacket packetToSend = new DatagramPacket(serializedMessage,serializedMessage.length,host,port);
                socketUDP.send(packetToSend);
            }
            else
                System.out.println("serializedMessage es nulo");
        } catch (IOException ex) {
            Logger.getLogger(ServerUDP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void endConnection(){
        socketUDP.close();
    }

}