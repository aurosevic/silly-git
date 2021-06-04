package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import app.silly_git.SillyFile;
import servent.message.AddMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PullMessage;
import servent.message.util.MessageUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PullHandler implements MessageHandler {

    private Message clientMessage;

    public PullHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.PULL) {
            PullMessage message = (PullMessage) clientMessage;

            int hash = Integer.parseInt(message.getMessageText());
            int version = message.getVersion();

            ServentInfo myInfo = AppConfig.myServentInfo;
            ServentInfo nextNodeInfo = AppConfig.chordState.getNextNodeForKey(hash);

            if (AppConfig.chordState.getValueMap().containsKey(hash)) {
                SillyFile sillyFile = AppConfig.chordState.getValueMap().get(hash);
                if (version == -1) {
                    AddMessage addMessage = new AddMessage(myInfo.getListenerPort(), message.getOriginalSender().getListenerPort(), String.valueOf(hash), sillyFile, true);
                    MessageUtil.sendMessage(addMessage);
                } else {
                    try {
                        if (sillyFile.isDirectory()) {
                            // TODO: Handle directory
                        } else {
                            String filePath = myInfo.getStorage() + sillyFile.getFilePath() + "~" + version;
                            byte[] newContent = Files.readAllBytes(Path.of(filePath));
                            sillyFile = sillyFile.changeContent(sillyFile, newContent);
                            AddMessage addMessage = new AddMessage(myInfo.getListenerPort(), message.getOriginalSender().getListenerPort(), String.valueOf(hash), sillyFile, true);
                            MessageUtil.sendMessage(addMessage);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                message = message.changeReceiver(message, myInfo.getListenerPort(), nextNodeInfo.getListenerPort());
                MessageUtil.sendMessage(message);
            }
        }
    }
}
