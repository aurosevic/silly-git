package cli.command;

import app.AppConfig;
import app.ChordState;
import app.silly_git.SillyFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class AddCommand implements CLICommand {

    @Override
    public String commandName() {
        return "add";
    }

    @Override
    public void execute(String args) {
        String filePath = AppConfig.myServentInfo.getRoot() + args;
        File file = new File(filePath);
        if (file.exists()) {
            int hash = ChordState.chordHashDir(args);
            AppConfig.timestampedStandardPrint("Hash for file: [" + args + "] -> [" + hash + "]");

            if (AppConfig.chordState.isKeyMine(hash)) {
                if (AppConfig.chordState.getValueMap().containsKey(hash)) {
                    AppConfig.timestampedErrorPrint("Hash [" + hash + "] for file [" + filePath + "] already exists.");
                } else {
                    AppConfig.timestampedStandardPrint("Hash [" + hash + "] belongs to me. Adding...");
                    SillyFile sillyFile = new SillyFile(args, file.isDirectory());
                    AppConfig.chordState.getValueMap().put(hash, sillyFile);
                    addFileToStorage(file);
                }
            } else {
                AppConfig.timestampedStandardPrint("Hash [" + hash + "] doesn't belongs to me. Sending...");
                AppConfig.chordState.getNextNodeForKey(hash);
                // TODO: Send Add message
            }
        } else {
            AppConfig.timestampedErrorPrint("File with path: " + filePath + " doesn't exist.");
        }
    }

    private void addFileToStorage(File file) {
        try {
            if (file.isDirectory()) {
                String storage = AppConfig.myServentInfo.getStorage();
                String parent = storage.substring(storage.indexOf("storage") + 8, storage.length() - 1);
                String dirToMake = AppConfig.myServentInfo.getStorage() + file.getPath().substring(file.getPath().indexOf(parent) + parent.length());
                File dir = new File(dirToMake);
                if (!dir.exists()) dir.mkdirs();
                for (File f : file.listFiles()) {
                    addFileToStorage(f);
                }
            } else {
                byte[] fileContent = Files.readAllBytes(file.toPath());
                String storageFileName = AppConfig.myServentInfo.getStorage() + file.getPath().substring(file.getPath().indexOf("root") + "root".length());
                File storageFile = new File(storageFileName);
                Files.write(storageFile.toPath(), fileContent);
            }
        } catch (IOException e) {
            AppConfig.timestampedErrorPrint("Couldn't add file [" + file.getPath() + "] to storage.");
        }
    }

    private String getStorageEquivalentForRoot(File file) {
        String filePath = file.getPath();
        return file.getPath().replaceFirst("root", "storage");
    }
}
