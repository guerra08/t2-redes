package component;

import java.io.Serializable;

public class ConfPacket implements Serializable {

    private int referredId;

    public ConfPacket(int referredId) {
        this.referredId = referredId;
    }

    public int getReferredId() {
        return referredId;
    }

    public void setReferredId(int referredId) {
        this.referredId = referredId;
    }
}
