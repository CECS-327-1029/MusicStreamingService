package streamingservice.clientside;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {

    private DatagramSocket socket;
    private InetAddress address;
    private byte[] buffer;

    public Client() throws SocketException, UnknownHostException {
        // sending or receiving point for a packet
        socket = new DatagramSocket();
        // get client address
        address = InetAddress.getByName("localhost");
    }

    // sends messages to the server and returns the response
    public String sendMessage(String msg) {
        buffer = msg.getBytes();    // message to bytes
        String received = null;
        try {
            // create a packet that holds message, address, and port info
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4445);

            socket.send(packet);    // send the message
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet); // get the message from server
            // convert message bytes to string
            received = new String(packet.getData(), 0, packet.getLength());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return received;
    }

    public void close() {
        socket.close();
    }

}