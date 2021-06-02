package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import app.silly_git.SillyFile;
import servent.message.AddMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.util.MessageUtil;

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
            String filePath = sillyFile.getFile().getPath();
            int hash = Integer.parseInt(message.getMessageText());

            if (AppConfig.chordState.isKeyMine(hash)) {
                if (AppConfig.chordState.getValueMap().containsKey(hash)) {
                    AppConfig.timestampedErrorPrint("Hash [" + hash + "] for file [" + filePath + "] already exists.");
                } else {
                    AppConfig.timestampedStandardPrint("Hash [" + hash + "] belongs to me. Adding...");
                    AppConfig.chordState.getValueMap().put(hash, sillyFile);
                    addFileToStorage(sillyFile.getFile());
                }
            } else {
                AppConfig.timestampedStandardPrint("Hash [" + hash + "] doesn't belongs to me. Sending...");
                ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(hash);
                // TODO: Send Add message
                AddMessage sendingMessage = new AddMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), String.valueOf(hash), sillyFile);
                MessageUtil.sendMessage(sendingMessage);
            }
        }
    }
}
