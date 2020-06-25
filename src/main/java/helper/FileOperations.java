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
    public static ArrayList<Packet> readFileAndReturnBytePartsAsPackets(String fileName) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "r");
        String fName = fileName.substring(fileName.lastIndexOf('/') + 1);
        byte[] fileBytes = new byte[(int) raf.length()];
        raf.readFully(fileBytes);
        raf.close();
        ArrayList<Packet> partsArray = new ArrayList<>();
        int parts = fileBytes.length / 512;
        System.out.println(parts);
        if(parts == 0){
            Packet p = new Packet(0, Arrays.copyOfRange(fileBytes, 0, fileBytes.length), fName, true);
            partsArray.add(p);
        }
        else{
            int start = 0;
            for (int i = 0; i <= parts; i++){
                int end = Math.min(start + 512, fileBytes.length);
                Packet p = new Packet(i, Arrays.copyOfRange(fileBytes, start, end), fName, i == (parts));
                partsArray.add(p);
                start += 512;
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
