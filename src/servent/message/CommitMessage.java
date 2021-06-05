package servent.message;

import app.ServentInfo;

public class CommitMessage extends BasicMessage {

    private static final long serialVersionUID = -8558031124520315033L;
    private Object newContent;
    private ServentInfo originalSender;

    public CommitMessage(ServentInfo originalSender, int senderPort, int receiverPort, String text, Object newContent) {
        super(MessageType.COMMIT, senderPort, receiverPort, text);
        this.originalSender = originalSender;
        this.newContent = newContent;
    }

    public CommitMessage changeReceiver(CommitMessage message, int newSenderPort, int newReceiverPort) {
        return new CommitMessage(message.getOriginalSender(), newSenderPort, newReceiverPort, message.getMessageText(), message.getNewContent());
    }

    public CommitMessage changeContent(CommitMessage message, byte[] newContent) {
        return new CommitMessage(message.getOriginalSender(), message.getSenderPort(), message.getReceiverPort(), message.getMessageText(), newContent);
    }

    public CommitMessage changeText(CommitMessage message, String newText) {
        return new CommitMessage(message.getOriginalSender(), message.getSenderPort(), message.getReceiverPort(), newText, message.getNewContent());
    }

    public Object getNewContent() {
        return newContent;
    }

    public ServentInfo getOriginalSender() {
        return originalSender;
    }
}
