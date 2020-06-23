package helper;

import component.Packet;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;

public class FileOperations {

    public static ArrayList<Packet> readFileAndReturnBytePartsAsPackets() throws IOException {
        RandomAccessFile raf = new RandomAccessFile("src/main/resources/lorem.txt", "r");
        byte[] fileBytes = new byte[(int) raf.length()];
        raf.readFully(fileBytes);
        raf.close();
        ArrayList<Packet> partsArray = new ArrayList<>();
        int id = 0;
        for (int i = 0; i < fileBytes.length - 512 + 1; i += 512){
            Packet p = new Packet(id, Arrays.copyOfRange(fileBytes, i, i + 512), "lorem.txt", i == fileBytes.length - 1);
            partsArray.add(p);
        }
        return partsArray;
    }

}
