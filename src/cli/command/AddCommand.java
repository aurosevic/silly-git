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
import java.util.regex.Pattern;

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
        try {
            if (file.exists()) {
                String pattern = Pattern.quote(System.getProperty("file.separator"));
                int hash = ChordState.chordHashDir(args.split(pattern)[0]);
                AppConfig.timestampedStandardPrint("Hash for file: [" + args + "] -> [" + hash + "]");

                SillyFile sillyFile;
                if (file.isDirectory()) {
                    for (File f : file.listFiles()) {
                        execute(args + "\\" + f.getName());
                    }
                } else {
                    sillyFile = new SillyFile(Files.readAllBytes(Path.of(filePath)), args);

                    if (AppConfig.chordState.isKeyMine(hash)) {
                        if (new File(AppConfig.myServentInfo.getStorage() + args).exists()) {
                            AppConfig.timestampedErrorPrint("Hash [" + hash + "] for file [" + filePath + "] already exists.");
                        } else {
                            AppConfig.timestampedStandardPrint("Hash [" + hash + "] belongs to me. Adding...");
                            AppConfig.chordState.getValueMap().put(hash, sillyFile);
                            addFileToStorage(sillyFile);
                        }
                    } else {
                        AppConfig.timestampedStandardPrint("Hash [" + hash + "] doesn't belongs to me. Sending...");
                        ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(hash);
                        AddMessage message = new AddMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), String.valueOf(hash), sillyFile);
                        MessageUtil.sendMessage(message);
                    }
                }
            } else {
                AppConfig.timestampedErrorPrint("File with path: " + filePath + " doesn't exist.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
