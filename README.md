# cz4013



## To-do list:

- [x] Allow users to query availability of for bookable facilities

- [x] Allow users to add bookings for available facilities

- [x] Allow users to change active bookings via time offset

- [x] Idempotent function: Allow users to cancel active bookings

- [x] Allow users to register to monitor facility availability over a week

- [x] Non-idempotent function: Allow users to extend active booking in 30-minute blocks

- [ ] Implement fault-tolerance measures (loss of request & reply messages) - At-least-once
    - [x] Implement timeout simulation
    - [ ] Show that at-least-once invocation semantics can lead to wrong results for non-idempotent operations

- [ ] Implement fault-tolerance measures (loss of request & reply messages) - At-most-once
    - [x] Implement timeout simulation
    - [ ] Show that at-most-once invocation semantics work correctly for all operations

- [x] Add type of semantics to use as argument to clientRecord/server

- [ ] Allow users to manually assign server IP address and port number

- [ ] To check on flow of project (successes & errors)
    

    
