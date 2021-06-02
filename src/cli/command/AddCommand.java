package cli.command;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import app.silly_git.SillyFile;
import servent.message.AddMessage;
import servent.message.util.MessageUtil;

import java.io.File;

import static app.utils.FileUtils.addFileToStorage;

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

            SillyFile sillyFile = new SillyFile(file);
            if (AppConfig.chordState.isKeyMine(hash)) {
                if (AppConfig.chordState.getValueMap().containsKey(hash)) {
                    AppConfig.timestampedErrorPrint("Hash [" + hash + "] for file [" + filePath + "] already exists.");
                } else {
                    AppConfig.timestampedStandardPrint("Hash [" + hash + "] belongs to me. Adding...");
                    AppConfig.chordState.getValueMap().put(hash, sillyFile);
                    addFileToStorage(file);
                }
            } else {
                AppConfig.timestampedStandardPrint("Hash [" + hash + "] doesn't belongs to me. Sending...");
                ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(hash);
                // TODO: Send Add message
                AddMessage message = new AddMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), String.valueOf(hash), sillyFile);
                MessageUtil.sendMessage(message);
            }
        } else {
            AppConfig.timestampedErrorPrint("File with path: " + filePath + " doesn't exist.");
        }
    }


}
