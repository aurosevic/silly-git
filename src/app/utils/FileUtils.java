package app.utils;

import app.AppConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtils {

    public static void addFileToStorage(File file) {
        try {
            if (file.isDirectory()) {
                String dirToMake = getStorageEquivalentForRoot(file);
                File dir = new File(dirToMake);
                if (!dir.exists()) dir.mkdirs();
                for (File f : file.listFiles()) {
                    addFileToStorage(f);
                }
            } else {
                byte[] fileContent = Files.readAllBytes(file.toPath());
                String storageFileName = getStorageEquivalentForRoot(file);
                File storageFile = new File(storageFileName);
                Files.write(storageFile.toPath(), fileContent);
            }
        } catch (IOException e) {
            AppConfig.timestampedErrorPrint("Couldn't add file [" + file.getPath() + "] to storage.");
        }
    }

    public static String getStorageEquivalentForRoot(File file) {
        String filePath = file.getPath();
        return file.getPath().replaceFirst("root", "storage");
    }
}
