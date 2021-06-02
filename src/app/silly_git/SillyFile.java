package app.silly_git;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class SillyFile implements Serializable {
    private boolean isInMainNodeGroup;
    private File file;
    private boolean isDirectory;
    private AtomicInteger versionCounter = new AtomicInteger(0);
    private int version;
    private int fileStatus;

    public SillyFile(File file) {
        this.file = file;
        this.isDirectory = file.isDirectory();
        this.version = versionCounter.getAndIncrement();
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

    public File getFile() {
        return file;
    }
}
