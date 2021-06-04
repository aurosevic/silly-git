package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import app.silly_git.SillyFile;
import servent.message.*;
import servent.message.util.MessageUtil;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

public class NewNodeHandler implements MessageHandler {

    private Message clientMessage;
    private List<Integer> processedHashValues = Collections.synchronizedList(new ArrayList<>());

    public NewNodeHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.NEW_NODE) {
            int newNodePort = clientMessage.getSenderPort();
            ServentInfo newNodeInfo = new ServentInfo("localhost", newNodePort);

            //check if the new node collides with another existing node.
            if (AppConfig.chordState.isCollision(newNodeInfo.getChordId())) {
                Message sry = new SorryMessage(AppConfig.myServentInfo.getListenerPort(), clientMessage.getSenderPort());
                MessageUtil.sendMessage(sry);
                return;
            }

            //check if he is my predecessor
            boolean isMyPred = AppConfig.chordState.isKeyMine(newNodeInfo.getChordId());
            if (isMyPred) { //if yes, prepare and send welcome message
                ServentInfo hisPred = AppConfig.chordState.getPredecessor();
                if (hisPred == null) {
                    hisPred = AppConfig.myServentInfo;
                }

                AppConfig.chordState.setPredecessor(newNodeInfo);

                Map<Integer, SillyFile> myValues = AppConfig.chordState.getValueMap();
                Map<Integer, SillyFile> hisValues = new HashMap<>();

                int myId = AppConfig.myServentInfo.getChordId();
                int hisPredId = hisPred.getChordId();
                int newNodeId = newNodeInfo.getChordId();

                for (Entry<Integer, SillyFile> valueEntry : myValues.entrySet()) {
                    if (hisPredId == myId) { //i am first and he is second
                        if (myId < newNodeId) {
                            if (valueEntry.getKey() <= newNodeId && valueEntry.getKey() > myId) {
                                hisValues.put(valueEntry.getKey(), valueEntry.getValue());
                            }
                        } else {
                            if (valueEntry.getKey() <= newNodeId || valueEntry.getKey() > myId) {
                                hisValues.put(valueEntry.getKey(), valueEntry.getValue());
                            }
                        }
                    }
                    if (hisPredId < myId) { //my old predecesor was before me
                        if (valueEntry.getKey() <= newNodeId) {
                            hisValues.put(valueEntry.getKey(), valueEntry.getValue());
                        }
                    } else { //my old predecesor was after me
                        if (hisPredId > newNodeId) { //new node overflow
                            if (valueEntry.getKey() <= newNodeId || valueEntry.getKey() > hisPredId) {
                                hisValues.put(valueEntry.getKey(), valueEntry.getValue());
                            }
                        } else { //no new node overflow
                            if (valueEntry.getKey() <= newNodeId && valueEntry.getKey() > hisPredId) {
                                hisValues.put(valueEntry.getKey(), valueEntry.getValue());
                            }
                        }
                    }

                }
                for (Integer key : hisValues.keySet()) { // remove his values from my map
                    myValues.remove(key);
                    SillyFile sillyFile = hisValues.get(key);

                    if (sillyFile.isDirectory()) {
                        for (Map.Entry<Integer, SillyFile> entry : sillyFile.getSillyFiles().entrySet()) {
                            SillyFile subFile = entry.getValue();
                            int hash = entry.getKey();
                            if (!processedHashValues.contains(hash)) {
                                AddMessage addMessage = new AddMessage(AppConfig.myServentInfo.getListenerPort(), newNodePort, String.valueOf(hash), subFile, false);
                                MessageUtil.sendMessage(addMessage);
                                if (hisValues.containsKey(hash)) {
                                    // Note: Let files exist separately if their hash belongs to the new node
                                    File file = new File(AppConfig.myServentInfo.getStorage() + subFile.getFilePath());
                                    file.delete();
                                }
                                processedHashValues.add(hash);
                            }
                        }
                    } else {
                        int hash = key;
                        if (!processedHashValues.contains(hash)) {
                            AddMessage addMessage = new AddMessage(AppConfig.myServentInfo.getListenerPort(), newNodePort, String.valueOf(hash), sillyFile, false);
                            MessageUtil.sendMessage(addMessage);
                            File file = new File(AppConfig.myServentInfo.getStorage() + sillyFile.getFilePath());
                            file.delete();
                            processedHashValues.add(hash);
                        }
                    }
                }
                AppConfig.chordState.setValueMap(myValues);

                WelcomeMessage wm = new WelcomeMessage(AppConfig.myServentInfo.getListenerPort(), newNodePort, hisValues);
                MessageUtil.sendMessage(wm);
            } else { // if he is not my predecessor, let someone else take care of it
                ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(newNodeInfo.getChordId());
                NewNodeMessage nnm = new NewNodeMessage(newNodePort, nextNode.getListenerPort());
                MessageUtil.sendMessage(nnm);
            }

        } else {
            AppConfig.timestampedErrorPrint("NEW_NODE handler got something that is not new node message.");
        }
    }
}
