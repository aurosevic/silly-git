package app.silly_git;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class SillyFile implements Serializable {
    private boolean isDirectory;
    private AtomicInteger versionCounter = new AtomicInteger(0);
    private int version;
    private int fileStatus;
    private byte[] fileContent;
    private String filePath;

    public SillyFile(byte[] fileContent, String filePath) {
        this.version = versionCounter.getAndIncrement();
        this.fileContent = fileContent;
        this.filePath = filePath;
        this.isDirectory = false;
    }

    public SillyFile(int fileStatus) {
        this.fileStatus = fileStatus;
    }

    public int getFileStatus() {
        return fileStatus;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public String getFilePath() {
        return filePath;
    }
}
