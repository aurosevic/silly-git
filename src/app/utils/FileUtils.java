package app.utils;

import app.AppConfig;
import app.ChordState;
import app.silly_git.SillyFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;

public class FileUtils {

    public static final String VERSION_PREFIX = "~";
    public static final String SEPARATOR = FileSystems.getDefault().getSeparator();

    public static void addFileToStorage(SillyFile sillyFile, boolean isPull) {
        try {
            byte[] fileContent = sillyFile.getFileContent();
            String rootDir = isPull ? AppConfig.myServentInfo.getRoot() : AppConfig.myServentInfo.getStorage();
            String storageFileName = rootDir + sillyFile.getFilePath();
            File storageFile = new File(storageFileName);
            File dir = new File(storageFile.getParent());
            if (!dir.exists()) dir.mkdirs();
            Files.write(storageFile.toPath(), fileContent);
        } catch (IOException e) {
            AppConfig.timestampedErrorPrint("Couldn't add file [" + sillyFile.getFilePath() + "] to storage.");
        }
    }

    public static void addFileToStorageVersioning(SillyFile originSillyFile, byte[] newContent) {
        try {
            byte[] fileContent = originSillyFile.getFileContent();
            String rootDir = AppConfig.myServentInfo.getStorage();
            String versionedFileName = rootDir + originSillyFile.getFilePath() + "~" + originSillyFile.getVersion();
            String newOriginFileName = rootDir + originSillyFile.getFilePath();
            File versionedFile = new File(versionedFileName);
            File newOriginFile = new File(newOriginFileName);
            File dir = new File(versionedFile.getParent());
            if (!dir.exists()) dir.mkdirs();
            Files.write(versionedFile.toPath(), fileContent);
            Files.write(newOriginFile.toPath(), newContent);
        } catch (IOException e) {
            AppConfig.timestampedErrorPrint("Couldn't add file [" + originSillyFile.getFilePath() + "] to storage.");
        }
    }

    public static void getFilesFromDir(SillyFile sillyFile, String path, boolean isPull) {
        String rootDir = isPull ? AppConfig.myServentInfo.getStorage() : AppConfig.myServentInfo.getRoot();
        rootDir += SEPARATOR + path;
        File file = new File(rootDir);
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                String fileName = path + SEPARATOR + f.getName();
                getFilesFromDir(sillyFile, fileName, isPull);
            }
        } else {
            try {
                byte[] fileContent = Files.readAllBytes(file.toPath());
                SillyFile subFile = new SillyFile(fileContent, path, new AtomicInteger(0));
                sillyFile.putToSillyMap(ChordState.chordHashDir(path), subFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
