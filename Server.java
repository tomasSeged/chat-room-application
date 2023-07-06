import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.ConnectIOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A central server system for the chat room.
 * Transport Protocol: TCP.
 * Implements Runnable - to allow threading and concurrent execution
 * @author Tomas H. Seged
 */
public class Server implements Runnable{

    /**
     * List of connections(clients).
     */
    private ArrayList<ConnectionHandler> connectionsList;

    /**
     * Server socket.
     */
    private ServerSocket server;

    /**
     * Boolean helper.
     */
    private boolean isFinito;

    /**
     * Thread pool.
     * No need of creating connections everytime a client enters the chat room.
     * Allows re-using threads.
     */
    private ExecutorService threadPool;

    /**
     * Constructor.
     */
    public Server(){
        this.connectionsList = new ArrayList<>();
        isFinito = false;
    }

    @Override
    public void run() {
        try {
            /**
             * Server socket with port# 9999.
             */
            server = new ServerSocket(9999);

            //i
            /**
             * Initialize threadPool.
             */
            threadPool = Executors.newCachedThreadPool();

            while(!isFinito){

                /**
                 * Accept connection.
                 */
                Socket client = server.accept();

                /**
                 * Open a new instance of ConnectionHandler.
                 */
                ConnectionHandler handler = new ConnectionHandler(client);

                /**
                 * Add ConnectionHandler into connections list.
                 */
                connectionsList.add(handler);

                /**
                 * Run handler.
                 */
                threadPool.execute(handler);
            }

        } catch (Exception e) {
            shutdown();
        }
    }

    /**
     * Broadcast message to all clients in the chat room.
     * @param message message to be sent
     */
    public void broadcast(String message){
        for(ConnectionHandler ch : connectionsList){
            if(ch!=null)
                ch.sendMessage(message);
        }
    }

    /**
     * Helper function to shut down all connections.
     */
    public void shutdown() {
        try {
            isFinito = true;
            threadPool.shutdown();

            if (!server.isClosed())
                server.close();
        } catch(IOException e){
            //ignore
        }

        for(ConnectionHandler ch : connectionsList){
            ch.shutdown();
        }

    }

    /**
     * Inner class.
     * Handles individual client connections.
     */
    class ConnectionHandler implements Runnable{

        /**
         * Client socket.
         */
        private Socket client;
        /**
         * Inputting string from socket
         */
        private BufferedReader in;

        /**
         * Output to client.
         */
        private PrintWriter out;

        /**
         * Name of user in chat room.
         */
        private String username;


        /**
         * Constructor.
         * @param client client socket
         */
        public ConnectionHandler(Socket client){
            this.client = client;
        }


        /**
         * Executed when Server class is run.
         *
         */
        @Override
        public void run() {
            try{

                //Initialize out and in
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                //Get nickname from client
                out.println("\n******************************");
                out.println("* To change username: /chusr *");
                out.println("* To quit: /quit             *");
                out.println("******************************\n");


                //this.username = in.readLine();
                this.username = JOptionPane.showInputDialog("--WELCOME TO THE CHAT ROOM!-- \n\nPlease enter your username: ");

                JOptionPane.showMessageDialog(null, "Hello " + username + "!");

                System.out.println("----- " + username + " Connected! -----");

                /**
                 * Announce joining of nickname to all clients.
                 */
                broadcast("\n" + "----- " + username + " joined the chat room! -----");

                String message;
                /**
                 * Process message sent by client.
                 * If it starts with "/nick", allow change of nickname
                 * If it starts with "/quit", quit the chat room
                 * Else, broadcast the message sent.
                 */
                while((message = in.readLine()) != null){
                    //If client inputs "/nick"
                    if(message.startsWith("/chusr ")){

                        String[] inputSplit = message.split(" ",2);

                        if(inputSplit.length==2){
                            broadcast(username + " renamed to" + inputSplit[1]);
                            System.out.println(username + " renamed to " + inputSplit[1]);
                            username = inputSplit[1];
                            out.println(" Successfully changed nickname to " + username);
                        }
                        else{
                            out.println("No nickname provided!");
                        }
                    }
                    //If client inputs "/quit"
                    else if(message.startsWith("/quit")){
                        //announce that user has left the chat.
                        broadcast(username + " left the chat!");
                        shutdown();
                    }
                    //If client inputs anything else, broadcast it.
                    else{
                        broadcast(username + ": " + message);
                    }
                }

            }
            catch(IOException e){
                shutdown();
            }
        }

        /**
         * Helper function to send message.
         * @param message message to be sent
         */
        public void sendMessage(String message){
            out.println(message);
        }

        /**
         * Helper function to shut down a connection between server and a specific client.
         */
        public void shutdown(){
            try{
                in.close();
                out.close();

                if(!client.isClosed()){
                    client.close();
                }
            } catch(IOException e){
                //ignore
            }

        }
    }

    /**
     * Main function.
     * @param args no args
     */
    public static void main(String[] args) {

        Server server = new Server();
        server.run();
    }
}
