package cli.command;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import app.silly_git.SillyFile;
import servent.message.CommitMessage;
import servent.message.util.MessageUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Scanner;

import static app.utils.FileUtils.*;

public class CommitCommand implements CLICommand {
    @Override
    public String commandName() {
        return "commit";
    }

    @Override
    public void execute(String args) {
        ServentInfo myInfo = AppConfig.myServentInfo;

        String fileName = myInfo.getRoot() + SEPARATOR + args;
        File file = new File(fileName);

        try {
            if (file.exists()) {
                int hash = ChordState.chordHashDir(args);
                byte[] localContent = Files.readAllBytes(file.toPath());

                if (AppConfig.chordState.getValueMap().containsKey(hash)) {
                    // File belongs to me
                    SillyFile originSillyFile = AppConfig.chordState.getValueMap().get(hash);
                    if (Arrays.equals(localContent, originSillyFile.getFileContent())) {
                        // No conflict
                        AppConfig.timestampedStandardPrint("Files are identical. Version is not changing.");
                    } else {
                        // With conflict
                        AppConfig.timestampedStandardPrint("File [" + args + "] has conflict.");
                        Scanner sc = new Scanner(System.in);
                        boolean working = true;
                        while (working) {
                            String message = """
                                    Please choose one of the commands:\s
                                    -> view: View the content of the file from origin.
                                    -> push: Push my file to the origin.
                                    -> pull: Get file from the origin.""";
                            AppConfig.timestampedStandardPrint(message);
                            String line = sc.nextLine();
                            switch (line) {
                                case "view" -> {
                                    String fileContent = new String(originSillyFile.getFileContent());
                                    message = "Content of file [" + args + "] from origin:\n" + fileContent;
                                    AppConfig.timestampedStandardPrint(message);
                                }
                                case "push" -> {
                                    addFileToStorageVersioning(originSillyFile, localContent);
                                    originSillyFile.setFileContent(localContent);
                                    originSillyFile.incrementVersion();
                                    AppConfig.timestampedStandardPrint("Pushed file [" + args + "] to origin.");
//                                    AppConfig.chordState.getValueMap().remove(hash);
//                                    AppConfig.chordState.getValueMap().put(hash, originSillyFile);
                                    working = false;
                                }
                                case "pull" -> {
                                    addFileToStorage(originSillyFile, true);
                                    AppConfig.timestampedStandardPrint("Pulled file [" + args + "] from origin.");
                                    working = false;
                                }
                                default -> AppConfig.timestampedErrorPrint("Unsupported command. " + message);
                            }
                        }
//                        sc.close();
                    }
                } else {
                    ServentInfo nextNodeInfo = AppConfig.chordState.getNextNodeForKey(hash);
                    CommitMessage message = new CommitMessage(myInfo, myInfo.getListenerPort(), nextNodeInfo.getListenerPort(), String.valueOf(hash), localContent);
                    MessageUtil.sendMessage(message);
                }
            } else {
                AppConfig.timestampedErrorPrint("File: [" + args + "] doesn't exist.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
