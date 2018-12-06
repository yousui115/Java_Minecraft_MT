package yousui115.mt.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import yousui115.mt.MT;

public class PacketHandler
{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(MT.MOD_ID);

    public static void register()
    {
        //■Server -> Client
        INSTANCE.registerMessage(MessageFavHdlr.class, MessageFav.class, 0, Side.CLIENT);
        //■Client -> Server
        INSTANCE.registerMessage(MessageFavHdlr.class, MessageFav.class, 1, Side.SERVER);
        //■Client -> Server
        INSTANCE.registerMessage(MessageGiveMeHdlr.class, MessageGiveMe.class, 2, Side.SERVER);
        //■Client -> Server
        INSTANCE.registerMessage(MessageTamedHdlr.class, MessageTamed.class, 3, Side.SERVER);
    }

}
