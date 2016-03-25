import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


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

        public packetAck(byte seq, int check) {
            this.seq = seq;
            this.check = check;
        }
    }

    public static int calcCheck(String string) {
        int total = 0;

        for (int i = 0; i < string.length(); i++) {
            total += string.charAt(i);
        }

        return total;
    }

    public static boolean checkPacketSum(String content, int checkSum){
        int total = 0;

        for(int i = 0; i < content.length(); i++){
            total += content.charAt(i);
        }

        if(total == checkSum){
            return true;
        }

        return false;
    }

    //create packets
    public static packetAck packetCreator(sender.packet p) throws IOException {
        packetAck ack = null;
        if(checkPacketSum(p.getString(), p.getCheck())){
            ack = new packetAck(p.getSeq(), 0);
        }else{//this is for corrupt packets!
            ack = new packetAck((byte)(1 - p.getSeq()), 0);
        }
        return ack;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //creating socket
        Socket socket = new Socket(url, port);
        sender.packet p;
        ByteOutputStream byteout = new ByteOutputStream(1);
        byteout.write(0);
        byteout.close();

        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        p = (sender.packet) in.readObject();
        packetCreator(p);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        System.out.println(p.getString());

    }
}