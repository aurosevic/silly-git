package app.utils;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import app.silly_git.SillyFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    public static void getFileContent(Map<String, byte[]> contentFile, File file, ServentInfo myInfo) {
        try {
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    getFileContent(contentFile, f, myInfo);
                }
            } else {
                byte[] content = Files.readAllBytes(file.toPath());
                String key = file.getPath().substring(file.getPath().indexOf(myInfo.getRoot()) + myInfo.getRoot().length());
                contentFile.put(key, content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] getContentForFile(ConcurrentHashMap<String, byte[]> map, String fileName) {
        if (map.containsKey(fileName)) return map.get(fileName);
        else return new byte[]{};
    }

    public static void removeFiles(ServentInfo myInfo, String path, int hash) {
        File rootFile = new File(myInfo.getRoot() + path);
        File storageFile = new File(myInfo.getStorage() + path);
        if (storageFile.isDirectory()) {
            for (File f : storageFile.listFiles()) {
                removeFiles(myInfo, path + SEPARATOR + f.getName(), hash);
                f.delete();
                new File(f.getPath().replace(SEPARATOR + "storage" + SEPARATOR, SEPARATOR + "root" + SEPARATOR)).delete();
            }
        } else {
            rootFile.delete();
            storageFile.delete();
            AppConfig.chordState.getValueMap().remove(hash);
        }
    }
}
