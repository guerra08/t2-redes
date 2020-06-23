package component;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Receiver {

    private final int RECEIVER_PORT = 9877;
    private DatagramSocket socket;
    private InetAddress ip;

    public Receiver(String method) {
        if(method.equals("slow")){
            try{
                _receiverUsingSlowStart();
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    private void _receiverUsingSlowStart() throws IOException {
        byte[] buf = new byte[512];
        DatagramPacket dp = new DatagramPacket(buf, buf.length);
        socket.receive(dp);
        ByteArrayInputStream byteStream = new ByteArrayInputStream(buf);
        ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
        System.out.println(is);
    }
}
