package servent.message;

import app.silly_git.SillyFile;

public class AddMessage extends BasicMessage {

    private static final long serialVersionUID = -8558031124520315033L;
    private SillyFile file;

    public AddMessage(int senderPort, int receiverPort, String text, SillyFile file) {
        super(MessageType.ADD, senderPort, receiverPort, text);
        this.file = file;
    }

    public SillyFile getFile() {
        return file;
    }
}
