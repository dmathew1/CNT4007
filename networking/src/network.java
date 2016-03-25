import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


//This is a server
public class network {
    private final static int port = 8080;

    public static class Mailbox {
        private sender.packet pck;
        boolean pckPresent;
        boolean ackPresent;
        private receiver.packetAck ack;

        Mailbox(){
            pck = null;
            ack = null;
            ackPresent = false;
            pckPresent = false;
        }

        public receiver.packetAck getAck(){
            ackPresent = false;
            receiver.packetAck ack = this.ack;
            this.ack = null;
            return ack;
        }
        public sender.packet getPck(){
            pckPresent = false;
            sender.packet pck = this.pck;
            this.pck = null;
            return pck;
        }
        public void putAck(receiver.packetAck ack){
            this.ack = ack;
            ackPresent = true;
        }
        public void putPck(sender.packet pck){
            this.pck = pck;
            pckPresent = true;
        }
        public boolean pckWaiting(){
            return pckPresent;
        }
        public boolean ackWaiting(){
            return ackPresent;
        }
    }


    //handle many receiver connections
    public static class senderHandler extends Thread{
        private Socket socket;
        private Mailbox mailbox;

        public senderHandler(Socket socket, Mailbox mailbox){
            this.socket = socket;
            this.mailbox = mailbox;
        }

        @Override
        public void run() {

        }
    }

    //handle many sender connections
    public static class receiverHandler  extends Thread{
        private Socket socket;
        private Mailbox mailbox;

        public receiverHandler(Socket socket, Mailbox mailbox){
            this.socket = socket;
            this.mailbox = mailbox;
        }


        @Override
        public void run() {
            try{
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                System.out.println(((sender.packet)in.readObject()).getString());

                //mailbox.putPck(in);
                sender.packet p;

                //waiting for in which is a packet

                while(true){
                    if(mailbox.pckWaiting()){
                       p = mailbox.getPck();

                String state = getResult();
                if(state.equals("PASS")){
                    //send packet as is to receiver
                    out.writeObject(p);
                }else if(state.equals("CORRUPT")){
                    int originalCheck = p.getCheck();
                    p.setCheck(originalCheck + 1);
                    //send packet to the receiver
                    out.writeObject(p);
                }else{
                    //this is for dropped packet
                    //send message back to sender

                }

                //reset
                in = null;
                //waiting for in which is a packet
                while(in == null){
                   // in = new ObjectInputStream(socket.getInputStream());
                }

                receiver.packetAck ack = (receiver.packetAck)in.readObject();
                state = getResult();
                if(state.equals("PASS")){
                    //send ack as is to sender
                    out.writeObject(p);
                }else if(state.equals("CORRUPT")){
                    int originalCheck = p.getCheck();
                    p.setCheck(originalCheck + 1);
                    //send packet to the receiver
                    out.writeObject(p);
                }else{
                    //this is for dropped packet
                    //send message back to sender

                }
                // down here we are reading an ack
                //in.readObject()


            }else{




                    }
                }

                }catch( IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
        }
    }


    public static String getResult(){
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
        Mailbox mailbox = new Mailbox();
        while(true){
            ByteInputStream byteInput = new ByteInputStream(new byte[1],1);

            if(byteInput.getBytes()[0] == 0){
                Thread thread = new receiverHandler(server.accept(), mailbox);
                thread.start();
            }else{
                Thread thread = new senderHandler(server.accept(), mailbox);
                thread.start();
            }
        }

    }

}
