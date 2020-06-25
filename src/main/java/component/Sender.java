package component;

import helper.FileOperations;
import network.AckPacket;
import network.Packet;
import network.UDPCommon;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Sender extends UDPCommon {

    private final int SENDER_PORT = 9876;
    private final int RECEIVER_PORT = 9877;
    private DatagramSocket socket;
    private InetAddress ip;
    private Packet lastSentPacket;
    private ArrayList<Packet> packets;

    public Sender(ArrayList<Packet> packets) {
        try {
            ip = InetAddress.getByName("localhost");
            socket = new DatagramSocket(SENDER_PORT);
            //socket.setSoTimeout(500);
            System.out.println("Starting sender...");
            this.packets = packets;
            _sendPacket(packets.remove(0), socket, ip, RECEIVER_PORT);
            _startSender();
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    private void _startSender(){
        while(true){
            try{
                byte[] buf = new byte[1024];
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                socket.receive(dp);
                ByteArrayInputStream byteStream = new ByteArrayInputStream(buf);
                ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
                AckPacket confP = (AckPacket) is.readObject();
                is.close();
                byteStream.close();
                _checkIncomingPacketSlowStart(confP);
                if(packets.isEmpty()) break;
            }catch (IOException | ClassNotFoundException e){
                if(e instanceof InterruptedIOException){
                    System.err.println("Timed out.");
                }
            }
        }
    }

    private void _checkIncomingPacketSlowStart(AckPacket confP){
        if(lastSentPacket != null && confP.getAckValue() == lastSentPacket.getSeq() + 1){
            lastSentPacket = null;
        }
        int i = 0;
        while(i < 2 && packets.size() != 0){
            _sendPacket(packets.remove(0), socket, ip, RECEIVER_PORT);
            i++;
        }
    }

    public static void main(String[] args) {
        try{
            new Sender(FileOperations.readFileAndReturnBytePartsAsPackets("src/main/resources/lorem.txt"));
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

}
