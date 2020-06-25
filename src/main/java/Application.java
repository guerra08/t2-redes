import network.Packet;
import helper.FileOperations;

import java.util.ArrayList;

public class Application {

    public static void main(String[] args) {
        try{
            ArrayList<Packet> fileBytes = FileOperations.readFileAndReturnBytePartsAsPackets();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}
