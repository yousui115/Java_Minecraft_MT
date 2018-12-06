package yousui115.mt.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yousui115.mt.util.Utils;

public class MessageTamedHdlr implements IMessageHandler<MessageTamed, IMessage>
{

    /**
     * ■Client -> Server
     */
    @Override
    public IMessage onMessage(MessageTamed message, MessageContext ctx)
    {
        EntityPlayerMP player = ctx.getServerHandler().player;

        Entity entity = player.world.getEntityByID(message.getEntityId());

        if (Utils.canTamed(entity))
        {
            Utils.tamedMob((EntityLiving)entity, player);
        }


        return null;
    }

}
