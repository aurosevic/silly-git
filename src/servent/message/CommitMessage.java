package servent.message;

import app.ServentInfo;

public class CommitMessage extends BasicMessage {

    private static final long serialVersionUID = -8558031124520315033L;
    private byte[] newContent;
    private int currentVersion;
    private ServentInfo originalSender;

    public CommitMessage(ServentInfo originalSender, int senderPort, int receiverPort, String text, byte[] newContent) {
        super(MessageType.COMMIT, senderPort, receiverPort, text);
        this.originalSender = originalSender;
        this.newContent = newContent;
        this.currentVersion = currentVersion;
    }

    public CommitMessage changeReceiver(CommitMessage message, int newSenderPort, int newReceiverPort) {
        return new CommitMessage(message.getOriginalSender(), newSenderPort, newReceiverPort, message.getMessageText(), message.getNewContent());
    }

    public byte[] getNewContent() {
        return newContent;
    }

    public int getCurrentVersion() {
        return currentVersion;
    }

    public ServentInfo getOriginalSender() {
        return originalSender;
    }
}
