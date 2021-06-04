package app.utils;

import app.AppConfig;
import app.ChordState;
import app.silly_git.SillyFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;

public class FileUtils {

    public static final String VERSION_PREFIX = "~";

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

    public static void addFileToStorageVersioning(SillyFile sillyFile) {
        try {
            byte[] fileContent = sillyFile.getFileContent();
            String rootDir = AppConfig.myServentInfo.getStorage();
            String storageFileName = rootDir + sillyFile.getFilePath() + "~" + sillyFile.getVersion();
            File storageFile = new File(storageFileName);
            File dir = new File(storageFile.getParent());
            if (!dir.exists()) dir.mkdirs();
            Files.write(storageFile.toPath(), fileContent);
        } catch (IOException e) {
            AppConfig.timestampedErrorPrint("Couldn't add file [" + sillyFile.getFilePath() + "] to storage.");
        }
    }

    public static void getFilesFromDir(SillyFile sillyFile, String path, boolean isPull) {
        String rootDir = isPull ? AppConfig.myServentInfo.getStorage() : AppConfig.myServentInfo.getRoot();
        rootDir += "\\" + path;
        File file = new File(rootDir);
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                String fileName = path + "\\" + f.getName();
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
