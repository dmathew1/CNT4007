/**
 * Created by denzel on 3/30/16.
 */
public class Buffer {
    private sender.packet packet;
    private receiver.packetAck ack;

    Object packetLock = new Object();
    Object ackLock =new Object();

    Buffer(){
        packet = null;
        ack = null;
    }

    public receiver.packetAck getAck(){
        ackLock = false;
        receiver.packetAck ack = this.ack;
        this.ack = null;
        return ack;
    }
    public sender.packet getPacket(){
        packetLock = false;
        sender.packet pck = this.packet;
        this.packet = null;
        return pck;
    }
    public void putAck(receiver.packetAck ack){
        this.ack = ack;
        synchronized (ackLock){
            ackLock.notifyAll();
        }
    }
    public void putPacket(sender.packet pck){
        this.packet = pck;
        synchronized (packetLock){
            packetLock.notifyAll();
        }
    }
    public void waitForPacket() throws InterruptedException{
        synchronized (packetLock){
            packetLock.wait();
        }
    }
    public void waitForAck() throws InterruptedException{
        synchronized (ackLock){
            ackLock.wait();
        }
    }
}
