package servent.handler;

import app.AppConfig;
import app.silly_git.SillyFile;
import servent.message.Message;
import servent.message.MessageType;

public class PutHandler implements MessageHandler {

    private Message clientMessage;

    public PutHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.PUT) {
            String[] splitText = clientMessage.getMessageText().split(":");
            if (splitText.length == 2) {
                int key;
                SillyFile value;

                try {
                    key = Integer.parseInt(splitText[0]);
                    value = new SillyFile(Integer.parseInt(splitText[1]));

                    AppConfig.chordState.putValue(key, value);
                } catch (NumberFormatException e) {
                    AppConfig.timestampedErrorPrint("Got put message with bad text: " + clientMessage.getMessageText());
                }
            } else {
                AppConfig.timestampedErrorPrint("Got put message with bad text: " + clientMessage.getMessageText());
            }


        } else {
            AppConfig.timestampedErrorPrint("Put handler got a message that is not PUT");
        }

    }

}
