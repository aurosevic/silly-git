package cli.command;

import app.AppConfig;
import app.ChordState;
import app.silly_git.SillyFile;

import java.io.File;

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
                    // TODO: Add to file system

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
}
