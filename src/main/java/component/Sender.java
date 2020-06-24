package component;

import helper.FileOperations;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Sender extends UDPCommon{

    private final int SENDER_PORT = 9876;
    private final int RECEIVER_PORT = 9877;
    private DatagramSocket socket;
    private InetAddress ip;
    private DataPacket lastSentPacket;
    private ArrayList<DataPacket> packets;

    public Sender(String method, ArrayList<DataPacket> packets) {
        try {
            ip = InetAddress.getByName("localhost");
            socket = new DatagramSocket(SENDER_PORT);
            //socket.setSoTimeout(500);
            System.out.println("Starting sender...");
            if (method.equals("slow")) {
                this.packets = packets;
                _sendPacket(packets.remove(0), socket, ip, RECEIVER_PORT);
                _senderUsingSlowStart();
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void _senderUsingSlowStart(){
        while(true){
            try{
                byte[] buf = new byte[1024];
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                socket.receive(dp);
                ByteArrayInputStream byteStream = new ByteArrayInputStream(buf);
                ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
                ConfPacket confP = (ConfPacket) is.readObject();
                is.close();
                byteStream.close();
                _checkIncomingPacketSlowStart(confP);
            }catch (IOException | ClassNotFoundException e){
                if(e instanceof InterruptedIOException){
                    System.out.println("Timed out.");
                }
            }
        }
    }

    private void _checkIncomingPacketSlowStart(ConfPacket confP){
        if(lastSentPacket != null && confP.getReferredId() == lastSentPacket.getId()){
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
            new Sender("slow", FileOperations.readFileAndReturnBytePartsAsPackets());
        }catch (Exception e){
            System.out.println(e);
        }
    }

}
