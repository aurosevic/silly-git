package servent.message;

import app.ServentInfo;

public class PullMessage extends BasicMessage {

    private static final long serialVersionUID = -8558031124520315033L;
    private ServentInfo originalSender;

    public PullMessage(ServentInfo originalSender, int senderPort, int receiverPort, String text) {
        super(MessageType.PULL, senderPort, receiverPort, text);
        this.originalSender = originalSender;
    }

    public PullMessage changeReceiver(PullMessage message, int newSenderPort, int newReceiverPort) {
        return new PullMessage(message.getOriginalSender(), newSenderPort, newReceiverPort, message.getMessageText());
    }

    public ServentInfo getOriginalSender() {
        return originalSender;
    }
}
