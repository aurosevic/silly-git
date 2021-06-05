package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import app.silly_git.SillyFile;
import servent.message.AddMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PullMessage;
import servent.message.util.MessageUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static app.utils.FileUtils.VERSION_PREFIX;

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
            SillyFile sillyFile = AppConfig.chordState.getValueMap().get(hash);
            try {
                if (AppConfig.chordState.getValueMap().containsKey(hash)) {
                    if (sillyFile.isDirectory()) {
                        for (Map.Entry<Integer, SillyFile> entry : sillyFile.getSillyFiles().entrySet()) {
                            String text = String.valueOf(entry.getKey());
                            message = message.changeText(message, text);
                            message = message.changeReceiver(message, myInfo.getListenerPort(), nextNodeInfo.getListenerPort());
                            MessageUtil.sendMessage(message);
                        }
                    } else {
                        if (version == -1) {
                            // Get the latest version
                            AddMessage addMessage = new AddMessage(myInfo.getListenerPort(), message.getOriginalSender().getListenerPort(), String.valueOf(hash), sillyFile, true);
                            MessageUtil.sendMessage(addMessage);
                        } else {
                            // Get the specified version of the file
                            String filePath = myInfo.getStorage() + sillyFile.getFilePath() + VERSION_PREFIX + version;
                            File testFile = new File(filePath);
                            if (testFile.exists()) {
                                byte[] newContent = Files.readAllBytes(Path.of(filePath));
                                sillyFile = sillyFile.changeContent(sillyFile, newContent);
                                AddMessage addMessage = new AddMessage(myInfo.getListenerPort(), message.getOriginalSender().getListenerPort(), String.valueOf(hash), sillyFile, true);
                                MessageUtil.sendMessage(addMessage);
                            } else {
                                AppConfig.timestampedErrorPrint("File [" + sillyFile.getFilePath() + "] with version [" + version + "] doesn't exist.");
                            }
                        }
                    }
                } else {
                    message = message.changeReceiver(message, myInfo.getListenerPort(), nextNodeInfo.getListenerPort());
                    MessageUtil.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
