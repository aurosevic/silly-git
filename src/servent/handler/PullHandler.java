package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import app.silly_git.SillyFile;
import servent.message.AddMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.PullMessage;
import servent.message.util.MessageUtil;

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

            ServentInfo myInfo = AppConfig.myServentInfo;
            ServentInfo nextNodeInfo = AppConfig.chordState.getNextNodeForKey(hash);

            if (AppConfig.chordState.getValueMap().containsKey(hash)) {
                SillyFile sillyFile = AppConfig.chordState.getValueMap().get(hash);
                AddMessage addMessage = new AddMessage(myInfo.getListenerPort(), message.getOriginalSender().getListenerPort(), String.valueOf(hash), sillyFile, true);
                MessageUtil.sendMessage(addMessage);
            } else {
                message = message.changeReceiver(message, myInfo.getListenerPort(), nextNodeInfo.getListenerPort());
                MessageUtil.sendMessage(message);
            }
        }
    }
}
