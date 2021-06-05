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
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import static app.utils.FileUtils.*;

public class CommitCommand implements CLICommand {
    Map<String, byte[]> localContentOfFilesInDir = new ConcurrentHashMap<>(); // For dirs that are not mine

    @Override
    public String commandName() {
        return "commit";
    }

    @Override
    public void execute(String args) {
        ServentInfo myInfo = AppConfig.myServentInfo;

        String fileName = myInfo.getRoot() + args;
        File file = new File(fileName);

        try {
            if (file.exists()) {
                int hash = ChordState.chordHashDir(args);
                Object localContent = null;
                if (file.isDirectory() && !AppConfig.chordState.getValueMap().containsKey(hash)) {
                    getFileContent(localContentOfFilesInDir, file, myInfo);
                    localContent = localContentOfFilesInDir;
                } else if (!file.isDirectory()) {
                    localContent = Files.readAllBytes(file.toPath());
                }

                if (AppConfig.chordState.getValueMap().containsKey(hash)) {
                    // File belongs to me
                    byte[] localContentBytes = ((byte[]) localContent);
                    SillyFile originSillyFile = AppConfig.chordState.getValueMap().get(hash);

                    if (file.isDirectory()) {
                        for (Map.Entry<Integer, SillyFile> entry : originSillyFile.getSillyFiles().entrySet()) {
                            originSillyFile = entry.getValue();
                            File rootFile = new File(myInfo.getRoot() + originSillyFile.getFilePath());
                            if (rootFile.exists()) {
                                localContentBytes = Files.readAllBytes(rootFile.toPath());
                                if (Arrays.equals(localContentBytes, originSillyFile.getFileContent())) {
                                    // No conflict
                                    AppConfig.timestampedStandardPrint("Files are identical. Version is not changing.");
                                } else {
                                    // With conflict
                                    AppConfig.timestampedStandardPrint("File [" + originSillyFile.getFilePath() + "] has conflict.");
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
                                                message = "Content of file [" + originSillyFile.getFilePath() + "] from origin:\n" + fileContent;
                                                AppConfig.timestampedStandardPrint(message);
                                            }
                                            case "push" -> {
                                                addFileToStorageVersioning(originSillyFile, localContentBytes);
                                                originSillyFile.setFileContent(localContentBytes);
                                                originSillyFile.incrementVersion();
                                                AppConfig.timestampedStandardPrint("Pushed file [" + originSillyFile.getFilePath() + "] to origin.");
                                                working = false;
                                            }
                                            case "pull" -> {
                                                addFileToStorage(originSillyFile, true);
                                                AppConfig.timestampedStandardPrint("Pulled file [" + originSillyFile.getFilePath() + "] from origin.");
                                                working = false;
                                            }
                                            default -> AppConfig.timestampedErrorPrint("Unsupported command. " + message);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (Arrays.equals(localContentBytes, originSillyFile.getFileContent())) {
                            // No conflict
                            AppConfig.timestampedStandardPrint("Files are identical. Version is not changing.");
                        } else {
                            // With conflict
                            AppConfig.timestampedStandardPrint("File [" + args + "] has conflict.");
                            Scanner sc = new Scanner(System.in);
                            boolean working = true;
                            while (working) {
                                String cliMessage = """
                                        Please choose one of the commands:\s
                                        -> view: View the content of the file from origin.
                                        -> push: Push my file to the origin.
                                        -> pull: Get file from the origin.""";
                                AppConfig.timestampedStandardPrint(cliMessage);
                                String line = sc.nextLine();
                                switch (line) {
                                    case "view" -> {
                                        String fileContent = new String(originSillyFile.getFileContent());
                                        cliMessage = "Content of file [" + args + "] from origin:\n" + fileContent;
                                        AppConfig.timestampedStandardPrint(cliMessage);
                                    }
                                    case "push" -> {
                                        addFileToStorageVersioning(originSillyFile, localContentBytes);
                                        originSillyFile.setFileContent(localContentBytes);
                                        originSillyFile.incrementVersion();
                                        AppConfig.timestampedStandardPrint("Pushed file [" + args + "] to origin.");
                                        working = false;
                                    }
                                    case "pull" -> {
                                        addFileToStorage(originSillyFile, true);
                                        AppConfig.timestampedStandardPrint("Pulled file [" + args + "] from origin.");
                                        working = false;
                                    }
                                    default -> AppConfig.timestampedErrorPrint("Unsupported command. " + cliMessage);
                                }
                            }
                        }
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
