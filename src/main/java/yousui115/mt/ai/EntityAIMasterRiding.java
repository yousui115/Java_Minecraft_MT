package yousui115.mt.ai;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import yousui115.mt.capability.CapMobHdr;
import yousui115.mt.capability.MobHdr;

public class EntityAIMasterRiding extends EntityAIBase
{

    protected EntityLiving taskOwner;

    public EntityAIMasterRiding(EntityLiving taskOwnerIn)
    {
        taskOwner = taskOwnerIn;

        //■排他ビット
        this.setMutexBits(0x111);
    }

    @Override
    public boolean shouldExecute()
    {
        //■マスターが乗っかってるか否か。
        List<Entity> passenger = taskOwner.getPassengers();
        if (passenger == null || passenger.isEmpty()) { return false;}

        //■キャパ
        MobHdr hdrM = taskOwner.getCapability(CapMobHdr.MobCap, null);
        //ここでfalse返すのは、別のどこかに不具合が潜んでいるはず。
        if (hdrM == null || hdrM.getOwnerID() == null) { return false; }

        //■主人か否か
        return passenger.get(0).getUniqueID().compareTo(hdrM.getOwnerID()) == 0;
    }

    @Override
    public void startExecuting()
    {
        super.startExecuting();

        taskOwner.getNavigator().clearPath();
    }

    /**
     * ■優先度による割込み可否
     */
    @Override
    public boolean isInterruptible()
    {
        //■割込み不可
        return false;
    }
}
