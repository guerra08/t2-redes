package component;

import helper.Colors;
import helper.FileOperations;
import network.AckPacket;
import network.ConnPacket;
import network.FilePacket;
import network.UDPCommon;
import structures.PacketList;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Receiver extends UDPCommon {

    private final int RECEIVER_PORT = 9877;
    private final int SENDER_PORT = 9876;
    private DatagramSocket socket;
    private InetAddress ip;
    private PacketList receivedPackets;
    private int ack;
    private boolean connected;
    private boolean receivedAllPackets;

    public Receiver() {
        try{
            receivedAllPackets = false;
            connected = false;
            ip = InetAddress.getByName("localhost");
            socket = new DatagramSocket(RECEIVER_PORT);
            socket.setSoTimeout(333);
            receivedPackets = new PacketList();
            ack = 0;
            System.out.println(Colors.ANSI_BLUE + "Starting receiver on port " + RECEIVER_PORT + "..." + Colors.ANSI_RESET);
            while(!connected){
                _connect(socket);
            }
            _receivePacket();
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    /**
     * Receives a packet and sends it to the checking method.
     * @throws SocketException Regarding SOTimeout
     */
    protected void _receivePacket() throws SocketException {
        while(!receivedAllPackets){
            try{
                byte[] buf = new byte[1024];
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                socket.receive(dp);
                ByteArrayInputStream byteStream = new ByteArrayInputStream(buf);
                ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
                FilePacket filePacket = (FilePacket) is.readObject();
                System.out.println(Colors.ANSI_GREEN + "Received packet with seq " + filePacket.getSeq() + " of type " + filePacket.getClass().getSimpleName() + Colors.ANSI_RESET);
                is.close();
                byteStream.close();
                _checkIncomingPacket(filePacket);
            }catch (IOException | ClassNotFoundException e){
                if(e instanceof InterruptedIOException){
                    System.out.println(Colors.ANSI_YELLOW + "Receiver timed out after " + socket.getSoTimeout() + "ms." + Colors.ANSI_RESET);
                }
            }
        }
    }

    /**
     * Checks an incoming packet and executes the correct actions according to the state of the application.
     * @param packet Received packet
     */
    protected void _checkIncomingPacket(Object packet){
        if(packet instanceof ConnPacket){
            ConnPacket conn = (ConnPacket) packet;
            if(conn.getStep() == 0){
                conn.increaseStep();
                _sendPacket(conn, socket, ip, SENDER_PORT);
            }
        }
        else if(packet instanceof FilePacket){
            FilePacket filePacket = (FilePacket) packet;
            //The Receiver is considered "connected" after it receives the first packet (seq 0)
            if(filePacket.getSeq() == 0) connected = true;
            if(filePacket.getCrc() == FilePacket.calculateCRC(filePacket.getBytes())){
                if(filePacket.getSeq() == ack){
                    ack++;
                    receivedPackets.add(filePacket);
                    System.out.println(Colors.ANSI_GREEN + "Packet with seq " + filePacket.getSeq() + " has been processed." + Colors.ANSI_RESET);
                }
            }
            _sendPacket(new AckPacket(ack), socket, ip, SENDER_PORT);
            if(filePacket.isLastPacket()){
                if(receivedPackets.size() == filePacket.getTotalSegments()){
                    FileOperations.mountFileFromPackets(receivedPackets.getInternalList());
                    receivedAllPackets = true;
                }
            }
        }
    }

    public static void main(String[] args) {
        new Receiver();
    }
}
