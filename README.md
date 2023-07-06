# chat-room-using-tcp

A chat room using TCP connection using Java. Using one central server, multiple clients (by using threading) will be able to join the chat room and communicate. Clients also have the ability to change their names. In addition, they will also be able to quit the chatroom.

For this project, I used the local IP to host the server. However, it can be changed to another Server's IP address.

How to Run:

- Compile both Server.java and Client.java
	=> javac *.java [inside src directory]

- Run server
	=> java Server
	
- Run as many clients as you want. 
	=> java Client
