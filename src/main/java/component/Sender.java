package component;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Sender {

    private final int SENDER_PORT = 9876;
    private DatagramSocket socket;
    private InetAddress ip;

    public Sender(String method) {
        if(method.equals("slow")){
            try{
                _senderUsingSlowStart();
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    private void _senderUsingSlowStart() throws IOException {

    }
}
