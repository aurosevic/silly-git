package cli.command;

import app.AppConfig;
import app.ChordState;

public class AddCommand implements CLICommand {

    @Override
    public String commandName() {
        return "add";
    }

    @Override
    public void execute(String args) {
        // todo: args -> hesiram kako god
        int hash = ChordState.chordHashDir(args);
        System.out.println(hash);
//        if (AppConfig.chordState.isKeyMine(hash)) {
//            AppConfig.chordState.getValueMap().put() // ako je true
//        } else {
//            ChordState.chordHash(args)
//            AppConfig.chordState.getNextNodeForKey()
//        }
    }
}
