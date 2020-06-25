package component;

import helper.FileOperations;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Receiver extends UDPCommon{

    private final int RECEIVER_PORT = 9877;
    private final int SENDER_PORT = 9876;
    private DatagramSocket socket;
    private InetAddress ip;
    private ArrayList<DataPacket> receivedPackets;

    public Receiver(String method) {
        try{
            ip = InetAddress.getByName("localhost");
            socket = new DatagramSocket(RECEIVER_PORT);
            socket.setSoTimeout(500);
            receivedPackets = new ArrayList<>();
            System.out.println("Starting receiver...");
            if(method.equals("slow")){
                _receiverUsingSlowStart();
            }
        }catch (Exception e){
            System.err.println(e);
        }
    }

    private void _receiverUsingSlowStart(){
        while(true){
            try{
                byte[] buf = new byte[1024];
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                socket.receive(dp);
                ByteArrayInputStream byteStream = new ByteArrayInputStream(buf);
                ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
                DataPacket dPacket = (DataPacket) is.readObject();
                System.out.println("Received packet " + dPacket.getId());
                is.close();
                byteStream.close();
                receivedPackets.add(dPacket);
                _sendPacket(new ConfPacket(dPacket.getId()), socket, ip, SENDER_PORT);
                if(dPacket.isLastPacket()){
                    System.out.println("Last packet received, id " + dPacket.getId());
                    FileOperations.mountFileFromPackets(receivedPackets);
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
        new Receiver("slow");
    }
}
