package main.server;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import main.common.message.BytePacker;

public class History {
    private ArrayList<ClientRecord> clientRecordList;
    public static final int HISTORY_RECORD_SIZE = 10;

    /**
     * Class constructor of History
     */
    public History() {
        clientRecordList = new ArrayList<>();
    }

    /**
     * searches for existing client in clientList, else create a new client and insert into list
     *
     * @param address ipaddress
     * @param port    portnumber
     * @return client object
     */
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

    /**
     * Represents each client that has sent the server a request before.
     */
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

        public InetAddress getClientAddress() {
            return this.address;
        }

        public int getClientPortNumber() {
            return this.portNumber;
        }

        /**
         * Searches if messageID exist in client hashmap
         *
         * @param messageId - messageId of incoming request
         * @return reply to request if messageID does exists in the hashmap, null otherwise
         */


        public BytePacker searchForDuplicateRequest(int messageId) {
            BytePacker reply = this.messageIdToReplyMap.get(messageId);
            if (reply != null) {
                System.out.println("Received duplicate request! Sending stored reply...");
            }
            return reply;
        }

        /**
         * Adds a messageId and reply to hashmap after request is serviced.
         *
         * @param messageId          - messageId of incoming request
         * @param reply - reply sent to client for this request
         */
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