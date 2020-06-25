package network;

import java.io.Serializable;

public class AckPacket implements Serializable {

    private int ackValue;

    /**
     * Creates a new object of type AckPacket
     * @param ackValue The ack value (usually is seq + 1)
     */
    public AckPacket(int ackValue) {
        this.ackValue = ackValue;
    }

    public int getAckValue() {
        return ackValue;
    }

    public void setAckValue(int ackValue) {
        this.ackValue = ackValue;
    }
}
