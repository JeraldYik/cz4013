# cz4013



## To-do list:

- [x] Idempotent function: Cancel active booking

- [ ] Non-idempotent function: Extend active booking time in 30-minute block

- [ ] Monitor Availability Function

- [x] Display appropriate output to User for Query Availability

- [x] Display appropriate output to User for Add Booking

- [x] Display appropriate output to User for Change Availability

- [ ] Implement fault-tolerance measures (loss of request & reply messages) - At-least-once
    - [x] Implement clientRecord-side timeout functionalities
    - [ ] Show that at-least-once invocation semantics can lead to wrong results for non-idempotent operations

- [ ] Implement fault-tolerance measures (loss of request & reply messages) - At-most-once
    - [ ] ~~Implement server-side timeout functionalities~~ (unnecessary)
    - [ ] Show that at-most-once invocation semantics work correctly for all operations

- [ ] Add type of semantics to use as argument to clientRecord/server

- [ ] Edit assigning of IP address of Server

- [ ] Implement marshalling/unmarshalling

- [ ] To check on flow of project (successes & errors)
    

    
