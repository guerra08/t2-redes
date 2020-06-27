package component;

import helper.Colors;
import helper.FileOperations;
import network.AckPacket;
import network.Connection;
import network.Packet;
import network.UDPCommon;
import structures.PacketList;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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
            socket.setSoTimeout(500);
            receivedPackets = new PacketList();
            ack = 0;
            System.out.println(Colors.ANSI_BLUE + "Starting receiver on port " + RECEIVER_PORT + "..." + Colors.ANSI_RESET);
            while(!connected){
                _connect(socket);
            }
            _startReceiver();
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    private void _startReceiver(){
        while(!receivedAllPackets){
            try{
                byte[] buf = new byte[1024];
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                socket.receive(dp);
                ByteArrayInputStream byteStream = new ByteArrayInputStream(buf);
                ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
                Packet packet = (Packet) is.readObject();
                System.out.println(Colors.ANSI_GREEN + "Received packet with seq " + packet.getSeq() + " of type " + packet.getClass().getSimpleName() + Colors.ANSI_RESET);
                is.close();
                byteStream.close();
                _checkIncomingPacket(packet);
            }catch (IOException | ClassNotFoundException e){
                if(e instanceof InterruptedIOException){
                    System.out.println(Colors.ANSI_YELLOW + "Receiver timed out after 500ms." + Colors.ANSI_RESET);
                }
            }
        }
    }

    protected void _checkIncomingPacket(Object packet){
        if(packet instanceof Connection){
            Connection conn = (Connection) packet;
            if(conn.getStep() == 0){
                conn.increaseStep();
                _sendPacket(conn, socket, ip, SENDER_PORT);
            }
        }
        else if(packet instanceof Packet){
            Packet dPacket = (Packet) packet;
            if(dPacket.getSeq() == 0) connected = true;
            if(dPacket.getCrc() == Packet.calculateCRC(dPacket.getBytes())){
                if(dPacket.getSeq() == ack){
                    ack++;
                    receivedPackets.add(dPacket);
                }
            }
            _sendPacket(new AckPacket(ack), socket, ip, SENDER_PORT);
            if(dPacket.isLastPacket()){
                System.out.println("Last packet received, seq " + dPacket.getSeq());
                if(receivedPackets.size() == dPacket.getTotalSegments()){
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
