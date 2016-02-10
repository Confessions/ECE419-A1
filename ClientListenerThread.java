import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Hashtable;
import java.util.Comparator;
import java.util.PriorityQueue;

public class ClientListenerThread implements Runnable {

    private MSocket mSocket  =  null;
    private Hashtable<String, Client> clientTable = null;
    private int globalsquencenumber = 0;
    private Comparator<MPacket> comparator = new MPacketComparator();
    private PriorityQueue<MPacket> queue = 
            new PriorityQueue<MPacket>(1000, comparator);

    public ClientListenerThread( MSocket mSocket,
                                Hashtable<String, Client> clientTable){
        this.mSocket = mSocket;
        this.clientTable = clientTable;
        if(Debug.debug) System.out.println("Instatiating ClientListenerThread");
    }

    //going to make the listener check for global sequence number before invoke each client function
    public void run() {
        MPacket received = null;
        MPacket first = null;
        Client client = null;
        if(Debug.debug) System.out.println("Starting ClientListenerThread");
        while(true){
            try{
                received = (MPacket) mSocket.readObject();
                System.out.println("Received " + received);
                queue.add(received);
                queuecheck : while(queue.size() != 0)
                {
                    first = queue.peek();
                    if(Debug.debug) System.out.println("First MPacket sequence number is "
                        +first.sequenceNumber);
                    if(globalsquencenumber == first.sequenceNumber)
                    {
                        first = queue.remove();
                        client = clientTable.get(first.name);
                        if(first.event == MPacket.UP){
                            client.forward();
                        }else if(first.event == MPacket.DOWN){
                            client.backup();
                        }else if(first.event == MPacket.LEFT){
                            client.turnLeft();
                        }else if(first.event == MPacket.RIGHT){
                            client.turnRight();
                        }else if(first.event == MPacket.FIRE){
                            client.fire();
                        }else if(first.event == MPacket.DESTORY){
                            client.destoryed(first.point);
                        }else{
                            throw new UnsupportedOperationException();
                        }
                        globalsquencenumber++;    
                    }
                    else
                        break queuecheck; 

                    
                    
                }
                    
            }catch(IOException e){
                e.printStackTrace();
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }            
        }
    }
}
