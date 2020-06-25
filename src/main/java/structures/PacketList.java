package structures;

import network.Packet;

import java.util.AbstractList;
import java.util.ArrayList;

public class PacketList extends AbstractList<Packet> {

    private final ArrayList<Packet> _list = new ArrayList<>();

    @Override
    public void add(int position, Packet e) {
        _list.add(e);
        _list.sort(null);
    }

    @Override
    public Packet get(int i) {
        return _list.get(i);
    }

    @Override
    public int size() {
        return _list.size();
    }

    public ArrayList<Packet> getInternalList(){
        return this._list;
    }

}
