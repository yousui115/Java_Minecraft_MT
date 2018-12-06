package yousui115.mt.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageGiveMeHdlr implements IMessageHandler<MessageGiveMe, IMessage>
{

    /**
     * ■Client -> Server
     */
    @Override
    public IMessage onMessage(MessageGiveMe message, MessageContext ctx)
    {
        if (ctx.side == Side.SERVER)
        {
            World world = Minecraft.getMinecraft().world;

            ctx.getServerHandler().player.getHeldItemMainhand().shrink(message.getNum());
        }

        return null;
    }

}
