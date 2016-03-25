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
        private BoundedBuffer bb;

        public senderHandler(Socket socket,BoundedBuffer bb){
            this.socket = socket;
            this.bb = bb;
            run();
        }

        @Override
        public void run() {
           bb.insert();
        }
    }

    //handle many sender connections
    public static class receiverHandler implements Runnable{
        private Socket socket;
        private BoundedBuffer bb;


        public receiverHandler(Socket socket,BoundedBuffer bb){
            this.socket = socket;
            this.bb = bb;

            run();
        }

        @Override
        public void run() {
          try{
              ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
              bb.consume(in.readObject());
          }catch(ClassNotFoundException | IOException e){
              System.out.println("WHY");
          }
        }
    }



    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(port);
        BoundedBuffer bb = new BoundedBuffer(10);

        while(true) {
            //buffer has to be passed in to the sender
            senderHandler senderHandler = new senderHandler(server.accept(), bb);

            //buffer has to be passed in to the receiver'
            receiverHandler receiverHandler = new receiverHandler(server.accept(), bb);
        }

    }

}
