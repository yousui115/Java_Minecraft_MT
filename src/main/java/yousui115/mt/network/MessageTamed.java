package yousui115.mt.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageTamed  implements IMessage
{
    private int entityId;
    public int getEntityId() { return entityId; }


    public MessageTamed() {}

    public MessageTamed(int entityIdIn)
    {
        entityId = entityIdIn;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        entityId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(entityId);
    }

}
