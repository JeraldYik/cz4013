package main.server;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import main.common.message.BytePacker;

public class History {
    private ArrayList<ClientRecord> clientRecordList;
    public static final int HISTORY_RECORD_SIZE = 10;

    public History() {
        clientRecordList = new ArrayList<>();
    }

    public ClientRecord findClient(InetAddress address, int port) {
        for (ClientRecord c : clientRecordList) {
            if (c.address.equals(address) && c.portNumber == port) {
                return c;
            }
        }
        ClientRecord newClientRecord = new ClientRecord(address, port);
        clientRecordList.add(newClientRecord);
        return newClientRecord;
    }

    public class ClientRecord {
        private InetAddress address;
        private int portNumber;
        private HashMap<Integer, BytePacker> messageIdToReplyMap;
        private int[] historyRecord;
        private int count;

        public ClientRecord(InetAddress address, int portNumber) {
            this.address = address;
            this.portNumber = portNumber;
            this.messageIdToReplyMap = new HashMap<>();
            historyRecord = new int[HISTORY_RECORD_SIZE]; //keep 10 messages in history
            count = 0;
            Arrays.fill(historyRecord, -1);
        }


        public BytePacker findDuplicateMessage(int messageId) {
            BytePacker reply = this.messageIdToReplyMap.get(messageId);
            if (reply != null) {
                System.out.println("Received duplicate request! Sending stored reply...");
            }
            return reply;
        }


        public void addReplyEntry(int messageId, BytePacker reply) {
            if (historyRecord[count] != -1) {
                messageIdToReplyMap.remove(historyRecord[count]);
            }
            this.messageIdToReplyMap.put(messageId, reply);
            historyRecord[count] = messageId;
            count = (count + 1) % HISTORY_RECORD_SIZE;
        }
    }
}