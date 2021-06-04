package cli.command;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import app.silly_git.SillyFile;
import servent.message.AddMessage;
import servent.message.PullMessage;
import servent.message.util.MessageUtil;

import java.io.File;
import java.util.Map;

import static app.utils.FileUtils.getFilesFromDir;

public class PullCommand implements CLICommand {
    @Override
    public String commandName() {
        return "pull";
    }

    @Override
    public void execute(String args) {
        String[] argsArray = args.split(" ");
        String fileName = argsArray[0];
        int version = -1;
        if (argsArray.length > 1) version = Integer.parseInt(argsArray[1]);

        int hash = ChordState.chordHashDir(fileName);

        ServentInfo myInfo = AppConfig.myServentInfo;
        ServentInfo nextNodeInfo = AppConfig.chordState.getNextNodeForKey(hash);

        if (AppConfig.chordState.getValueMap().containsKey(hash)) {
            File testFile = new File(myInfo.getRoot() + fileName);
            if (testFile.exists() && !testFile.isDirectory()) {
                AppConfig.timestampedErrorPrint("I already have file: [" + fileName + "]");
            } else {
                // I have file in storage, but not in the root OR it was a dir and I'm checking in case there's more
                // files in that directory than what I have
                SillyFile sillyFile = AppConfig.chordState.getValueMap().get(hash);
                if (sillyFile.isDirectory()) {
                    getFilesFromDir(sillyFile, fileName, true);
                    for (Map.Entry<Integer, SillyFile> entry : sillyFile.getSillyFiles().entrySet()) {
                        SillyFile subFile = entry.getValue();
                        testFile = new File(myInfo.getRoot() + subFile.getFilePath());
                        if (!testFile.exists()) {
                            AddMessage addMessage = new AddMessage(myInfo.getListenerPort(), myInfo.getListenerPort(), String.valueOf(entry.getKey()), subFile, true);
                            MessageUtil.sendMessage(addMessage);
                        } else AppConfig.timestampedErrorPrint("I already have file: [" + fileName + "]");
                    }
                } else {
                    AddMessage addMessage = new AddMessage(myInfo.getListenerPort(), myInfo.getListenerPort(), String.valueOf(hash), sillyFile, true);
                    MessageUtil.sendMessage(addMessage);
                }
            }
        } else {
            PullMessage message = new PullMessage(myInfo, myInfo.getListenerPort(), nextNodeInfo.getListenerPort(), String.valueOf(hash), version);
            MessageUtil.sendMessage(message);
        }
    }
}
