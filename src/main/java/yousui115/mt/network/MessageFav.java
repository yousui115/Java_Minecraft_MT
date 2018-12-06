package yousui115.mt.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageFav implements IMessage
{
    private int fav;
    public int getFav() { return fav; }

    private int entityId;
    public int getEntityId() { return entityId; }



    public MessageFav(){}
    public MessageFav(int favIn, int entityIdIn)
    {
        fav = favIn;
        entityId = entityIdIn;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        fav = buf.readInt();
        entityId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(fav);
        buf.writeInt(entityId);
    }

}
