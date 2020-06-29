package component;

import helper.Colors;
import helper.FileOperations;
import network.AckPacket;
import network.ConnPacket;
import network.FilePacket;
import network.UDPCommon;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class Sender extends UDPCommon {

    private final int SENDER_PORT = 9876;
    private final int RECEIVER_PORT = 9877;
    private DatagramSocket socket;
    private InetAddress ip;
    private ArrayList<FilePacket> filePackets;
    private boolean connected;
    private boolean hasSentAllPackets;
    private int lastAckReceived;
    private int lastSeqSent;
    private int timeoutCount;
    private int repeatedAck;
    private int resentLastCount;

    public Sender(ArrayList<FilePacket> filePackets) {
        try {
            connected = false;
            resentLastCount = 0;
            lastSeqSent = 0;
            timeoutCount = 0;
            repeatedAck = 0;
            hasSentAllPackets = false;
            this.filePackets = filePackets;
            ip = InetAddress.getByName("localhost");
            socket = new DatagramSocket(SENDER_PORT);
            socket.setSoTimeout(333);
            System.out.println(Colors.ANSI_BLUE + "Starting sender on port " + SENDER_PORT + "..." + Colors.ANSI_RESET);
            while(!connected){
                _sendPacket(new ConnPacket(0), socket, ip, RECEIVER_PORT);
                _connect(socket);
            }
            _sendPacket(filePackets.get(0), socket, ip, RECEIVER_PORT);
            _receivePacket();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Updates the application state if necessary, receives a packet and sends it to the checking method.
     * @throws SocketException Regarding SOTimeout
     */
    protected void _receivePacket() throws SocketException {
        while (!hasSentAllPackets && resentLastCount <= 5) {
            try {
                if(lastAckReceived >= filePackets.size()){
                    hasSentAllPackets = true;
                }
                else if(timeoutCount == 5){
                    _sendPacket(filePackets.get(lastAckReceived), socket, ip, RECEIVER_PORT);
                    if(lastAckReceived == filePackets.size() - 1){
                        resentLastCount++;
                    }
                    timeoutCount = 0;
                }
                else if(repeatedAck == 3){
                    _sendPacket(filePackets.get(lastAckReceived), socket, ip, RECEIVER_PORT);
                    repeatedAck = 0;
                }
                byte[] buf = new byte[1024];
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                socket.receive(dp);
                ByteArrayInputStream byteStream = new ByteArrayInputStream(buf);
                ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
                AckPacket confP = (AckPacket) is.readObject();
                is.close();
                byteStream.close();
                _checkIncomingPacket(confP);
            } catch (IOException | ClassNotFoundException e) {
                if (e instanceof InterruptedIOException) {
                    if(connected) timeoutCount++;
                    System.out.println(Colors.ANSI_YELLOW + "Sender timed out after " + socket.getSoTimeout() + "ms." + Colors.ANSI_RESET);
                }
            }
        }
        System.out.println("\n" + Colors.ANSI_YELLOW + "Stopping Sender..." + Colors.ANSI_RESET);
    }

    /**
     * Checks an incoming packet and executes the correct actions according to the state of the application.
     * @param packet Received packet
     */
    protected void _checkIncomingPacket(Object packet) {
        if (packet instanceof ConnPacket) {
            ConnPacket conn = (ConnPacket) packet;
            if (conn.getStep() == 1) {
                connected = true;
            }
        } else if (packet instanceof AckPacket) {
            AckPacket confP = (AckPacket) packet;
            System.out.println(Colors.ANSI_GREEN + "Received packet with ack " + confP.getAckValue() + " of type " + confP.getClass().getSimpleName() + Colors.ANSI_RESET);
            if(lastAckReceived == confP.getAckValue()){
                repeatedAck++;
            }
            else{
                repeatedAck = 0;
            }
            lastAckReceived = confP.getAckValue();
            int i = 0;
            int seqAux = lastSeqSent;
            while (i < 2 && seqAux < filePackets.size() - 1) {
                seqAux++;
                _sendPacket(filePackets.get(seqAux), socket, ip, RECEIVER_PORT);
                lastSeqSent = seqAux;
                i++;
            }
        }
    }

    public static void main(String[] args) {
        try {
            new Sender(FileOperations.readFileAndReturnBytePartsAsPackets(args[0]));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}
