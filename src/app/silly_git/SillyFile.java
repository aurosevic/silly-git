package app.silly_git;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class SillyFile implements Serializable {
    private boolean isInMainNodeGroup;
    private String path;
    private boolean isDirectory;
    private AtomicInteger version = new AtomicInteger();
    private int fileStatus;

    public SillyFile(String path, boolean isDirectory) {
        this.path = path;
        this.isDirectory = isDirectory;
        this.version.incrementAndGet();
        this.isInMainNodeGroup = false;
    }

    public SillyFile(boolean isInMainNodeGroup) {
        this.isInMainNodeGroup = isInMainNodeGroup;
    }

    public SillyFile(int fileStatus) {
        this.fileStatus = fileStatus;
    }

    public int getFileStatus() {
        return fileStatus;
    }
}
