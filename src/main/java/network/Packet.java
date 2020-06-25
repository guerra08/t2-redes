package network;

import java.io.Serializable;
import java.util.zip.CRC32;

public class Packet implements Serializable {

    private int seq;
    private byte[] bytes;
    private String fName;
    private final long crc;
    private boolean isLastPacket;

    /**
     * Creates a new object of type Packet
     * @param seq Seq value
     * @param bytes Bytes stored from file
     * @param fName Name of the file
     * @param isLast Boolean flag to check if is last packet
     */
    public Packet(int seq, byte[] bytes, String fName, boolean isLast) {
        CRC32 crcObject = new CRC32();
        crcObject.update(bytes);
        this.seq = seq;
        this.bytes = bytes;
        this.fName = fName;
        this.isLastPacket = isLast;
        this.crc = crcObject.getValue();
    }

    public String getFName() { return fName; }

    public void setFName(String fName) { this.fName = fName; }

    public boolean isLastPacket() { return isLastPacket; }

    public void setLastPacket(boolean lastPacket) { isLastPacket = lastPacket; }

    public int getSeq() { return seq; }

    public void setSeq(int seq) { this.seq = seq; }

    public byte[] getBytes() { return bytes; }

    public void setBytes(byte[] bytes) { this.bytes = bytes; }

    public long getCrc(){ return this.crc; }

    public static long calculateCRC(byte[] byteArray){
        CRC32 crcObject = new CRC32();
        crcObject.update(byteArray);
        return crcObject.getValue();
    }
}
