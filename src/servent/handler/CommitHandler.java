package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import app.silly_git.SillyFile;
import servent.message.AddMessage;
import servent.message.CommitMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.util.MessageUtil;

import java.util.Arrays;
import java.util.Scanner;

import static app.utils.FileUtils.addFileToStorageVersioning;

public class CommitHandler implements MessageHandler {

    private Message clientMessage;

    public CommitHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.COMMIT) {
            CommitMessage message = (CommitMessage) clientMessage;

            int hash = Integer.parseInt(message.getMessageText());

            ServentInfo myInfo = AppConfig.myServentInfo;
            ServentInfo nextNodeInfo = AppConfig.chordState.getNextNodeForKey(hash);

            if (AppConfig.chordState.getValueMap().containsKey(hash)) {
                SillyFile originSillyFile = AppConfig.chordState.getValueMap().get(hash);
                if (originSillyFile.isDirectory()) {

                } else {
                    if (Arrays.equals(message.getNewContent(), originSillyFile.getFileContent())) {
                        // No conflict
                        AppConfig.timestampedStandardPrint("Files are identical.");
                    } else {
                        // With conflict
                        String filePath = originSillyFile.getFilePath();
                        AppConfig.timestampedStandardPrint("File [" + filePath + "] has conflict.");
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
                                    cliMessage = "Content of file [" + filePath + "] from origin:\n" + fileContent;
                                    AppConfig.timestampedStandardPrint(cliMessage);
                                }
                                case "push" -> {
                                    addFileToStorageVersioning(originSillyFile, message.getNewContent());
                                    originSillyFile.setFileContent(message.getNewContent());
                                    originSillyFile.incrementVersion();
                                    AppConfig.timestampedStandardPrint("Pushed file [" + filePath + "] to origin.");
                                    working = false;
                                }
                                case "pull" -> {
                                    AddMessage addMessage = new AddMessage(myInfo.getListenerPort(), message.getOriginalSender().getListenerPort(), message.getMessageText(), originSillyFile, true);
                                    MessageUtil.sendMessage(addMessage);
                                    AppConfig.timestampedStandardPrint("Pulled file [" + filePath + "] from origin.");
                                    working = false;
                                }
                                default -> AppConfig.timestampedErrorPrint("Unsupported command. " + cliMessage);
                            }
                        }
                    }
                }
            } else {
                message = message.changeReceiver(message, myInfo.getListenerPort(), nextNodeInfo.getListenerPort());
                MessageUtil.sendMessage(message);
            }
        }
    }
}
