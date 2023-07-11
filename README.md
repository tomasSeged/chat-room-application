# chat-room-application

A chat room Java application. Using one central server, multiple clients (by using threading) will be able to join the chat room and communicate. Clients also have the ability to change their names. In addition, they will also be able to quit the chatroom. Implemented using TCP application protocol.

For this project, I used the local IP to host the server. However, it can be changed to another Server's IP address.
[To run the central server using another Server's IP address, change the IP address in line 40 of CLient.java.]

How to Run:

- Compile both Server.java and Client.java
	=> javac *.java [inside src directory]

- Run server
	=> java Server
	
- Run as many clients as you want. 
	=> java Client
