package servent.message;

public class RemoveMessage extends BasicMessage {

    private static final long serialVersionUID = -8558031124520315033L;

    public RemoveMessage(int senderPort, int receiverPort, String text) {
        super(MessageType.REMOVE, senderPort, receiverPort, text);
    }

    public RemoveMessage changeReceiver(int newSender, int newReceiver) {
        return new RemoveMessage(newSender, newReceiver, this.getMessageText());
    }
}
