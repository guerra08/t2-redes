package structures;

import network.FilePacket;

import java.util.AbstractList;
import java.util.ArrayList;

public class PacketList extends AbstractList<FilePacket> {

    private final ArrayList<FilePacket> _list = new ArrayList<>();

    @Override
    public void add(int position, FilePacket e) {
        _list.add(e);
        _list.sort(null);
    }

    @Override
    public FilePacket get(int i) {
        return _list.get(i);
    }

    @Override
    public int size() {
        return _list.size();
    }

    public ArrayList<FilePacket> getInternalList(){
        return this._list;
    }

}
