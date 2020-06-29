package network;

import java.io.Serializable;

public class ConnPacket implements Serializable {

    private int step;

    /**
     * Creates a new object of type ConnPacket, used for connection between Sender and Receiver.
     * @param step The step value (0 from Sender opening and 1 from Receiver acknowledging it).
     */
    public ConnPacket(int step) { this.step = step; }

    public int getStep() { return step; }

    public void setStep(int step) { this.step = step; }

    public void increaseStep(){
        this.step++;
    }
}
