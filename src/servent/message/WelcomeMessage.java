package servent.message;

import app.silly_git.SillyFile;

import java.util.Map;

public class WelcomeMessage extends BasicMessage {

    private static final long serialVersionUID = -8981406250652693908L;

    private Map<Integer, SillyFile> values;

    public WelcomeMessage(int senderPort, int receiverPort, Map<Integer, SillyFile> values) {
        super(MessageType.WELCOME, senderPort, receiverPort);

        this.values = values;
    }

    public Map<Integer, SillyFile> getValues() {
        return values;
    }
}
