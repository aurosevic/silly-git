package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import app.silly_git.SillyFile;
import servent.message.AddMessage;
import servent.message.CommitMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.util.MessageUtil;

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
                SillyFile mySillyFile = AppConfig.chordState.getValueMap().get(hash);
                if (message.getNewContent() == mySillyFile.getFileContent()) {
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
                                mySillyFile.setFileContent(message.getNewContent());
                                mySillyFile.incrementVersion();
                                AppConfig.chordState.getValueMap().remove(hash);
                                AppConfig.chordState.getValueMap().put(hash, mySillyFile);
                                working = false;
                            }
                            case "pull" -> {
                                AddMessage addMessage = new AddMessage(myInfo.getListenerPort(), message.getOriginalSender().getListenerPort(), message.getMessageText(), mySillyFile, true);
                                MessageUtil.sendMessage(addMessage);
                                working = false;
                            }
                            default -> AppConfig.timestampedErrorPrint("Unsupported command. Use: view, push or pull.");
                        }
                    }
                    sc.close();
                }
            } else {
                message = message.changeReceiver(message, myInfo.getListenerPort(), nextNodeInfo.getListenerPort());
                MessageUtil.sendMessage(message);
            }
        }
    }
}
