package helper;

import component.DataPacket;

import java.io.IOException;
import java.io.RandomAccessFile;
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
        for (int i = 0; i < fileBytes.length - 512 + 1; i += 512){
            DataPacket p = new DataPacket(id, Arrays.copyOfRange(fileBytes, i, i + 512), "lorem.txt", i == fileBytes.length - 1);
            partsArray.add(p);
        }
        return partsArray;
    }

}
