import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.*;
import java.net.Socket;


public class receiver {

    //text path
    private static final String path = "src/text.txt";

    //port
    private static final int port = 8080;

    //url
    private static final String url = "localhost";

    //the packet class that encapsulate everything
    public static class packetAck implements Serializable {
        public byte seq;
        public int check;

        public packetAck(byte seq) {
            this.seq = seq;
        }

        public packetAck(receiver.packetAck ack){
            this.seq = ack.seq;
            this.check = ack.check;
        }

        public void setCheck(int check){

            this.check = check;
        }

    }

    public static boolean calculateCheck(String content, int checkSum){
        int total = 0;
        for(int i = 0; i < content.length(); i++){
            total += content.charAt(i);
        }
        if(total == checkSum){
            return true;
        }
        return false;
    }

    public static boolean checkSum(sender.packet packet){
        int sum = 0;
        for(int i = 0; i<packet.getString().length(); i++){
            sum += packet.getString().charAt(i);
        }
        if(sum == packet.getCheck()){
            return true;
        }else{
            return false;
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //creating socket
        Socket socket = new Socket(url, port);

        //packet Sequence
        int packetSeq = 0;

        //packet number
        int packetNum = 0;

        //creating the channels
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        //selector for the receiver line
        out.writeInt(2);
        out.flush();

        try{
            while(true){

                //get the packet from the network
                sender.packet packet = (sender.packet)in.readObject();
                packetNum++;

                if(packet.getSeq() == -1){
                    System.out.println("closing");
                    continue;
                }

                System.out.println("Waiting " + packetSeq + ", " + packetNum + ", " + packet.getSeq() + ", ");
                if(checkSum(packet)){

                    //not corrupt
                    System.out.println("Ack " + packet.getSeq());
                    packetAck ack = new packetAck(packet.getSeq());
                    out.writeObject(ack);

                    //correct ack
                    if(packetSeq == packet.getSeq()){
                        packetSeq = (byte)(1-packetSeq);
                    }
                }

                //corrupt packet received
                else{
                    System.out.println("Ack" + (1-packet.getSeq()));
                    packetAck ack = new packetAck((byte)(1-packet.getSeq()));
                    out.writeObject(ack);
                }
            }
        }catch (EOFException e){
            e.printStackTrace();
        }
    }
}