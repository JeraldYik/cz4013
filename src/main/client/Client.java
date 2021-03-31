package main.client;

import main.common.facility.Facilities;
import main.common.facility.Time;
import main.common.message.BytePacker;
import main.common.message.ByteUnpacker;
import main.common.message.OneByteInt;
import main.common.network.Method;
import main.common.network.MonitoringExpireException;
import main.common.network.Transport;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

import static main.client.Util.*;

public class Client {

    protected static final String STATUS = "STATUS";
    protected static final String SERVICE_ID = "SERVICEID";
    protected static final String MESSAGE_ID = "MESSAGEID";
    protected static final String REPLY = "REPLY";
    private final Transport transport;
    private final int timeout = 2000;
    private final InetSocketAddress serverAddr;
    private int message_id;
    private final double failureProbability;
    private final Random random;


    public Client(Transport transport, InetSocketAddress serverAddr, double failureProbability) {
        this.transport = transport;
        this.serverAddr = serverAddr;
        this.failureProbability = failureProbability;
        this.message_id = 0;
        this.random = new Random();
    }

    public int getMessageId() {
        return message_id++;
    }

    public void sendMessageToServer() {
        try {
            String testmsg = readLine("Your message: ");

            int message_id = this.getMessageId();

            BytePacker packer = new BytePacker.Builder()
                    .setProperty(SERVICE_ID, new OneByteInt(Method.PING))
                    .setProperty(MESSAGE_ID, message_id)
                    .setProperty(Method.Ping.PING.toString(), testmsg)
                    .build();

            try {
                ByteUnpacker.UnpackedMsg unpackedMsg;
                this.transport.send(this.serverAddr, packer);
                System.out.println("message sent to server");
                while (true) {
                    if (this.random.nextDouble() >= failureProbability) {
                        unpackedMsg = transport.receivalProcedure(message_id);
                        break;
                    } else {
                        System.out.println("Simulating packet loss");
                        Thread.sleep(timeout);
                        this.transport.send(this.serverAddr, packer);
                    }
                }
                if (transport.checkStatus(unpackedMsg)) {
                    String reply = unpackedMsg.getString(REPLY);
                    System.out.println("Response from server: " + reply);
                } else {
                    System.out.println("Failed to ping");
                }
                Thread.sleep(2000);
            } catch (IOException e) {
                System.out.print(e);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (RuntimeException e) {
            System.out.println("Client.sendMessageToServer - Runtime Exception! " + e.getMessage());
        }
    }

    public void queryAvailability() {
        String MANUAL = "----------------------------------------------------------------\n" +
                "Please choose a facility by typing [1-4]:\n" +
                "1: LT1\n" +
                "2: LT2\n" +
                "3: MR1\n" +
                "4: MR2\n" +
                "0: Exit and perform another query\n";
        System.out.print(MANUAL);
        boolean terminate = false;

        Facilities.Types facility = null;

        try {
            while (!terminate) {
                int userChoice = safeReadInt("Your choice of facility: ");
                switch (userChoice) {
                    case 1:
                        facility = Facilities.Types.LT1;
                        terminate = true;
                        break;
                    case 2:
                        facility = Facilities.Types.LT2;
                        terminate = true;
                        break;
                    case 3:
                        facility = Facilities.Types.MR1;
                        terminate = true;
                        break;
                    case 4:
                        facility = Facilities.Types.MR2;
                        terminate = true;
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Invalid choice!");
                        break;
                }
            }
            /** Add timeout here **/

            int message_id = this.getMessageId();

            BytePacker packer = new BytePacker.Builder()
                    .setProperty(SERVICE_ID, new OneByteInt(Method.QUERY))
                    .setProperty(MESSAGE_ID, message_id)
                    .setProperty(Method.Query.FACILITY.toString(), facility.toString())
                    .build();

            ByteUnpacker.UnpackedMsg unpackedMsg;

            this.transport.send(this.serverAddr, packer);
            System.out.println("message sent to server");
            unpackedMsg = transport.receivalProcedure(message_id);

            if (transport.checkStatus(unpackedMsg)) {
                System.out.println("Response from server:");
                String response = unpackedMsg.getString(REPLY);
                this.printBookings(response, facility);
            } else {
                System.out.println("Failed to query facility");
            }

        } catch (IOException e) {
            System.out.print(e.getMessage());
        } catch (RuntimeException e) {
        System.out.println("Client.queryAvailability - Runtime Exception! " + e.getMessage());
        }
    }

    public void addBooking() {
        String facility = "";
        int startDay;
        int startHr;
        int startMin;
        int endDay;
        int endHr;
        int endMin;

        System.out.println("------------------------Adding Booking------------------------");
        Time start = getTime("Please enter the start time.");

        Time end = getTime("Please enter the end time. (Must be later than the end time)");
        while (!Time.compare(start, end)) {
            System.out.println("End time must be larger than start time!");
            System.out.println("Start time: " + start);
            System.out.println("Invalid end time entered: " + end + "\n");
            end = getTime("Please enter a larger end time: ");
        }
        startDay = start.day;
        startHr = start.hour;
        startMin = start.minute;

        endDay = end.day;
        endHr = end.hour;
        endMin = end.minute;

        System.out.println();
        System.out.println("Start time: " + start.toString());
        System.out.println("End time: " + end.toString());

        String MANUAL = "Please choose a facility by typing [1-4]:\n" +
                "1: LT1\n" +
                "2: LT2\n" +
                "3: MR1\n" +
                "4: MR2\n" +
                "0: Exit and perform another query\n";
        System.out.print(MANUAL);
        boolean terminate = false;
        try {
            while (!terminate) {
                int userChoice = safeReadInt("Your choice of facility: ");
                switch (userChoice) {
                    case 1:
                        facility = Facilities.Types.LT1.toString();
                        terminate = true;
                        break;
                    case 2:
                        facility = Facilities.Types.LT2.toString();
                        terminate = true;
                        break;
                    case 3:
                        facility = Facilities.Types.MR1.toString();
                        terminate = true;
                        break;
                    case 4:
                        facility = Facilities.Types.MR2.toString();
                        terminate = true;
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Invalid choice!");
                        break;
                }
            }

            int message_id = this.getMessageId();

            BytePacker packer = new BytePacker.Builder()
                    .setProperty(SERVICE_ID, new OneByteInt(Method.ADD))
                    .setProperty(MESSAGE_ID, message_id)
                    .setProperty(Method.Add.STARTDAY.toString(), startDay)
                    .setProperty(Method.Add.STARTHOUR.toString(), startHr)
                    .setProperty(Method.Add.STARTMIN.toString(), startMin)
                    .setProperty(Method.Add.ENDDAY.toString(), endDay)
                    .setProperty(Method.Add.ENDHOUR.toString(), endHr)
                    .setProperty(Method.Add.ENDMIN.toString(), endMin)
                    .setProperty(Method.Add.FACILITY.toString(), facility)
                    .build();

            this.transport.send(serverAddr, packer);

            try {
                ByteUnpacker.UnpackedMsg unpackedMsg;
                while (true) {
                    if (this.random.nextDouble() >= failureProbability) {
                        unpackedMsg = transport.receivalProcedure(message_id);
                        break;
                    } else {
                        DatagramPacket temp = transport.receive();
                        System.out.println("Simulating packet loss");
                        Thread.sleep(2000);
                        this.transport.send(this.serverAddr, packer);
                    }
                }

                if (transport.checkStatus(unpackedMsg)) {
                    String reply = unpackedMsg.getString(REPLY);
                    System.out.println(reply);
                } else {
                    System.out.println("Failed to add booking");
                }

            } catch (IOException e) {
                System.out.print(e);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (RuntimeException e) {
            System.out.println("Client.addBooking - Runtime Exception! " + e.getMessage());
        }
    }

    public void changeBooking() {

        UUID uuid;

        while (true) {
            try {
                uuid = UUID.fromString(readLine("Please enter the confirmation ID of the booking: "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Please enter a valid confirmation ID!");
            }
        }
        int offset = safeReadInt("Please enter the offset desired for this booking in minutes (negative for advancement, positive for postponement)\n(1 => 1min, 60 => 1hour, 3600 => 1 day): ");

        try {
            int message_id = this.getMessageId();

            BytePacker packer = new BytePacker.Builder()
                    .setProperty(SERVICE_ID, new OneByteInt(Method.CHANGE))
                    .setProperty(MESSAGE_ID, message_id)
                    .setProperty(Method.Change.UUID.toString(), uuid.toString())
                    .setProperty(Method.Change.OFFSET.toString(), offset)
                    .build();

            try {
                ByteUnpacker.UnpackedMsg unpackedMsg;
                this.transport.send(serverAddr, packer);
                while (true) {
                    if (this.random.nextDouble() >= failureProbability) {
                        unpackedMsg = transport.receivalProcedure(message_id);
                        break;
                    } else {
                        DatagramPacket temp = transport.receive();
                        System.out.println("Simulating packet loss");
                        Thread.sleep(timeout);
                        this.transport.send(this.serverAddr, packer);
                    }
                }

                if (transport.checkStatus(unpackedMsg)) {
                    String reply = unpackedMsg.getString(REPLY);
                    System.out.println("Response from server: " + reply);
                } else {
                    System.out.println("Failed to change booking");
                }

            } catch (IOException e) {
                System.out.print(e);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (RuntimeException e) {
            System.out.println("Client.changeBooking - Runtime Exception! " + e.getMessage());
        }
    }

    /**
     * For simplicity, you may assume that the user that has issued a register request for monitoring is blocked from inputting any new request until the monitor interval expires,
     * i.e., the client simply waits for the updates from the server during the monitoring interval. As a result, you do not have to use multiple threads at a client.
     */

    public void monitorAvailability() {
        boolean terminate = false;
        Facilities.Types facility = null;
        int monitorInterval = safeReadInt("Please enter your monitor interval in minutes\n(1 => 1min, 60 => 1hour, 3600 => 1 day): ");

        String MANUAL = "----------------------------------------------------------------\n" +
                "Please choose a facility by typing [1-4]:\n" +
                "1: LT1\n" +
                "2: LT2\n" +
                "3: MR1\n" +
                "4: MR2\n" +
                "0: Exit and perform another query\n";
        System.out.print(MANUAL);

        try {
            while (!terminate) {
                int userChoice = safeReadInt("Your choice of facility: ");
                switch (userChoice) {
                    case 1:
                        facility = Facilities.Types.LT1;
                        terminate = true;
                        break;
                    case 2:
                        facility = Facilities.Types.LT2;
                        terminate = true;
                        break;
                    case 3:
                        facility = Facilities.Types.MR1;
                        terminate = true;
                        break;
                    case 4:
                        facility = Facilities.Types.MR2;
                        terminate = true;
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Invalid choice!");
                        break;
                }
            }

            int message_id = this.getMessageId();

            BytePacker packer = new BytePacker.Builder()
                    .setProperty(SERVICE_ID, new OneByteInt(Method.MONITOR))
                    .setProperty(MESSAGE_ID, message_id)
                    .setProperty(Method.Monitor.INTERVAL.toString(), monitorInterval)
                    .setProperty(Method.Monitor.FACILITY.toString(), facility.toString())
                    .build();

            this.transport.send(serverAddr, packer);

            /** Add 2s for latency issues? **/
            LocalDateTime end = LocalDateTime.now().plusMinutes(Long.valueOf(monitorInterval)).plusSeconds(2);

            boolean receiveServerPingResponse = true;
            /** Blocks user until interval expires, while continuously listen to packets from server
             * If no packets received, i.e. no updates within interval, receive end packet from server to end monitoring
             * **/
            while (true) {
                /** Add timeout here **/
                System.out.println("\nWaiting for response from server...");
                LocalDateTime now = LocalDateTime.now();
                int remainingTimeout = (int) ChronoUnit.MILLIS.between(now, end);

                try {
                    ByteUnpacker.UnpackedMsg unpackedMsg;
                    /** Do check for monitor ping response, or actual updates on monitoring **/
                    if (receiveServerPingResponse) {
                        /** only receive ping response once per monitoring **/
                        receiveServerPingResponse = false;
                        unpackedMsg = transport.setNonZeroTimeoutReceivalProcedure(remainingTimeout, message_id);
                        if (transport.checkStatus(unpackedMsg)) {
                            String reply = unpackedMsg.getString(REPLY);
                            System.out.println("Response from server: " + reply);
                        } else {
                            System.out.println("Failed to query facility");
                        }
                    } else {
                        unpackedMsg = transport.setNonZeroTimeoutReceivalProcedure(remainingTimeout);
                        if (transport.checkStatus(unpackedMsg)) {
                            String reply = unpackedMsg.getString(REPLY);
                            System.out.println("Response from server:");
                            this.printBookings(reply, facility);
                        } else {
                            System.out.println("Failed to query facility");
                        }
                    }
                } catch (IOException e) {
                    System.out.print(e);
                }
            }
        } catch (MonitoringExpireException e) {
            System.out.println(monitorInterval + " min(s) elapsed. Interval expired.");
        } catch (RuntimeException e) {
            System.out.println("Client.monitorBooking - " + e.getClass().toString() + ": " + e.getMessage());
        }

    }

    // an idempotent operation
    public void cancelBooking() {
        UUID uuid;

        while (true) {
            try {
                uuid = UUID.fromString(readLine("Please enter the confirmation ID of the booking: "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Please enter a valid booking UUID!");
            }
        }

        try {
            int message_id = this.getMessageId();

            BytePacker packer = new BytePacker.Builder()
                    .setProperty(SERVICE_ID, new OneByteInt(Method.CANCEL))
                    .setProperty(MESSAGE_ID, message_id)
                    .setProperty(Method.Cancel.UUID.toString(), uuid.toString())
                    .build();

            this.transport.send(serverAddr, packer);
            /** Add timeout here **/

            try {
                ByteUnpacker.UnpackedMsg unpackedMsg = transport.receivalProcedure(message_id);

                if (transport.checkStatus(unpackedMsg)) {
                    String reply = unpackedMsg.getString(REPLY);
                    System.out.println("Response from server: " + reply);
                } else {
                    System.out.println("Failed to cancel booking");
                }

            } catch (IOException e) {
                System.out.print(e);
            }

        } catch (RuntimeException e) {
            System.out.println(("Client.cancelBooking - Runtime Exception! " + e.getMessage()));
        }
    }

    // a non-idempotent operation
    public void extendBooking() {
        UUID uuid;
        while (true) {
            try {
                uuid = UUID.fromString(readLine("Please enter the confirmation ID of the booking: "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Please enter a valid booking UUID!");
            }
        }
        double extend = safeReadDouble("Please enter the extension desired for this booking in hours (30-minute block)\n(i.e. 30-minute => 0.5, 2-hours => 2): ");
        while (extend <= 0 || extend % 0.5 != 0.0) {
            extend = safeReadDouble("Your input is not in multiples of 0.5 or is <= 0!\nPlease enter the extension desired for this booking (in 30-minute block)\n(i.e. 30-minute => 0.5, 2-hours => 2): ");
        }

        try {
            int message_id = this.getMessageId();

            BytePacker packer = new BytePacker.Builder()
                    .setProperty(SERVICE_ID, new OneByteInt(Method.EXTEND))
                    .setProperty(MESSAGE_ID, message_id)
                    .setProperty(Method.Extend.UUID.toString(), uuid.toString())
                    .setProperty(Method.Extend.EXTEND.toString(), extend)
                    .build();

            try {
                ByteUnpacker.UnpackedMsg unpackedMsg;
                this.transport.send(serverAddr, packer);
                while (true) {
                    if (this.random.nextDouble() >= failureProbability) {
                        unpackedMsg = transport.receivalProcedure(message_id);
                        break;
                    } else {
                        DatagramPacket temp = transport.receive();
                        System.out.println("Simulating packet loss");
                        Thread.sleep(timeout);
                        this.transport.send(this.serverAddr, packer);
                    }
                }
                if (transport.checkStatus(unpackedMsg)) {
                    String reply = unpackedMsg.getString(REPLY);
                    System.out.println("Response from server: " + reply);
                } else {
                    System.out.println("Failed to extend booking");
                }

            } catch (IOException e) {
                System.out.print(e);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (RuntimeException e) {
            System.out.println("Client.extendBooking - Runtime Exception! " + e.getMessage());
        }
    }

    private void printBookings(String response, Facilities.Types t) {
        if (t == null) {
            System.out.println("Facility Type is null for unknown reason");
            return;
        }
        System.out.println("Message received from server: ");
        if (response.isEmpty()) {
            System.out.println("No active bookings for " + t.toString());
        } else {
            System.out.println("For " + t.toString() + ":");
            String[] bookings = response.split(Method.DELIMITER);
            for (int bookingCount = 1; bookingCount <= bookings.length; bookingCount++) {
                System.out.format("---------------Booking %d---------------\n", bookingCount);
                String[] startAndEnd = bookings[bookingCount - 1].split("-");

                System.out.println("Start Time: " + startAndEnd[0]);
                System.out.println("End Time: " + startAndEnd[1]);
            }
        }
    }

    private Time getTime(String prompt) {
        System.out.println(prompt);
        String DAYS_MANUAL = "1: Monday\n"
                + "2: Tuesday\n"
                + "3: Wednesday\n"
                + "4: Thursday\n"
                + "5: Friday\n"
                + "6: Saturday\n"
                + "7: Sunday\n";
        boolean terminated = false;
        int userDayChoice = -1;
        while (!terminated) {
            System.out.println(DAYS_MANUAL);
            userDayChoice = safeReadInt("Please enter the day according to the mapping: ");
            if (userDayChoice >= 1 && userDayChoice <= 7) {
                terminated = true;
            } else {
                System.out.println("Invalid day choice!");
            }
        }

        terminated = false;
        int userHourChoice = -1;
        while (!terminated) {
            userHourChoice = safeReadInt("Please enter the hour (0-23): ");
            if (userHourChoice >= 0 && userHourChoice <= 23) {
                terminated = true;
            } else {
                System.out.println("Invalid hour choice!");
            }
        }

        terminated = false;
        int userMinuteChoice = -1;
        while (!terminated) {
            userMinuteChoice = safeReadInt("Please enter the minute (0-59): ");
            if (userMinuteChoice >= 0 && userMinuteChoice <= 59) {
                terminated = true;
            } else {
                System.out.println("Invalid minute choice!");
            }
        }

        return new Time(userDayChoice, userHourChoice, userMinuteChoice);
    }

    public void sendDuplicatePingsToServer() {
        try {
            String testmsg = readLine("Your message: ");

            int message_id = this.getMessageId();

            BytePacker packer = new BytePacker.Builder()
                    .setProperty(SERVICE_ID, new OneByteInt(Method.PING))
                    .setProperty(MESSAGE_ID, message_id)
                    .setProperty(Method.Ping.PING.toString(), testmsg)
                    .build();

            try {
                ByteUnpacker.UnpackedMsg unpackedMsg;
                for (int i = 1; i <= 5; i++) {
                    this.transport.send(this.serverAddr, packer);
                    System.out.println("message sent to server");
                    while (true) {
                        if (this.random.nextDouble() >= failureProbability) {
                            unpackedMsg = transport.receivalProcedure(message_id);
                            break;
                        } else {
                            System.out.println("Simulating packet loss");
                            Thread.sleep(2000);
                            this.transport.send(this.serverAddr, packer);
                        }
                    }
                    System.out.println("Message count: " + i);
                    if (transport.checkStatus(unpackedMsg)) {
                        String reply = unpackedMsg.getString(REPLY);
                        System.out.println("Response from server: " + reply);
                    } else {
                        System.out.println("Failed to ping");
                    }
                    Thread.sleep(2000);
                }
            } catch (IOException e) {
                System.out.print(e);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (RuntimeException e) {
            System.out.println("Client.sendMessageToServer - Runtime Exception! " + e.getMessage());
        }
    }

    public void sendDuplicateExtendsToServer() {
        UUID uuid;
        while (true) {
            try {
                uuid = UUID.fromString(readLine("Please enter the confirmation ID of the booking: "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Please enter a valid booking UUID!");
            }
        }
        double extend = safeReadDouble("Please enter the extension desired for this booking in hours (30-minute block)\n(i.e. 30-minute => 0.5, 2-hours => 2): ");
        while (extend <= 0 || extend % 0.5 != 0.0) {
            extend = safeReadDouble("Your input is not in multiples of 0.5 or is <= 0!\nPlease enter the extension desired for this booking (in 30-minute block)\n(i.e. 30-minute => 0.5, 2-hours => 2): ");
        }

        try {
            int message_id = this.getMessageId();

            BytePacker packer = new BytePacker.Builder()
                    .setProperty(SERVICE_ID, new OneByteInt(Method.EXTEND))
                    .setProperty(MESSAGE_ID, message_id)
                    .setProperty(Method.Extend.UUID.toString(), uuid.toString())
                    .setProperty(Method.Extend.EXTEND.toString(), extend)
                    .build();

            for (int i = 1; i <= 5; i++) {
                this.transport.send(serverAddr, packer);

                /** Add timeout here **/

                try {
                    ByteUnpacker.UnpackedMsg unpackedMsg = transport.receivalProcedure(message_id);

                    if (transport.checkStatus(unpackedMsg)) {
                        String reply = unpackedMsg.getString(REPLY);
                        System.out.println("Response from server: " + reply);
                    } else {
                        System.out.println("Failed to extend booking");
                    }

                } catch (IOException e) {
                    System.out.print(e);

                }
            }
        } catch (RuntimeException e) {
            System.out.println("Client.extendBooking - Runtime Exception! " + e.getMessage());
        }
    }

    public void sendDuplicateCancelsToServer() {
        UUID uuid;

        while (true) {
            try {
                uuid = UUID.fromString(readLine("Please enter the confirmation ID of the booking: "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Please enter a valid booking UUID!");
            }
        }

        try {
            int message_id = this.getMessageId();

            BytePacker packer = new BytePacker.Builder()
                    .setProperty(SERVICE_ID, new OneByteInt(Method.CANCEL))
                    .setProperty(MESSAGE_ID, message_id)
                    .setProperty(Method.Cancel.UUID.toString(), uuid.toString())
                    .build();

            try {
                for (int i = 1; i <= 5; i++) {
                    this.transport.send(this.serverAddr, packer);
                    System.out.println("Message count: " + i);

                    ByteUnpacker.UnpackedMsg unpackedMsg = transport.receivalProcedure(message_id);

                    if (transport.checkStatus(unpackedMsg)) {
                        String reply = unpackedMsg.getString(REPLY);
                        System.out.println("Response from server: " + reply);
                    } else {
                        System.out.println("Failed to cancel booking");
                    }
                }
            } catch (RuntimeException e) {
                System.out.println(("Client.cancelBooking - Runtime Exception! " + e.getMessage()));
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (RuntimeException e) {
            System.out.println("Client.cancelBooking - Runtime Exception! " + e.getMessage());
        }
    }

    public void sendDuplicateChangesToServer() {

        UUID uuid;

        while (true) {
            try {
                uuid = UUID.fromString(readLine("Please enter the confirmation ID of the booking: "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Please enter a valid confirmation ID!");
            }
        }
        int offset = safeReadInt("Please enter the offset desired for this booking in minutes (negative for advancement, positive for postponement)\n(1 => 1min, 60 => 1hour, 3600 => 1 day): ");

        try {
            int message_id = this.getMessageId();

            BytePacker packer = new BytePacker.Builder()
                    .setProperty(SERVICE_ID, new OneByteInt(Method.CHANGE))
                    .setProperty(MESSAGE_ID, message_id)
                    .setProperty(Method.Change.UUID.toString(), uuid.toString())
                    .setProperty(Method.Change.OFFSET.toString(), offset)
                    .build();

            for(int i=1;i<=5;i++) {
                this.transport.send(serverAddr, packer);

                ByteUnpacker.UnpackedMsg unpackedMsg = transport.receivalProcedure(message_id);

                if (transport.checkStatus(unpackedMsg)) {
                    String reply = unpackedMsg.getString(REPLY);
                    System.out.println("Response from server: " + reply);
                } else {
                    System.out.println("Failed to change booking");
                }
            }

        } catch (IOException e) {
            System.out.print(e);
        }
    }
}
