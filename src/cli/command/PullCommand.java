package cli.command;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import servent.message.PullMessage;
import servent.message.util.MessageUtil;

public class PullCommand implements CLICommand {
    @Override
    public String commandName() {
        return "pull";
    }

    @Override
    public void execute(String args) {
        String[] argsArray = args.split(" ");
        String fileName = argsArray[0];
        int version = -1;
        if (argsArray.length > 1) version = Integer.parseInt(argsArray[1]);

        int hash = ChordState.chordHashDir(fileName);

        ServentInfo myInfo = AppConfig.myServentInfo;
        ServentInfo nextNodeInfo = AppConfig.chordState.getNextNodeForKey(hash);

        if (AppConfig.chordState.getValueMap().containsKey(hash)) {
            AppConfig.timestampedErrorPrint("I already have this file.");
        } else {
            PullMessage message = new PullMessage(myInfo, myInfo.getListenerPort(), nextNodeInfo.getListenerPort(), String.valueOf(hash), version);
            MessageUtil.sendMessage(message);
        }
    }
}
