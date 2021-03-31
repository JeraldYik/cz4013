package main.server;

import main.common.message.BytePacker;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


/**
 * The type History.
 */
public class History {

    /**
     * The constant HISTORY_RECORD_SIZE.
     */
    public static final int HISTORY_RECORD_SIZE = 10;
    private final ArrayList<ClientRecord> clientRecordList;

    /**
     * Instantiates a new History.
     */
    public History() {
        clientRecordList = new ArrayList<>();
    }

    /**
     * Find client record.
     *
     * @param address the address
     * @param port    the port
     * @return the client record
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
     * The type Client record.
     */
    public class ClientRecord {
        private final InetAddress address;
        private final int portNumber;
        private final HashMap<Integer, BytePacker> messageIdToReplyMap;
        private final int[] historyRecord;
        private int count;

        /**
         * Instantiates a new Client record.
         *
         * @param address    the address
         * @param portNumber the port number
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
         * Find duplicate message byte packer.
         *
         * @param messageId the message id
         * @return the byte packer
         */
        public BytePacker findDuplicateMessage(int messageId) {
            BytePacker reply = this.messageIdToReplyMap.get(messageId);
            if (reply != null) {
                System.out.println("Received duplicate request! Sending stored reply...");
            }
            return reply;
        }

        /**
         * Add reply entry.
         *
         * @param messageId the message id
         * @param reply     the reply
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