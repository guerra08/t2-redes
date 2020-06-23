import component.DataPacket;
import helper.FileOperations;

import java.util.ArrayList;

public class Application {

    public static void main(String[] args) {
        try{
            ArrayList<DataPacket> fileBytes = FileOperations.readFileAndReturnBytePartsAsPackets();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}
