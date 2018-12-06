package yousui115.mt.container;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import yousui115.mt.capability.CapMobHdr;
import yousui115.mt.capability.MobHdr;
import yousui115.mt.network.MessageFav;
import yousui115.mt.network.PacketHandler;

public class ContainerNegotiate extends Container
{
    private EntityPlayer player;

    private EntityMob target;

    /**
     * ■コンストラクタ
     * @param playerIn
     */
    public ContainerNegotiate(EntityPlayer playerIn, EntityMob targetIn)
    {
        player = playerIn;

        target = targetIn;

        MobHdr hdrM = target.getCapability(CapMobHdr.MobCap, null);

        if (player instanceof EntityPlayerMP)
        {
            //DEBUG
            System.out.println("Server -> Client : MessageFav");
            PacketHandler.INSTANCE.sendTo(new MessageFav(hdrM.getFavor(), target.getEntityId()), (EntityPlayerMP)player);
        }
    }

    /**
     * ■GUIを開くか否か(true:開く false:開かない)
     */
    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        //TODO:エメラルド保持の場合にのみ、GUIが開く、とか
        return true;
    }

}
