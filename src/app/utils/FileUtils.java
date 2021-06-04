package app.utils;

import app.AppConfig;
import app.silly_git.SillyFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtils {

    public static void addFileToStorage(SillyFile sillyFile, boolean isPull) {
        try {
            byte[] fileContent = sillyFile.getFileContent();
            String rootDir;
            if (isPull) rootDir = AppConfig.myServentInfo.getRoot();
            else rootDir = AppConfig.myServentInfo.getStorage();
            String storageFileName = rootDir + sillyFile.getFilePath();
            File storageFile = new File(storageFileName);
            File dir = new File(storageFile.getParent());
            if (!dir.exists()) dir.mkdirs();
            Files.write(storageFile.toPath(), fileContent);
        } catch (IOException e) {
            AppConfig.timestampedErrorPrint("Couldn't add file [" + sillyFile.getFilePath() + "] to storage.");
        }
    }
}
