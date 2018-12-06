package yousui115.mt.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import yousui115.mt.capability.CapMobHdr;
import yousui115.mt.capability.MobHdr;

public class MessageFavHdlr implements IMessageHandler<MessageFav, IMessage>
{

    /**
     * ■Server -> Client
     */
    @Override
    public IMessage onMessage(MessageFav message, MessageContext ctx)
    {
        World world = null;
        if (ctx.side == Side.CLIENT)
        {
            world = Minecraft.getMinecraft().world;
        }
        else
        {
            world = ctx.getServerHandler().player.world;
        }
        if (world == null) { return null; }

        //■
        Entity entity = world.getEntityByID(message.getEntityId());
        if (entity instanceof EntityMob == false) { return null; }

        //■
        MobHdr hdrM = entity.getCapability(CapMobHdr.MobCap, null);
        if (hdrM != null)
        {
            hdrM.setFavor(message.getFav());
        }

        return null;
    }

}
