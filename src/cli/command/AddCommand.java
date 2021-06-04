package cli.command;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import app.silly_git.SillyFile;
import servent.message.AddMessage;
import servent.message.util.MessageUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static app.utils.FileUtils.addFileToStorage;
import static app.utils.FileUtils.getFilesFromDir;

public class AddCommand implements CLICommand {

    @Override
    public String commandName() {
        return "add";
    }

    @Override
    public void execute(String args) {
        ServentInfo myInfo = AppConfig.myServentInfo;

        int version = 0;
        String[] argArray = args.split(" ");
        args = argArray[0];
        if (argArray.length > 1) version = Integer.parseInt(argArray[1]);

        String filePath = myInfo.getRoot() + args;
        File file = new File(filePath);
        try {
            if (file.exists()) {
                int hash = ChordState.chordHashDir(args);
                ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(hash);

                if (file.isDirectory()) {
                    SillyFile sillyFile = new SillyFile(args);
                    getFilesFromDir(sillyFile, args, false);
                    for (Map.Entry<Integer, SillyFile> entry : sillyFile.getSillyFiles().entrySet()) {
                        SillyFile subFile = entry.getValue();
                        execute(subFile.getFilePath() + " " + subFile.getVersion());
                    }
                    AppConfig.timestampedStandardPrint("Hash for dir: [" + args + "] -> [" + hash + "]");
                    if (AppConfig.chordState.isKeyMine(hash)) {
                        AppConfig.timestampedStandardPrint("Hash [" + hash + "] belongs to me. Adding...");
                        AppConfig.chordState.getValueMap().put(hash, sillyFile);
                        // Note: No need to addFileToStorage, they will be added separately
                    } else {
                        AppConfig.timestampedStandardPrint("Hash [" + hash + "] doesn't belongs to me. Sending...");
                        AddMessage message = new AddMessage(myInfo.getListenerPort(), nextNode.getListenerPort(), String.valueOf(hash), sillyFile, false);
                        MessageUtil.sendMessage(message);
                    }
                } else {
                    AppConfig.timestampedStandardPrint("Hash for file: [" + args + "] -> [" + hash + "]");
                    SillyFile sillyFile = new SillyFile(Files.readAllBytes(Path.of(filePath)), args, new AtomicInteger(version));

                    if (AppConfig.chordState.isKeyMine(hash)) {
                        if (new File(myInfo.getStorage() + args).exists()) {
                            AppConfig.timestampedErrorPrint("Hash [" + hash + "] for file [" + filePath + "] already exists.");
                        } else {
                            AppConfig.timestampedStandardPrint("Hash [" + hash + "] belongs to me. Adding...");
                            AppConfig.chordState.getValueMap().put(hash, sillyFile);
                            addFileToStorage(sillyFile, false);
                        }
                    } else {
                        AppConfig.timestampedStandardPrint("Hash [" + hash + "] doesn't belongs to me. Sending...");
                        AddMessage message = new AddMessage(myInfo.getListenerPort(), nextNode.getListenerPort(), String.valueOf(hash), sillyFile, false);
                        MessageUtil.sendMessage(message);
                    }
                }
            } else {
                AppConfig.timestampedErrorPrint("File/dir with path: " + filePath + " doesn't exist.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
