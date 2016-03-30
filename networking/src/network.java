import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;


//This is a server that both the sender and receiver will connect to exchange packets
public class network implements Runnable {

    //Random object
    Random random = new Random();

    //the port that all clients should connect to
    private final static int port = 8080;

    //buffer to hold the packets
    Buffer buffer;

    //the socket where things will connect to
    Socket socket;

    //constructor
    //every network thread needs to take in the socket being passed in and the global container
    public network(Socket socket, Buffer buffer){
        this.socket = socket;
        this.buffer = buffer;
    }

    @Override
    public void run() {
        boolean running = true;

        //read in the first value
        try {
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            //selector
            int select = (int) inputStream.readInt();

            //This is the sender contacting the network server
            if (select == 1) {
                while (running) {

                    //get the packet from the sender
                    sender.packet packet = (sender.packet) inputStream.readObject();

                    if (packet.getSeq() == -1) {
                        break;
                    }

                    System.out.println("Received: packet " + packet.getSeq() + ", " + packet.getID() + ", ");


                    //random generator
                    Double rand = random.nextDouble();
                    if (rand < 0.5D) {
                        System.out.println("PASS");
                        buffer.putPacket(packet);
                    } else if (rand < 0.75D) {
                        int check = packet.getCheck();
                        System.out.println("CORRUPT");
                        sender.packet newPacket = new sender.packet(packet);
                        newPacket.setCheck(check + 1);
                        buffer.putPacket(newPacket);
                    } else {
                        System.out.println("DROP");
                        outputStream.writeObject(new receiver.packetAck((byte) 2));
                    }

                    //wait for the ack
                    buffer.waitForAck();

                    //get ack
                    receiver.packetAck ack = buffer.getAck();

                    //send to the sender
                    outputStream.writeObject(ack);
                }

                //terminate
                System.out.println("I should be terminating");
                buffer.putPacket(new sender.packet(-1));
            }

            //This is the receiver contacting the network server
            else if (select == 2) {
                sender.packet packet = null;
                while (running) {

                    //wait for the packet
                    buffer.waitForPacket();

                    //retrieve packet from the container
                    packet = buffer.getPacket();

                    //check the conditions for termination
                    if (packet.getSeq() == -1) {
                        outputStream.writeObject(packet);
                        outputStream.close();
                        socket.close();
                    }


                    //send the object to the receiver thread
                    outputStream.writeObject(packet);


                    //wait for the ack
                    receiver.packetAck ack = (receiver.packetAck) inputStream.readObject();

                    //run the random
                    Double rand = random.nextDouble();

                    //pass
                    if (rand < 0.5D) {
                        System.out.println("PASS");
                        buffer.putAck(ack);
                    } else if (rand < 0.75D) {
                        int check = ack.check;
                        System.out.println("CORRUPT");
                        receiver.packetAck newAck = new receiver.packetAck(ack);
                        newAck.setCheck(check + 1);
                        buffer.putAck(newAck);
                    } else {
                        System.out.println("DROP");
                        buffer.putAck(new receiver.packetAck((byte) 2));
                    }
                }
            }
        }

        catch(IOException e) {
            e.printStackTrace();
        }
        catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }

    }


    public static void main(String[] args) throws IOException {
        int threads = 0;

        //open up the server socket
        ServerSocket server = new ServerSocket(port);

        //create the buffer
        Buffer buffer = new Buffer();

        //spin only two threads that connect to this
        while(threads < 2){
            threads++;
            new Thread(new network(server.accept(),buffer)).start();
        }
    }
}