import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Client class.
 * @author Tomas H. Seged
 */
public class Client implements Runnable{

    /**
     * Socket client.
     */
    private Socket client;

    /**
     * Input.
     */
    private BufferedReader in;

    /**
     * Output.
     */
    private PrintWriter out;

    /**
     * Boolean helper
     */
    private boolean isFinito;

    @Override
    public void run() {
        try{
            /**
             * Create a socket with local IP address and port 9999
             * Can be changed to another Server's IP address
             */
            client = new Socket("127.0.0.1",9999);

            /**
             * Initialize out and in.
             */
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            /**
             * Create and run a thread.
             */
            InputHandler inHandler = new InputHandler();
            Thread th = new Thread(inHandler);
            th.start();

            /**
             * Print out message
             */
            String inMessage;
            while((inMessage = in.readLine()) != null){
                System.out.println(inMessage);
            }


        } catch(IOException e){
            shutdown();
        }
    }

    /**
     * Helper function to shut down all connections.
     */
    public void shutdown(){
        isFinito = true;
        try{
            in.close();
            out.close();
            if(!client.isClosed())
                client.close();
        } catch(IOException e){

        }
    }

    /**
     * Inner class inputHandler.
     */
    class InputHandler implements Runnable{

        /**
         * Run function from Runnable
         */
        @Override
        public void run() {
            try{
                /**
                 * Pass the command line input
                 */
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

                /**
                 * Constantly read for input from user.
                 */
                while(!isFinito){
                    String message = inputReader.readLine();
                    /**
                     * If user wants to quit, shut down.
                     */
                    if(message.equals("/quit")){
                        out.println(message);
                        inputReader.close();
                        shutdown();
                    }
                    /**
                     * Else, send the message.
                     */
                    else{
                        out.println(message);
                    }
                }
            } catch(IOException e){
                shutdown();
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}
