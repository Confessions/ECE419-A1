import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.Comparator;
import java.util.PriorityQueue;

public class ServerListenerThread implements Runnable {

    private MSocket mSocket =  null;
    private BlockingQueue eventQueue = null;
    private int localSquenceNumber = 0;
    private Comparator<MPacket> comparator = new MPacketComparator();
    private PriorityQueue<MPacket> queue = 
            new PriorityQueue<MPacket>(1000, comparator);

    public ServerListenerThread( MSocket mSocket, BlockingQueue eventQueue){
        this.mSocket = mSocket;
        this.eventQueue = eventQueue;
    }
    //implement local queue to insure key press consistency

    public void run() {
        MPacket received = null;
        MPacket first = null;
        if(Debug.debug) System.out.println("Starting a listener");
        while(true){
            try{
                received = (MPacket) mSocket.readObject();
                queue.add(received);
                if(Debug.debug) System.out.println("Received: " + received);
                    first = queue.peek();
                    if(Debug.debug) System.out.println("First MPacket sequence number is "
                        +first.sequenceNumber);
                    if(Debug.debug) System.out.println("First recieved sequence number is "
                    +received.sequenceNumber);
                    if(Debug.debug) System.out.println("local sequence number is "
                    + localSquenceNumber);
                    if (localSquenceNumber == first.sequenceNumber )
                    {
                        if(Debug.debug) System.out.println("inside localSquenceNumber == first.sequenceNumber ");
                        first = queue.remove();
                        eventQueue.put(first);
                        localSquenceNumber++;
                        queuecheck : while(queue.size() != 0)
                        {
                            first = queue.peek();
                            if (localSquenceNumber == first.sequenceNumber)
                            {
                                first = queue.remove();
                                eventQueue.put(first);
                                localSquenceNumber++;
                            }
                            else
                            {
                                break queuecheck;
                            }
                        }
                    }    

            }catch(InterruptedException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }
            
        }
    }
}
