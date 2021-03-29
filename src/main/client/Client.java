package main.client;

//import javafx.util.Pair;
import main.common.facility.Facilities;
import main.common.facility.Time;
import main.common.message.BytePacker;
import main.common.message.ByteUnpacker;
//import main.common.message.OneByteInt;
import main.common.message.OneByteInt;
import main.common.network.Method;
//import main.common.network.MethodNotFoundException;
//import main.common.network.RawMessage;

//import java.lang.invoke.MethodHandle;
//import java.net.DatagramPacket;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
//import java.net.Socket;
//import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.UUID;
import java.util.Arrays;

import main.common.network.*;

import static main.client.Util.*;

public class Client {

    private SocketAddress serverAddr;
    private final Transport transport;
    private int message_id;

    protected static final String STATUS = "status";
    protected static final String SERVICE_ID = "serviceId";
    protected static final String MESSAGE_ID = "messageId";
    protected static final String REPLY = "reply";


    public Client(Transport transport, SocketAddress serverAddr){
        this.transport = transport;
        this.serverAddr = serverAddr;
//        this.socket = new DatagramSocket(); // Already initialised in Transport
        this.message_id = 0;
    }

    public int getMessageId(){
        return message_id++;
    }

    public void setMessageId(int msg_id){
        this.message_id = msg_id;
    }


    public void sendMessageToServer() {
        try {
            String testmsg = readLine("Your message: ");

            int message_id = this.getMessageId();

            BytePacker packer = new BytePacker.Builder()
                    .setProperty(SERVICE_ID, new OneByteInt(Method.PING))
                    .setProperty(MESSAGE_ID, message_id)
                    .setProperty("pingMessage", testmsg)
                    .build();

            this.transport.send(this.serverAddr, packer);
            System.out.println("message sent to server");
            /** Add timeout here **/

            try {
//                DatagramPacket p = transport.receive();
//                byte[] data = p.getData();
//                ByteUnpacker unpacker = new ByteUnpacker.Builder()
//                        .setType(SERVICE_ID, ByteUnpacker.TYPE.ONE_BYTE_INT)
//                        .setType(MESSAGE_ID, ByteUnpacker.TYPE.INTEGER)
//                        .setType("pingMessage", ByteUnpacker.TYPE.STRING)
//                        .build();
//
//                ByteUnpacker.UnpackedMsg unpackedMsg = unpacker.parseByteArray(data);
                ByteUnpacker.UnpackedMsg unpackedMsg = transport.receivalProcedure(serverAddr, packer, message_id);

                if(transport.checkStatus(unpackedMsg)) {
                    String reply = unpackedMsg.getString(REPLY);
                    System.out.println("Response from server: " + reply);
                } else {
                    System.out.println("Failed to ping");
                }

            } catch (IOException e) {
                System.out.print(e);
            }

        } catch(RuntimeException e) {
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
        String facility = "";
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
            /** Add timeout here **/

            int message_id = this.getMessageId();

            BytePacker packer = new BytePacker.Builder()
                    .setProperty(SERVICE_ID, new OneByteInt(Method.QUERY))
                    .setProperty(MESSAGE_ID, message_id)
                    .setProperty("facility", facility)
                    .build();

            transport.send(serverAddr, packer);

            /** Add timeout here **/

            try {
                ByteUnpacker.UnpackedMsg unpackedMsg = transport.receivalProcedure(serverAddr, packer, message_id);

                if(transport.checkStatus(unpackedMsg)) {
                    String reply = unpackedMsg.getString(REPLY);
                    System.out.println("Response from server: " + reply);
                } else {
                    System.out.println("Failed to query facility");
                }

            } catch (IOException e) {
                System.out.print(e);
            }
        } catch(RuntimeException e) {
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
            System.out.println("Invalid end time entered: " + end  + "\n");
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
//        HashMap<String, Object> payload = new HashMap<>();
//        payload.put(Method.Add.START.toString(), start);
//        payload.put(Method.Add.END.toString(), end);

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
                    .setProperty("startDay", startDay)
                    .setProperty("startHour", startHr)
                    .setProperty("startMin", startMin)
                    .setProperty("endDay", endDay)
                    .setProperty("endHour", endHr)
                    .setProperty("endMin", endMin)
                    .setProperty("facility", facility)
                    .build();

            transport.send(serverAddr, packer);


            /** Add timeout here **/

            try {
                ByteUnpacker.UnpackedMsg unpackedMsg = transport.receivalProcedure(serverAddr, packer, message_id);

                if(transport.checkStatus(unpackedMsg)) {
                    String reply = unpackedMsg.getString(REPLY);
                    System.out.println("Response from server: " + reply);
                } else {
                    System.out.println("Failed to add booking");
                }

            } catch (IOException e) {
                System.out.print(e);
            }

        } catch(RuntimeException e) {
            System.out.println("Client.addBooking - Runtime Exception! " + e.getMessage());
        }
    }

    public void changeBooking() {
        String uuid = readLine("Please enter the confirmation ID of the booking: ");
        int offset = safeReadInt("Please enter the offset desired for this booking in minutes (negative for advancement, positive for postponement)\n(1 => 1min, 60 => 1hour, 3600 => 1 day): ");
        HashMap<String, Object> payload = new HashMap<>();
        payload.put(Method.Change.UUID.toString(), uuid);
        payload.put(Method.Change.OFFSET.toString(), offset);

        try {
//            this.transport.send(this.serverAddr, main.common.Util.putInHashMapPacket(Method.Methods.CHANGE, payload));
            int message_id = this.getMessageId();

            BytePacker packer = new BytePacker.Builder()
                    .setProperty(SERVICE_ID, new OneByteInt(Method.CHANGE))
                    .setProperty(MESSAGE_ID, message_id)
                    .setProperty("uuid", uuid)
                    .setProperty("offset", offset)
                    .build();

            this.transport.send(serverAddr, packer);

            /** Add timeout here **/

            try {
                ByteUnpacker.UnpackedMsg unpackedMsg = transport.receivalProcedure(serverAddr, packer, message_id);

                if(transport.checkStatus(unpackedMsg)) {
                    String reply = unpackedMsg.getString(REPLY);
                    System.out.println("Response from server: " + reply);
                } else {
                    System.out.println("Failed to change booking");
                }

            } catch (IOException e) {
                System.out.print(e);
            }

        } catch(RuntimeException e) {
            System.out.println("Client.changeBooking - Runtime Exception! " + e.getMessage());
        }
    }

    /**
     * For simplicity, you may assume that the user that has issued a register request for monitoring is blocked from inputting any new request until the monitor interval expires,
     * i.e., the client simply waits for the updates from the server during the monitoring interval. As a result, you do not have to use multiple threads at a client.
     */
    public void monitorAvailability(String clientAddr, int clientPort) {
//        boolean terminate = false;
//        HashMap<String, Object> payload = new HashMap<>();
//        int monitorInterval = safeReadInt("Please enter your monitor interval in minutes\n(1 => 1min, 60 => 1hour, 3600 => 1 day): ");
//        payload.put(Method.Monitor.INTERVAL.toString(), monitorInterval);
//        payload.put(Method.Monitor.CLIENTADDR.toString(), clientAddr);
//        payload.put(Method.Monitor.CLIENTPORT.toString(), clientPort);
//
//        String MANUAL = "----------------------------------------------------------------\n" +
//                "Please choose a facility by typing [1-4]:\n" +
//                "1: LT1\n" +
//                "2: LT2\n" +
//                "3: MR1\n" +
//                "4: MR2\n" +
//                "0: Exit and perform another query\n";
//        System.out.print(MANUAL);
//
//        try {
//            while (!terminate) {
//                int userChoice = safeReadInt("Your choice of facility: ");
//                switch (userChoice) {
//                    case 1:
//                        payload.put(Method.Monitor.FACILITY.toString(), Facilities.Types.LT1);
//                        terminate = true;
//                        break;
//                    case 2:
//                        payload.put(Method.Monitor.FACILITY.toString(), Facilities.Types.LT2);
//                        terminate = true;
//                        break;
//                    case 3:
//                        payload.put(Method.Monitor.FACILITY.toString(), Facilities.Types.MR1);
//                        terminate = true;
//                        break;
//                    case 4:
//                        payload.put(Method.Monitor.FACILITY.toString(), Facilities.Types.MR2);
//                        terminate = true;
//                        break;
//                    case 0:
//                        return;
//                    default:
//                        System.out.println("Invalid choice!");
//                        break;
//                }
//            }
//            this.transport.send(this.serverAddr, main.common.Util.putInHashMapPacket(Method.Methods.MONITOR, payload));
//
//            /** Add 10s for latency issues? **/
//            LocalDateTime end = LocalDateTime.now().plusMinutes(Long.valueOf(monitorInterval)).plusSeconds(10);
//
//            /** Blocks user until interval expires, while continuously listen to packets from server **/
//            while (LocalDateTime.now().isBefore(end)) {
//                /** Add timeout here **/
//                RawMessage res = this.transport.receive();
//                String rcv_method = (String) res.packet.get(Method.METHOD);
//                if (rcv_method.equals(Method.Methods.MONITOR.toString())) {
//                    System.out.println("message received from main.server: ");
//                    System.out.println(res.packet);
//                } else {
//                    throw new MethodNotFoundException("Client.monitorBooking - Unexpected Method! Expecting method 'MONITOR'");
//                }
//            }
//            System.out.println(monitorInterval + " mins elapsed. Interval expired");
//        } catch(RuntimeException e) {
//            System.out.println("Client.monitorBooking - Runtime Exception! " + e.getMessage());
//        }

    }

    // an idempotent operation
    public void cancelBooking() {
        String uuid = readLine("Please enter the confirmation ID of the booking: ");

        try {

            int message_id = this.getMessageId();

            BytePacker packer = new BytePacker.Builder()
                    .setProperty(SERVICE_ID, new OneByteInt(Method.CANCEL))
                    .setProperty(MESSAGE_ID, message_id)
                    .setProperty("uuid", uuid)
                    .build();

            this.transport.send(serverAddr, packer);
            /** Add timeout here **/

            try {
                ByteUnpacker.UnpackedMsg unpackedMsg = transport.receivalProcedure(serverAddr, packer, message_id);

                if(transport.checkStatus(unpackedMsg)) {
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
        String uuid = readLine("Please enter the confirmation ID of the booking: ");
        double extend = safeReadDouble("Please enter the extension desired for this booking in hours (30-minute block)\n(i.e. 30-minute => 0.5, 2-hours => 2): ");
        while (extend <= 0 || extend % 0.5 != 0.0) {
            extend = safeReadDouble("Your input is not in multiples of 0.5 or is <= 0!\nPlease enter the extension desired for this booking (in 30-minute block)\n(i.e. 30-minute => 0.5, 2-hours => 2): ");
        }


        try {

            int message_id = this.getMessageId();

            BytePacker packer = new BytePacker.Builder()
                    .setProperty(SERVICE_ID, new OneByteInt(Method.EXTEND))
                    .setProperty(MESSAGE_ID, message_id)
                    .setProperty("uuid", uuid)
                    .setProperty("extendTime", extend)
                    .build();

            this.transport.send(serverAddr, packer);

            /** Add timeout here **/

            try {
                ByteUnpacker.UnpackedMsg unpackedMsg = transport.receivalProcedure(serverAddr, packer, message_id);

                if(transport.checkStatus(unpackedMsg)) {
                    String reply = unpackedMsg.getString(REPLY);
                    System.out.println("Response from server: " + reply);
                } else {
                    System.out.println("Failed to extend booking");
                }

            } catch (IOException e) {
                System.out.print(e);
            }

        } catch(RuntimeException e) {
            System.out.println("Client.extendBooking - Runtime Exception! " + e.getMessage());
        }
    }


    public static void testRMI(String serverHost, int serverPort) {
        /** TODO:
         *  Use socket programming
         *  maybe there's no need for rmi architecture
         */
//         try {
//             String rmiName = "rmi://" + serverHost + ":" + serverPort + "/City";
//             RMIRegistry registry = RMIRegistry.getInstance();
////             IRemote cityServer = (IRemote) Naming.lookup(rmiName);
//             IRemote cityServer = (IRemote) registry.lookup(rmiName);
//
//         int pop = cityServer.getPopulation("Toronto");
//            System.out.println("pop: " + pop);
//         } catch (Exception e) {
//            System.out.println("Exception! " + e.getMessage());
//         }
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
}
