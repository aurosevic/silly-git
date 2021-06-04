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
import java.util.Scanner;

import static app.utils.FileUtils.addFileToStorage;
import static app.utils.FileUtils.addFileToStorageVersioning;

public class CommitCommand implements CLICommand {
    @Override
    public String commandName() {
        return "commit";
    }

    @Override
    public void execute(String args) {
        ServentInfo myInfo = AppConfig.myServentInfo;

        String fileName = myInfo.getRoot() + "\\" + args;
        File file = new File(fileName);

        try {
            if (file.exists()) {
                int hash = ChordState.chordHashDir(fileName);
                byte[] newContent = Files.readAllBytes(file.toPath());

                if (AppConfig.chordState.getValueMap().containsKey(hash)) {
                    // File belongs to me
                    SillyFile mySillyFile = AppConfig.chordState.getValueMap().get(hash);

                    if (newContent == mySillyFile.getFileContent()) {
                        // No conflict
                        AppConfig.timestampedStandardPrint("Files are identical.");
                    } else {
                        // With conflict
                        Scanner sc = new Scanner(System.in);
                        boolean working = true;
                        while (working) {
                            String line = sc.nextLine();
                            switch (line) {
                                case "view" -> {
                                    String fileContent = new String(mySillyFile.getFileContent());
                                    AppConfig.timestampedStandardPrint(fileContent);
                                    AppConfig.timestampedStandardPrint("push -> push my file to origin | pull -> get file from origin.");
                                }
                                case "push" -> {
                                    addFileToStorageVersioning(mySillyFile);
                                    mySillyFile.setFileContent(newContent);
                                    mySillyFile.setVersion(mySillyFile.getVersion() + 1);
                                    AppConfig.chordState.getValueMap().remove(hash);
                                    AppConfig.chordState.getValueMap().put(hash, mySillyFile);
                                    working = false;
                                }
                                case "pull" -> {
                                    addFileToStorage(mySillyFile, true);
                                    working = false;
                                }
                                default -> AppConfig.timestampedErrorPrint("Unsupported command. Use: view, push or pull.");
                            }
                        }
                        sc.close();
                    }
                } else {
                    ServentInfo nextNodeInfo = AppConfig.chordState.getNextNodeForKey(hash);
                    CommitMessage message = new CommitMessage(myInfo, myInfo.getListenerPort(), nextNodeInfo.getListenerPort(), String.valueOf(hash), newContent);
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
