package network;

import helper.Colors;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public abstract class UDPCommon {

    /**
     * Sends a packet to a given port in an ip address using a DatagramSocket.
     * @param packet The packet being sent
     * @param socket The target socket
     * @param ip IP address
     * @param port Port number
     */
    protected void _sendPacket(Object packet, DatagramSocket socket, InetAddress ip, int port){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(packet);
            os.flush();
            byte[] sendData = bos.toByteArray();
            os.close();
            bos.close();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, port);
            socket.send(sendPacket);
            if(packet instanceof FilePacket){
                System.out.println(Colors.ANSI_CYAN + "Packet of type " + packet.getClass().getSimpleName() + " with seq "
                        + ((FilePacket) packet).getSeq() + " sent to port " + port + Colors.ANSI_RESET + "\n");
            }
            else if(packet instanceof AckPacket)
                System.out.println(Colors.ANSI_CYAN + "Packet of type " + packet.getClass().getSimpleName() + " with ack "
                        + ((AckPacket) packet).getAckValue() +  " sent to port " + port +Colors.ANSI_RESET + "\n");
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    /**
     * Method for connecting the sender and the receiver
     * @param socket The socket being used
     */
    protected void _connect(DatagramSocket socket) {
        try {
            byte[] buf = new byte[1024];
            DatagramPacket dp = new DatagramPacket(buf, buf.length);
            socket.receive(dp);
            ByteArrayInputStream byteStream = new ByteArrayInputStream(buf);
            ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
            Object p = is.readObject();
            is.close();
            byteStream.close();
            _checkIncomingPacket(p);
        } catch (Exception e) {
            if(e instanceof InterruptedIOException){
                System.out.println(Colors.ANSI_YELLOW + "Awaiting connection..." + Colors.ANSI_RESET);
            }
            else System.err.println(e.getMessage());
        }
    }

    /**
     * Method for checking the received packet
     * @param packet Received packet
     */
    protected abstract void _checkIncomingPacket(Object packet);

    /**
     * Method for receiving incoming packets
     */
    protected abstract void _receivePacket() throws SocketException;
}
