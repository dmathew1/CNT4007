import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by denzel on 3/24/16.
 */
public class sender {

    //text path
    private static final String path = "./text.txt";

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

    //main method
    public static void main(String[] args) throws IOException{
        //creating socket
        Socket socket = new Socket(url,port);
        ArrayList<packet> packetArrayList = new ArrayList<>();
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        packetCreator();

        ByteOutputStream byteout = new ByteOutputStream(1);
        byteout.write(1);
        byteout.close();

           for(int i = 0; i<packetArrayList.size(); i++){
               out.writeObject(packetArrayList.get(i));
           }
       }

}
