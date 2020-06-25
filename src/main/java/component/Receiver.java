package component;

import helper.FileOperations;
import network.AckPacket;
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

    public Receiver() {
        try{
            ip = InetAddress.getByName("localhost");
            socket = new DatagramSocket(RECEIVER_PORT);
            socket.setSoTimeout(500);
            receivedPackets = new PacketList();
            System.out.println("Starting receiver...");
            _startReceiver();
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    private void _startReceiver(){
        while(true){
            try{
                byte[] buf = new byte[1024];
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                socket.receive(dp);
                ByteArrayInputStream byteStream = new ByteArrayInputStream(buf);
                ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
                Packet dPacket = (Packet) is.readObject();
                System.out.println("Received packet " + dPacket.getSeq());
                is.close();
                byteStream.close();
                if(dPacket.getCrc() == Packet.calculateCRC(dPacket.getBytes())){
                    receivedPackets.add(dPacket);
                }
                _sendPacket(new AckPacket(dPacket.getSeq() + 1), socket, ip, SENDER_PORT);
                if(dPacket.isLastPacket()){
                    System.out.println("Last packet received, seq " + dPacket.getSeq());
                    FileOperations.mountFileFromPackets(receivedPackets.getInternalList());
                    break;
                }
            }catch (IOException | ClassNotFoundException e){
                if(e instanceof InterruptedIOException){
                    System.err.println("Timed out.");
                }
            }
        }
    }

    public static void main(String[] args) {
        new Receiver();
    }
}
