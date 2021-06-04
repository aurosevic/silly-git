package servent.message;

import app.ServentInfo;

public class PullMessage extends BasicMessage {

    private static final long serialVersionUID = -8558031124520315033L;
    private ServentInfo originalSender;
    private int version;

    public PullMessage(ServentInfo originalSender, int senderPort, int receiverPort, String text, int version) {
        super(MessageType.PULL, senderPort, receiverPort, text);
        this.originalSender = originalSender;
        this.version = version;
    }

    public PullMessage changeReceiver(PullMessage message, int newSenderPort, int newReceiverPort) {
        return new PullMessage(message.getOriginalSender(), newSenderPort, newReceiverPort, message.getMessageText(), message.getVersion());
    }

    public ServentInfo getOriginalSender() {
        return originalSender;
    }

    public int getVersion() {
        return version;
    }
}
