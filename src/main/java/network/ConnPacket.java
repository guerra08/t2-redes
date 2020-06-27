package network;

import java.io.Serializable;

public class ConnPacket implements Serializable {

    private int step;

    public ConnPacket(int step) { this.step = step; }

    public int getStep() { return step; }

    public void setStep(int step) { this.step = step; }

    public void increaseStep(){
        this.step++;
    }
}
