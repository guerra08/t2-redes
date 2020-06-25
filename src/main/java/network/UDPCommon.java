package network;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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
            System.out.println("Packet sent to port " + port);
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

}
