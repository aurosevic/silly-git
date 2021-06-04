package app.silly_git;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SillyFile implements Serializable {
    private boolean isDirectory;
    private AtomicInteger version;
    private int fileStatus;
    private byte[] fileContent;
    private String filePath;
    private String directoryPath;
    private Map<Integer, SillyFile> sillyFiles = Collections.synchronizedMap(new HashMap<>());

    public SillyFile(byte[] fileContent, String filePath) {
        this.fileContent = fileContent;
        this.filePath = filePath;
        this.isDirectory = false;
    }

    public SillyFile(byte[] fileContent, String filePath, AtomicInteger version) {
        this.version = version;
        this.fileContent = fileContent;
        this.filePath = filePath;
        this.isDirectory = false;
    }

    public SillyFile(String directoryPath) {
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

    public AtomicInteger getVersion() {
        return version;
    }

    public void incrementVersion() {
        this.version.incrementAndGet();
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    public Map<Integer, SillyFile> getSillyFiles() {
        return sillyFiles;
    }

    public void putToSillyMap(int key, SillyFile sillyFile) {
        this.sillyFiles.put(key, sillyFile);
    }
}
