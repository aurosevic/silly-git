package app;

import java.io.Serializable;

/**
 * This is an immutable class that holds all the information for a servent.
 *
 * @author bmilojkovic
 */
public class ServentInfo implements Serializable {

    private static final long serialVersionUID = 5304170042791281555L;
    private final String ipAddress;
    private final int listenerPort;
    private final int chordId;
    private String root;
    private String storage;

    public ServentInfo(String ipAddress, int listenerPort) {
        this.ipAddress = ipAddress;
        this.listenerPort = listenerPort;
        this.chordId = ChordState.chordHash(listenerPort);
    }

    public ServentInfo(String ipAddress, int listenerPort, String root, String storage) {
        this.ipAddress = ipAddress;
        this.listenerPort = listenerPort;
        this.chordId = ChordState.chordHash(listenerPort);
        this.root = root;
        this.storage = storage;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getListenerPort() {
        return listenerPort;
    }

    public int getChordId() {
        return chordId;
    }

    public String getRoot() {
        return root;
    }

    public String getStorage() {
        return storage;
    }

    @Override
    public String toString() {
        return "[" + chordId + "|" + ipAddress + "|" + listenerPort + "]";
    }

}
