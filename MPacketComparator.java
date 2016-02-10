import java.util.Comparator;

public class MPacketComparator implements Comparator<MPacket>
{
    @Override
    public int compare(MPacket x, MPacket y)
    {

        if (x.sequenceNumber < y.sequenceNumber)
        {
            return -1;
        }
        if (x.sequenceNumber > y.sequenceNumber)
        {
            return 1;
        }
        return 0;
    }
}