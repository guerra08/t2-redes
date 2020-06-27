package component;

import helper.FileOperations;
import network.AckPacket;
import network.Connection;
import network.Packet;
import network.UDPCommon;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Sender extends UDPCommon {

    private final int SENDER_PORT = 9876;
    private final int RECEIVER_PORT = 9877;
    private DatagramSocket socket;
    private InetAddress ip;
    private ArrayList<Packet> packets;
    private boolean connected;
    private int lastAckReceived;
    private int timeoutCount;
    private int repeatedAck;
    private Set<Integer> sentPackets;

    public Sender(ArrayList<Packet> packets) {
        try {
            connected = false;
            sentPackets = new HashSet<>();
            ip = InetAddress.getByName("localhost");
            socket = new DatagramSocket(SENDER_PORT);
            socket.setSoTimeout(500);
            System.out.println("Starting sender...");
            this.packets = packets;
            while(!connected){
                _sendPacket(new Connection(0), socket, ip, RECEIVER_PORT);
                _connect(socket);
            }
            timeoutCount = 0;
            repeatedAck = 0;
            _sendPacket(packets.get(0), socket, ip, RECEIVER_PORT);
            _startSender();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void _startSender() {
        while (connected) {
            try {
                if(timeoutCount == 5){
                    _sendPacket(packets.get(lastAckReceived), socket, ip, RECEIVER_PORT);
                    timeoutCount = 0;
                }
                else if(repeatedAck == 3){
                    _sendPacket(packets.get(lastAckReceived), socket, ip, RECEIVER_PORT);
                    repeatedAck = 0;
                }
                else{
                    byte[] buf = new byte[1024];
                    DatagramPacket dp = new DatagramPacket(buf, buf.length);
                    socket.receive(dp);
                    ByteArrayInputStream byteStream = new ByteArrayInputStream(buf);
                    ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
                    AckPacket confP = (AckPacket) is.readObject();
                    is.close();
                    byteStream.close();
                    _checkIncomingPacket(confP);
                }
            } catch (IOException | ClassNotFoundException e) {
                if (e instanceof InterruptedIOException) {
                    if(connected) timeoutCount++;
                    //System.err.println("Timed out.");
                }
            }
        }
    }

    protected void _checkIncomingPacket(Object packet) {
        if (packet instanceof Connection) {
            Connection conn = (Connection) packet;
            if (conn.getStep() == 1) {
                connected = true;
            }
        } else if (packet instanceof AckPacket) {
            AckPacket confP = (AckPacket) packet;
            if(lastAckReceived == confP.getAckValue()){
                repeatedAck++;
            }
            else{
                repeatedAck = 0;
            }
            lastAckReceived = confP.getAckValue();
            int i = 0;
            int ackAux = lastAckReceived;
            while (i < 2 && ackAux < packets.size()) {
                _sendPacket(packets.get(ackAux), socket, ip, RECEIVER_PORT);
                ackAux++;
                i++;
            }
        }
    }

    public static void main(String[] args) {
        try {
            new Sender(FileOperations.readFileAndReturnBytePartsAsPackets("src/main/resources/iris.data"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}
