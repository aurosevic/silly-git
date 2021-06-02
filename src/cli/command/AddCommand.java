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

    private String getStorageEquivalentForRoot(File file) {
        String filePath = file.getPath();
        return file.getPath().replaceFirst("root", "storage");
    }
}
