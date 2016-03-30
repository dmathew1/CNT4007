import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;


public class sender {

    //text path
    private static final String path = "./src/text.txt";

    //port
    private static final int port = 8080;

    //url
    private static final String url = "localhost";

    //the packet class that encapsulate everything
    public static class packet implements Serializable{
        private byte seq;
        private byte id;
        private int check;
        private String word;

        public packet(byte seq, byte id, int check, String word){
            //String str = new String(seq, StandardCharsets.UTF_8);
            //String val = seq;
            this.seq = seq;
            this.id = id;
            this.check = check;
            this.word = word;
        }

        public packet(int i){
            this.seq  = (byte)i;
        }

        public packet(packet packet){
            this.seq = packet.seq;
            this.id = packet.id;
            this.check = packet.check;
            this.word = packet.word;
        }

        public byte getSeq(){
            return this.seq;
        }

        public byte getID(){
            return this.id;
        }

        public int getCheck(){
            return this.check;
        }

        public String getString(){
            return this.word;
        }

        public void setCheck(int check){
            this.check = check;
        }
    }

    public static int calcCheck(String string){
        int total = 0;

        for(int i = 0; i < string.length(); i++){
            total += string.charAt(i);
        }

        return total;
    }

    //creating packets
    public static ArrayList<packet> packetCreator() throws IOException{
        ArrayList<packet> packetArray = new ArrayList<>();
        byte position = 0;
        byte seq = 0;
        int check = 0;

        //read in file
        File text = new File(path);
        Scanner reader = new Scanner(text);

        //calculate the total amount of packets
        while(reader.hasNext()){
            seq = (byte)(1-seq);

            String currentString = reader.next();
            check = calcCheck(currentString);

            packet p = new packet(seq,position,check,currentString);
            packetArray.add(p);
            position++;
        }

        return packetArray;
    }

    //sender main method
    public static void main(String[] args) throws Exception{

        //creating socket
        Socket socket = new Socket(url,port);

        //create packets and put them into an arraylist
        ArrayList<packet> packetArrayList;
        packetArrayList = packetCreator();

        //open the channels for reading and writing
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        //selector for the sender line
        out.writeInt(1);
        out.flush();


        //send the packets
        //for each packet
        int sent = 0;
        for(int i=0;i<packetArrayList.size();i++) {

            //flag to check whether the packet was successfully sent
            boolean succesful = false;
            while(!succesful) {

                //send packet to the network
                out.writeObject(packetArrayList.get(i));
                out.flush();
                sent++;
                System.out.println("Waiting ACK" + packetArrayList.get(i).getSeq() + ", " + sent + " ,  ");

                //receive the ack
                receiver.packetAck ack = (receiver.packetAck)in.readObject();

                //check the acks checksum
                if(ack.check != 0){
                    System.out.println("Corrupt, resend packet " + packetArrayList.get(i).getSeq());
                    continue;
                }
                else if(ack.seq != packetArrayList.get(i).getSeq()){
                    System.out.println("ACK " + ack.seq + ", resend packet " + packetArrayList.get(i).seq);
                }
                else if(ack.check == 2){
                    System.out.println("Drop, resend packet " + packetArrayList.get(i).seq);
                    continue;
                }else{
                    System.out.println("Ack " + ack.seq + ", send packet " + (1-packetArrayList.get(i).seq));
                    succesful = true;
                }
            }
        }
        out.writeObject(new packet(-1));
        out.close();
        in.close();
        socket.close();
    }
}