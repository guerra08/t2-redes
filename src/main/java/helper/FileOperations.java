package helper;

import component.DataPacket;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class FileOperations {

    public static ArrayList<DataPacket> readFileAndReturnBytePartsAsPackets() throws IOException {
        RandomAccessFile raf = new RandomAccessFile("src/main/resources/lorem.txt", "r");
        byte[] fileBytes = new byte[(int) raf.length()];
        raf.readFully(fileBytes);
        raf.close();
        ArrayList<DataPacket> partsArray = new ArrayList<>();
        int id = 0;
        int parts = fileBytes.length / 512;
        for (int i = 0; i < fileBytes.length - 512 + 1; i += 512){
            DataPacket p = new DataPacket(id, Arrays.copyOfRange(fileBytes, i, i + 512), "lorem.txt", id == (parts - 1));
            partsArray.add(p);
            id++;
        }
        return partsArray;
    }

    public static void mountFileFromPackets(ArrayList<DataPacket> packets){
        try{
            File fileToCreate = new File("src/main/resources/received/" + packets.get(0).getfName());
            OutputStream os = new FileOutputStream(fileToCreate);
            while(!packets.isEmpty()){
                os.write(packets.remove(0).getBytes());
            }
            os.close();
        }catch (Exception e){
            System.err.println(e.getMessage());
        }

    }

}
