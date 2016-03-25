import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by denzel on 3/24/16.
 */

//This is a server
public class network {
    private final static int port = 8080;

    //handle many sender connections
    public static class senderHandler implements Runnable{
        private Socket socket;


        public senderHandler(Socket socket){
            this.socket = socket;
            run();
        }

        @Override
        public void run() {

        }
    }

    //handle many sender connections
    public static class receiverHandler implements Runnable{
        private Socket socket;

        public receiverHandler(Socket socket){
            this.socket = socket;

            run();
        }

        @Override
        public void run() {
            try{
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                sender.packet p = (sender.packet)in.readObject();
                String state = getState();
                if(state.equals("PASS")){
                    //send packet as is to receiver
                }else if(state.equals("CORRUPT")){
                    int originalCheck = p.getCheck();
                    p.setCheck(originalCheck + 1);
                    //send packet to the receiver
                }else{
                    //this is for dropped packet
                }
            }catch(ClassNotFoundException | IOException e){
                System.out.println("WHY");
            }
        }
    }

    public static String getState(){
        String state = "";
        double percentage = Math.random();
        if(percentage < .5){
            state = "PASS";
        }else if(percentage >= .5 && percentage <= .75){
            state = "CORRUPT";
        }else{
            state = "DROP";
        }
        return state;
    }


    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(port);

        while(true) {
            //buffer has to be passed in to the sender
            senderHandler senderHandler = new senderHandler(server.accept());

            //buffer has to be passed in to the receiver'
            receiverHandler receiverHandler = new receiverHandler(server.accept());
        }

    }

}
