package component;

public class Packet {

    private int id;
    private byte[] bytes;
    private String fName;
    private boolean isLastPacket;

    public Packet(int id, byte[] bytes, String fName, boolean isLast) {
        this.id = id;
        this.bytes = bytes;
        this.fName = fName;
        this.isLastPacket = isLast;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public boolean isLastPacket() {
        return isLastPacket;
    }

    public void setLastPacket(boolean lastPacket) {
        isLastPacket = lastPacket;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
