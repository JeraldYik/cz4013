package main.server.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

import main.server.message.BytePacker;

public class WrapperSocket implements Socket {
    private final Socket socket;

    public WrapperSocket(Socket socket){
        this.socket = socket;
    }

    public void send(BytePacker msg, InetAddress address, int port) throws IOException {

        this.socket.send(msg, address, port);

    }

    public void receive(DatagramPacket p) throws IOException {
        this.socket.receive(p);
    }

    public void close() {
        this.socket.close();

    }

    public void setTimeOut(int timeout) throws SocketException {
        this.socket.setTimeOut(timeout);

    }

    public Socket getSocket(){
        return socket;
    }

}
