package yousui115.mt.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageGiveMe implements IMessage
{
    private int num;
    public int getNum() { return num; }

    public MessageGiveMe() {}

    public MessageGiveMe(int numIn)
    {
        num = numIn;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        num = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(num);
    }

}
