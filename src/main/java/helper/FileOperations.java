package helper;

import network.FilePacket;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class FileOperations {

    /**
     * Reads a file and returns it's parts divided in 512 bytes pieces (packets)
     * @return <ArrayList<Packet>> List of packets
     * @throws IOException If file reading is not ok
     */
    public static ArrayList<FilePacket> readFileAndReturnBytePartsAsPackets(String fileName) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "r");
        String fName = fileName.substring(fileName.lastIndexOf('/') + 1);
        byte[] fileBytes = new byte[(int) raf.length()];
        raf.readFully(fileBytes);
        raf.close();
        ArrayList<FilePacket> partsArray = new ArrayList<>();
        int parts = fileBytes.length / 512;
        if(parts == 0){
            FilePacket p = new FilePacket(0, Arrays.copyOfRange(fileBytes, 0, fileBytes.length), fName, true, parts + 1);
            partsArray.add(p);
        }
        else{
            int start = 0;
            for (int i = 0; i <= parts; i++){
                int end = Math.min(start + 512, fileBytes.length);
                FilePacket p = new FilePacket(i, Arrays.copyOfRange(fileBytes, start, end), fName, i == (parts), parts + 1);
                partsArray.add(p);
                start += 512;
            }
        }
        return partsArray;
    }

    /**
     * Given a list of packets, mount them in a new file.
     * @param filePackets The list of packets
     */
    public static void mountFileFromPackets(ArrayList<FilePacket> filePackets){
        try{
            File fileToCreate = new File("./" + filePackets.get(0).getFName());
            OutputStream os = new FileOutputStream(fileToCreate);
            while(!filePackets.isEmpty()){
                os.write(filePackets.remove(0).getBytes());
            }
            os.close();
            System.out.println(Colors.ANSI_GREEN + "File " + fileToCreate.getName() + " has been saved." + Colors.ANSI_RESET);
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

}
