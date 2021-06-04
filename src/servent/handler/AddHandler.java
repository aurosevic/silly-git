package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import app.silly_git.SillyFile;
import servent.message.AddMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.util.MessageUtil;

import java.io.File;

import static app.utils.FileUtils.addFileToStorage;

public class AddHandler implements MessageHandler {

    private Message clientMessage;

    public AddHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.ADD) {
            AddMessage message = (AddMessage) clientMessage;

            SillyFile sillyFile = message.getFile();
            String filePath = sillyFile.isDirectory() ? sillyFile.getDirectoryPath() : sillyFile.getFilePath();
            int hash = Integer.parseInt(message.getMessageText());

            ServentInfo myInfo = AppConfig.myServentInfo;

            if (message.isPull()) {
                if (!sillyFile.isDirectory()) addFileToStorage(sillyFile, true);
            } else {
                if (AppConfig.chordState.isKeyMine(hash)) {
                    if (new File(myInfo.getStorage() + filePath).exists()) {
                        AppConfig.timestampedErrorPrint("Hash [" + hash + "] for file/dir [" + filePath + "] already exists.");
                    } else {
                        AppConfig.timestampedStandardPrint("Hash [" + hash + "] belongs to me. Adding...");
                        AppConfig.chordState.getValueMap().put(hash, sillyFile);
                        if (!sillyFile.isDirectory()) addFileToStorage(sillyFile, false);
                    }
                } else {
                    AppConfig.timestampedStandardPrint("Hash [" + hash + "] doesn't belongs to me. Sending...");
                    ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(hash);
                    AddMessage sendingMessage = new AddMessage(myInfo.getListenerPort(), nextNode.getListenerPort(), String.valueOf(hash), sillyFile, false);
                    MessageUtil.sendMessage(sendingMessage);
                }
            }
        }
    }
}
