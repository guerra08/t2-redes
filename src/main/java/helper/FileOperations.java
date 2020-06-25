package helper;

import network.Packet;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class FileOperations {

    /**
     * Reads a file and returns it's parts divided in 512 bytes pieces (packets)
     * @return <ArrayList<Packet>> List of packets
     * @throws IOException If file reading is not ok
     */
    public static ArrayList<Packet> readFileAndReturnBytePartsAsPackets() throws IOException {
        RandomAccessFile raf = new RandomAccessFile("src/main/resources/lorem.txt", "r");
        byte[] fileBytes = new byte[(int) raf.length()];
        raf.readFully(fileBytes);
        raf.close();
        ArrayList<Packet> partsArray = new ArrayList<>();
        int id = 0;
        int parts = fileBytes.length / 512;
        if(parts == 0){
            Packet p = new Packet(id, Arrays.copyOfRange(fileBytes, 0, fileBytes.length), "lorem.txt", true);
            partsArray.add(p);
        }
        else{
            for (int i = 0; i < fileBytes.length - 512 + 1; i += 512){
                Packet p = new Packet(id, Arrays.copyOfRange(fileBytes, i, i + 512), "lorem.txt", id == (parts - 1));
                partsArray.add(p);
                id++;
            }
        }
        return partsArray;
    }

    /**
     * Given a list of packets, mount them in a new file.
     * @param packets The list of packets
     */
    public static void mountFileFromPackets(ArrayList<Packet> packets){
        try{
            File fileToCreate = new File("src/main/resources/received/" + packets.get(0).getFName());
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
