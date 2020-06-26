package network;

import java.io.Serializable;

public class Connection implements Serializable {

    private int step;

    public Connection(int step) { this.step = step; }

    public int getStep() { return step; }

    public void setStep(int step) { this.step = step; }

    public void increaseStep(){
        this.step++;
    }
}
