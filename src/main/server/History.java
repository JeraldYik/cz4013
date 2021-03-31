package main.server;

import main.common.message.BytePacker;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


/**
 * This class implements server-side message history for At-Most-Once invocation semantics.
 * Whenever a client request is received, a client record is created and stored.
 * From there, any reply generated is stored in the client record, and retrieved if a duplicate request is received.
 */
public class History {

    public static final int HISTORY_RECORD_SIZE = 10;
    private final ArrayList<ClientRecord> clientRecordList;

    /**
     * @constructor for History class
     */
    public History() {
        clientRecordList = new ArrayList<>();
    }

    /**
     * Finds existing client record.
     * If no existing client record is found, create new record and add to clientRecord.
     *
     * @param address Client IP address
     * @param port    Client port number
     * @return clientRecord
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
     * Created for each client that sends a request to the server
     */
    public class ClientRecord {

        private final InetAddress address;
        private final int portNumber;
        private final HashMap<Integer, BytePacker> messageIdToReplyMap;
        private final int[] historyRecord;
        private int count;

        /**
         * @constructor for Client class
         * @param address    client IP address
         * @param portNumber client port number
         */
        public ClientRecord(InetAddress address, int portNumber) {
            this.address = address;
            this.portNumber = portNumber;
            this.messageIdToReplyMap = new HashMap<>();
            historyRecord = new int[HISTORY_RECORD_SIZE]; //keep 10 messages in history
            count = 0;
            Arrays.fill(historyRecord, -1);
        }

        /**
         * Find duplicate messageIds from stored server replies in messageIdToReplyMap
         *
         * @param messageId client request messageId
         * @return stored reply if found, else return null
         */
        public BytePacker findDuplicateMessage(int messageId) {
            BytePacker reply = this.messageIdToReplyMap.get(messageId);
            if (reply != null) {
                System.out.println("Received duplicate request! Sending stored reply...");
            }
            return reply;
        }

        /**
         * Adds a new reply entry to messageIdToReplyMap once client operation is executed
         *
         * @param messageId messageId of incoming request
         * @param reply     reply generated in Handler to be stored
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