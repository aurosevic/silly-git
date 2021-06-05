package cli.command;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import app.silly_git.SillyFile;
import servent.message.RemoveMessage;
import servent.message.util.MessageUtil;

import java.io.File;
import java.util.Map;

import static app.utils.FileUtils.removeFiles;

public class RemoveCommand implements CLICommand {
    @Override
    public String commandName() {
        return "remove";
    }

    @Override
    public void execute(String args) {
        int hash = ChordState.chordHashDir(args);

        ServentInfo myInfo = AppConfig.myServentInfo;
        ServentInfo nextNodeInfo = AppConfig.chordState.getNextNodeForKey(hash);

        if (AppConfig.chordState.getValueMap().containsKey(hash)) {
            File file = new File(myInfo.getRoot() + args);
            if (file.isDirectory()) {
                SillyFile sillyFile = AppConfig.chordState.getValueMap().get(hash);
                for (Map.Entry<Integer, SillyFile> entry : sillyFile.getSillyFiles().entrySet()) {
                    // Send remove message for each file separately
                    RemoveMessage message = new RemoveMessage(myInfo.getListenerPort(), nextNodeInfo.getListenerPort(), String.valueOf(hash));
                    MessageUtil.sendMessage(message);
                }
            }
            // Remove file/dir from root, origin and map
            removeFiles(myInfo, args, hash);
            if (file.isDirectory()) {
                file.delete();
                new File(myInfo.getStorage() + args).delete();
            }
            AppConfig.timestampedStandardPrint("Removed file [" + args + "] from system.");
        } else {
            RemoveMessage message = new RemoveMessage(myInfo.getListenerPort(), nextNodeInfo.getListenerPort(), String.valueOf(hash));
            MessageUtil.sendMessage(message);
        }
    }
}
