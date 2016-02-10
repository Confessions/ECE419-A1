Introduction
============
This implements a client-server Mazewar game. 
Additionally, the MSocket class can be used to 
induce errors, such as adding delays and reordering packets.


Architecture
============
The server starts and listens on some port.

When a client contacts the server, the server spawns a 
ServerListenerThread. Server.java defines the MAX_CLIENTS constant 
that determines the maximum number of clients that join.

When the expected number of clients have contacted the 
server, the server spawns a single ServerSenderThreads, that broadcasts 
the name and location of all the clients (this requires that names
must be unique).

When an event happens, it is sent to the server. The server
stamps the event with a global sequence number and broadcasts to all 
hosts.

The client and server communicates using MSockets, and MServerSockets
which are analogous to Sockets and ServerSockets. MSockets are 
constructed by passing a host address and port. MSockets expose two 
main methods, readObject and writeObject to read and write objects, respectively. 

The MSocket can reorder packets and add delays based on 
3 constants defined in MSocket.java, namely DELAY_WEIGHT, DELAY_THRESHOLD, 
UNORDER_FACTOR. Errors are added both on sending and receiveing packets.


Making and Running
==================
To make the project run
make

To run the server:
</path/to/java/>java Server <listening port>

To run the clients: 
</path/to/java/>java Mazewar <server host> <server port>


Documentation
=============
Xun Jiang 999921349
Ying Zhou 999864669

Server to client inconsistency
in this lab we fixed the server to client inconsistency by adding a priority queue to insert the packets with its sequence number which is stamped by the server,
the we will peek the queue untill there is a match to the sequence number we desire to avoid processing packets out of order. 

client to server inconsistency 
we have also fixed the client to server key pressing latency by inplementing a priority queue and included a local sequence number that is only obtainable by the server listen thread that is dedicated to one client to avoid more consistency issue.

bullet inconsistency 
we also found out that sometimes when the two client fire at each other, the bullet will not disappear rather stay inside the cell's content as a projectile. the projecttile's projectilemap entry will be delete just fine. we have implemented a new packet type called destory. once the local client have two bullets meet at certain point we will add both projectile to the destory projectile list how ever, we do not clear the content in the cells. then we send these two cells to the server and ask all the client to clear the cell at two points. finally, after we recieved the destory packet if we can still locate the current projecttile in the projectilemap we will simply ignore such packet to avoid remove the bullet currently in that cell due to latency.

spawn inconsistency
this is really easy implementation, we just hardcode the spawn direction then it is funciotnal.