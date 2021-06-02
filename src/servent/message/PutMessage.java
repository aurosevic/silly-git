package servent.message;

import app.silly_git.SillyFile;

public class PutMessage extends BasicMessage {

    private static final long serialVersionUID = 5163039209888734276L;

    public PutMessage(int senderPort, int receiverPort, int key, SillyFile value) {
        super(MessageType.PUT, senderPort, receiverPort, key + ":" + value);
    }
}
