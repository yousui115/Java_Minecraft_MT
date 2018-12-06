package yousui115.mt.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import yousui115.mt.client.gui.GuiScreenNegotiate;
import yousui115.mt.container.ContainerNegotiate;

public class GuiHandler implements IGuiHandler
{

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == 0)
        {
            //変数名がx, y, z だからといって、座標値を入れないといけない、なんて事はない。
            //何の値が入ってきているかさえ理解していれば、何ら問題ない。
            //Server,ClientのEntityIDは同期している、はず。(マルチは未確認)
            Entity entity = world.getEntityByID(x);
            if (entity instanceof EntityMob)
            {
                return new ContainerNegotiate(player, (EntityMob)entity);
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == 0)
        {
            Entity entity = world.getEntityByID(x);
            if (entity instanceof EntityMob)
            {
                return new GuiScreenNegotiate(player, (EntityMob)entity);
            }
        }
        return null;
    }

}
