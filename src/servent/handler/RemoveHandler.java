package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import app.silly_git.SillyFile;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.RemoveMessage;
import servent.message.util.MessageUtil;

import java.io.File;
import java.util.Map;

import static app.utils.FileUtils.removeFiles;

public class RemoveHandler implements MessageHandler {

    private Message clientMessage;

    public RemoveHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.REMOVE) {
            RemoveMessage message = (RemoveMessage) clientMessage;

            int hash = Integer.parseInt(message.getMessageText());
            SillyFile sillyFile = AppConfig.chordState.getValueMap().get(hash);
            ServentInfo myInfo = AppConfig.myServentInfo;
            ServentInfo nextNodeInfo = AppConfig.chordState.getNextNodeForKey(hash);

            if (AppConfig.chordState.getValueMap().containsKey(hash)) {
                String path = sillyFile.isDirectory() ? sillyFile.getDirectoryPath() : sillyFile.getFilePath();
                File file = new File(myInfo.getRoot() + path);
                if (file.isDirectory()) {
                    for (Map.Entry<Integer, SillyFile> entry : sillyFile.getSillyFiles().entrySet()) {
                        // Send remove message for each file separately
                        message = message.changeReceiver(myInfo.getListenerPort(), nextNodeInfo.getListenerPort());
                        MessageUtil.sendMessage(message);
                    }
                }
                // Remove file/dir from root, origin and map
                removeFiles(myInfo, path, hash);
                if (file.isDirectory()) {
                    file.delete();
                    new File(myInfo.getStorage() + path).delete();
                }
                AppConfig.timestampedStandardPrint("Removed file [" + path + "] from system.");
            } else {
                message = message.changeReceiver(myInfo.getListenerPort(), nextNodeInfo.getListenerPort());
                MessageUtil.sendMessage(message);
            }
        }
    }
}
