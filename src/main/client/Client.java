package main.client;

import main.common.facility.Facilities;
import main.common.facility.Time;
import main.common.network.Method;
import main.common.network.MethodNotFoundException;
import main.common.network.RawMessage;
import main.common.network.Transport;

import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.util.HashMap;

import static main.client.Util.*;

public class Client {
    private final Transport transport;
    private final SocketAddress serverAddr;

    public Client(Transport transport, SocketAddress serverAddr) {
        this.transport = transport;
        this.serverAddr = serverAddr;
    }

    public void sendMessageToServer() {
        try {
            String testmsg = readLine("Your message: ");
            this.transport.send(this.serverAddr, main.common.Util.putInHashMapPacket(Method.Methods.PING, testmsg));
            System.out.println("Message sent to main.server.");

            /** Add timeout here **/

            RawMessage res = this.transport.receive();
            String method = (String) res.packet.get("method");
            if (method.equals(Method.Methods.PING.toString())) {
                System.out.println("Message received from main.server: ");
                System.out.println(res.packet);
            } else {
                throw new MethodNotFoundException("Client.sendMessageToServer - Unexpected Method! Expecting method 'PING'");
            }
        } catch(RuntimeException e) {
            System.out.println("Client.sendMessageToServer - " + e.getClass().toString() + ": " + e.getMessage());
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
        HashMap<String, Object> payload = new HashMap<>();
        try {
            while (!terminate) {
                int userChoice = safeReadInt("Your choice of facility: ");
                switch (userChoice) {
                    case 1:
                        payload.put(Method.Query.FACILITY.toString(),Facilities.Types.LT1);
                        this.transport.send(this.serverAddr, main.common.Util.putInHashMapPacket(Method.Methods.QUERY, payload));
                        terminate = true;
                        break;
                    case 2:
                        payload.put(Method.Query.FACILITY.toString(),Facilities.Types.LT2);
                        this.transport.send(this.serverAddr, main.common.Util.putInHashMapPacket(Method.Methods.QUERY, payload));
                        terminate = true;
                        break;
                    case 3:
                        payload.put(Method.Query.FACILITY.toString(),Facilities.Types.MR1);
                        this.transport.send(this.serverAddr, main.common.Util.putInHashMapPacket(Method.Methods.QUERY, payload));
                        terminate = true;
                        break;
                    case 4:
                        payload.put(Method.Query.FACILITY.toString(),Facilities.Types.MR2);
                        this.transport.send(this.serverAddr, main.common.Util.putInHashMapPacket(Method.Methods.QUERY, payload));
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
            RawMessage res = this.transport.receive();
            String rcv_method = (String) res.packet.get("method");
            if (rcv_method.equals(Method.Methods.QUERY.toString())) {
                System.out.println("Message received from main.server: ");
                System.out.println(res.packet);
            } else {
                throw new MethodNotFoundException("Client.queryAvailability - Unexpected Method! Expecting method 'QUERY'");
            }
        } catch(RuntimeException e) {
            System.out.println("Client.queryAvailability - " + e.getClass().toString() + ": " + e.getMessage());
        }
    }

    public void addBooking() {
        Time start = getTime("Please enter the start time.");
        Time end = getTime("Please enter the end time. (Must be later than the end time)");
        while (!Time.compare(start, end)) {
            System.out.println("End time must be larger than start time!");
            System.out.println("Start time: " + start);
            System.out.println("Invalid end time entered: " + end  + "\n");
            end = getTime("Please enter a larger end time: ");
        }
        System.out.println();
        System.out.println("Start time: " + start.toString());
        System.out.println("End time: " + end.toString());
        HashMap<String, Object> payload = new HashMap<>();
        payload.put(Method.Add.START.toString(), start);
        payload.put(Method.Add.END.toString(), end);

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
                        payload.put(Method.Add.FACILITY.toString(), Facilities.Types.LT1);
                        terminate = true;
                        break;
                    case 2:
                        payload.put(Method.Add.FACILITY.toString(), Facilities.Types.LT2);
                        terminate = true;
                        break;
                    case 3:
                        payload.put(Method.Add.FACILITY.toString(), Facilities.Types.MR1);
                        terminate = true;
                        break;
                    case 4:
                        payload.put(Method.Add.FACILITY.toString(), Facilities.Types.MR2);
                        terminate = true;
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Invalid choice!");
                        break;
                }
            }
            this.transport.send(this.serverAddr, main.common.Util.putInHashMapPacket(Method.Methods.ADD, payload));

            /** Add timeout here **/
            RawMessage res = this.transport.receive();
            String rcv_method = (String) res.packet.get(Method.METHOD);
            if (rcv_method.equals(Method.Methods.ADD.toString())) {
                System.out.println("Message received from main.server: ");
                System.out.println(res.packet);
            } else {
                throw new MethodNotFoundException("Client.addBooking - Unexpected Method! Expecting method 'ADD'");
            }
        } catch(RuntimeException e) {
            System.out.println("Client.addBooking - " + e.getClass().toString() + ": " + e.getMessage());
        }
    }

    public void changeBooking() {
        String uuid = readLine("Please enter the confirmation ID of the booking: ");
        int offset = safeReadInt("Please enter the offset desired for this booking in minutes (negative for advancement, positive for postponement)\n(1 => 1min, 60 => 1hour, 3600 => 1 day): ");
        HashMap<String, Object> payload = new HashMap<>();
        payload.put(Method.Change.UUID.toString(), uuid);
        payload.put(Method.Change.OFFSET.toString(), offset);

        try {
            this.transport.send(this.serverAddr, main.common.Util.putInHashMapPacket(Method.Methods.CHANGE, payload));

            /** Add timeout here **/
            RawMessage res = this.transport.receive();
            String rcv_method = (String) res.packet.get(Method.METHOD);
            if (rcv_method.equals(Method.Methods.CHANGE.toString())) {
                System.out.println("Message received from main.server: ");
                System.out.println(res.packet);
            } else {
                throw new MethodNotFoundException("Client.changeBooking - Unexpected Method! Expecting method 'CHANGE'");
            }
        } catch(RuntimeException e) {
            System.out.println("Client.changeBooking - " + e.getClass().toString() + ": " + e.getMessage());
        }
    }

    /**
     * For simplicity, you may assume that the user that has issued a register request for monitoring is blocked from inputting any new request until the monitor interval expires,
     * i.e., the client simply waits for the updates from the server during the monitoring interval. As a result, you do not have to use multiple threads at a client.
     */
    public void monitorAvailibility() {
        boolean terminate = false;
        HashMap<String, Object> payload = new HashMap<>();
        int monitorInterval = safeReadInt("Please enter your monitor interval in minutes\n(1 => 1min, 60 => 1hour, 3600 => 1 day): ");
        payload.put(Method.Monitor.INTERVAL.toString(), monitorInterval);

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
                        payload.put(Method.Monitor.FACILITY.toString(), Facilities.Types.LT1);
                        terminate = true;
                        break;
                    case 2:
                        payload.put(Method.Monitor.FACILITY.toString(), Facilities.Types.LT2);
                        terminate = true;
                        break;
                    case 3:
                        payload.put(Method.Monitor.FACILITY.toString(), Facilities.Types.MR1);
                        terminate = true;
                        break;
                    case 4:
                        payload.put(Method.Monitor.FACILITY.toString(), Facilities.Types.MR2);
                        terminate = true;
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Invalid choice!");
                        break;
                }
            }
            this.transport.send(this.serverAddr, main.common.Util.putInHashMapPacket(Method.Methods.MONITOR, payload));

            /** Add 10s for latency issues? **/
            LocalDateTime end = LocalDateTime.now().plusMinutes(Long.valueOf(monitorInterval)).plusSeconds(10);

            /** Blocks user until interval expires, while continuously listen to packets from server
             * If no packets received, i.e. no updates within interval, receive end packet from server to end monitoring
             * **/

            while (LocalDateTime.now().isBefore(end)) {
                /** Add timeout here **/
                RawMessage res = this.transport.receive();
                String rcv_method = (String) res.packet.get(Method.METHOD);
                if (rcv_method.equals(Method.Methods.MONITOR.toString())) {
                    System.out.println("Message received from main.server: ");
                    System.out.println(res.packet);
                } else {
                    throw new MethodNotFoundException("Client.monitorBooking - Unexpected Method! Expecting method 'MONITOR'");
                }
            }
            System.out.println(monitorInterval + " mins elapsed. Interval expired");
        } catch(RuntimeException e) {
            System.out.println("Client.monitorBooking - " + e.getClass().toString() + ": " + e.getMessage());
        }

    }

    // an idempotent operation

    // a non-idempotent operation
    public void extendBooking() {
        String uuid = readLine("Please enter the confirmation ID of the booking: ");
        double extend = safeReadDouble("Please enter the extension desired for this booking in hours (30-minute block)\n(i.e. 30-minute => 0.5, 2-hours => 2): ");
        while (extend <= 0 || extend % 0.5 != 0.0) {
            extend = safeReadDouble("Your input is not in multiples of 0.5 or is <= 0!\nPlease enter the extension desired for this booking (in 30-minute block)\n(i.e. 30-minute => 0.5, 2-hours => 2): ");
        }
        HashMap<String, Object> payload = new HashMap<>();
        payload.put(Method.Extend.UUID.toString(), uuid);
        payload.put(Method.Extend.EXTEND.toString(), extend);

        try {
            this.transport.send(this.serverAddr, main.common.Util.putInHashMapPacket(Method.Methods.EXTEND, payload));

            /** Add timeout here **/
            RawMessage res = this.transport.receive();
            String rcv_method = (String) res.packet.get(Method.METHOD);
            if (rcv_method.equals(Method.Methods.EXTEND.toString())) {
                System.out.println("Message received from main.server: ");
                System.out.println(res.packet);
            } else {
                throw new MethodNotFoundException("Client.extendBooking - Unexpected Method! Expecting method 'EXTEND'");
            }
        } catch(RuntimeException e) {
            System.out.println("Client.extendBooking - " + e.getClass().toString() + ": " + e.getMessage());
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
