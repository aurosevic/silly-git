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
    private String directoryPath;

    public SillyFile(byte[] fileContent, String filePath) {
        this.version = versionCounter.getAndIncrement();
        this.fileContent = fileContent;
        this.filePath = filePath;
        this.isDirectory = false;
    }

    public SillyFile(byte[] fileContent, String filePath, int version) {
        this.version = version;
        this.fileContent = fileContent;
        this.filePath = filePath;
        this.isDirectory = false;
    }

    public SillyFile(String directoryPath) {
        this.version = versionCounter.getAndIncrement();
        this.directoryPath = directoryPath;
        this.isDirectory = true;
    }

    public SillyFile(String directoryPath, int version) {
        this.version = version;
        this.directoryPath = directoryPath;
        this.isDirectory = true;
    }

    public SillyFile changeContent(SillyFile sillyFile, byte[] newContent) {
        return new SillyFile(newContent, sillyFile.getFilePath(), sillyFile.getVersion());
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

    public String getDirectoryPath() {
        return directoryPath;
    }

    public int getVersion() {
        return version;
    }
}
