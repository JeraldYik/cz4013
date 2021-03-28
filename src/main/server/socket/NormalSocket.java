package main.server.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

//import main.server.Console;
import main.server.message.BytePacker;

public class NormalSocket implements Socket{
    private DatagramSocket socket;

    public NormalSocket(DatagramSocket socket){
        this.socket = socket;
    }

    public void send(BytePacker msg, InetAddress address, int port) throws IOException {
//        Console.debug("InetAddress: "+ address + ", Port: " + port);
        byte[] message = msg.getByteArray();
        DatagramPacket p = new DatagramPacket(message, message.length,address, port);
        send(p);
        return;
    }

    public void receive (DatagramPacket p) throws IOException {
        this.socket.receive(p);
        return;
    }

    public void close() {
        this.socket.close();
        return;
    }

    public void setTimeOut(int timeout) throws SocketException {
        this.socket.setSoTimeout(timeout);
        return;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    public void send(DatagramPacket p) throws IOException{
        this.socket.send(p);
    }

}
