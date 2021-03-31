# CZ4013 Distributed Systems

## Repository Structure
Directory | Contents
----------| ---------
'client' | Codes for client side application
'server'| Codes for server side application
'common/facility' | Codes for handling booking
'common/message' | Codes for marshalling and unmarshalling messages
'common/network' | Codes for sending and receiving messages with socket


## Starting Server
1. Run ServerMain.java
2. Select invocation semantics
   * At-Least-Once: 0
   * At-Most-Once: 1

Example:
* java ServerMain.java
* 0: At-Least-Once Server 
* 1: At-Most-Once Server
* 0
* Listening on udp://127.0.0.1:49512

## Starting Client
1. Run ClientMain.java
2. Enter probability of packet loss (0.0 - 1.0)

Example:
* java ClientMain.java
* Enter preferred server reply failure probability (0.0 - 1.0):
* 0.5
* Please choose a service by typing [1-]:
* 1: Query Availability of a Facility
* 2: Add Booking to a Facility
* 3: Change Booking to a Facility
* 4: Monitor Availability of a Facility
* 5: (Idempotent) Cancel an active Booking
* 6: (Non-Idempotent) Extend an active Booking time in 30-minute block
* 7: View available test statements
* 8: Ping main.server
* 9: Print the manual
* 0: Stop the main.client
* (MAIN MENU) Your choice of service ('9' for MANUAL):
* 2

