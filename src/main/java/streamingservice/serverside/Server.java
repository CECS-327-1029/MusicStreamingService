package streamingservice.serverside;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server extends Thread {

    private DatagramSocket socket;  // used to send packets
    private boolean running;
    private byte[] buffer = new byte[256];  // contains the message

    public Server() throws SocketException {
        // sending or receiving point for a packet
        // uses port 4445
        socket = new DatagramSocket(4445);
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            // receives incoming messages
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            // blocks until a message arrives and it stores the message inside
            // the byte array of the packet
            try {
                socket.receive(packet);

                // get the address and port of the client so that we know who
                // to respond to
                InetAddress address = packet.getAddress();
                int port = packet.getPort();

                // this new packet is used to send a message to the client
                packet = new DatagramPacket(buffer, buffer.length, address, port);

                String received = new String(packet.getData(), 0, packet.getLength());

                if (received.equals("end")) {   // client's message is "end"
                    // ends by some error or user termination
                    running = false;
                    continue;
                }
                socket.send(packet);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
        socket.close();
    }


}