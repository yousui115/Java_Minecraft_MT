package yousui115.mt.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import yousui115.mt.capability.CapMobHdr;
import yousui115.mt.capability.MobHdr;
import yousui115.mt.util.Utils;

public class EntityAIOwnerHurtByTargetMT extends EntityAITarget
{
    private EntityCreature taskOwner;
    private EntityLivingBase attackTarget;
    private int timestamp;

    public EntityAIOwnerHurtByTargetMT(EntityCreature taskOwnerIn)
    {
        super(taskOwnerIn, false);
        this.taskOwner = taskOwnerIn;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute()
    {
        //■手懐けられている。
        if (Utils.isTamed(taskOwner))
        {
            //■主人の取得
            MobHdr mobHdr = taskOwner.getCapability(CapMobHdr.MobCap, null);
            EntityLivingBase master = mobHdr.getOwner(taskOwner.world);

            if (master != null)
            {
                //■主人を攻撃した対象を目標に設定
                this.attackTarget = master.getRevengeTarget();
                int revengeTime = master.getRevengeTimer();

                //■リベンジ設定時間が異なる かつ 攻撃対象可能モブ かつ 攻撃対象可能モブ（特殊）
                return revengeTime != this.timestamp &&
                        this.isSuitableTarget(this.attackTarget, false) &&
                        shouldAttackEntity(this.attackTarget, master);
            }
        }

        return false;
    }

    /**
     * ■攻撃可能な対象であるか否か
     * @param attackerIn
     * @param ownerIn
     * @return
     */
    private boolean shouldAttackEntity(EntityLivingBase attackerIn, EntityLivingBase ownerIn)
    {
        //TODO
        return true;
    }

    /**
     * ■
     */
    @Override
    public void startExecuting()
    {
        //■キャパビリティ
        this.taskOwner.setAttackTarget(this.attackTarget);
        MobHdr mobHdr = taskOwner.getCapability(CapMobHdr.MobCap, null);

        //■主人の取得
        EntityLivingBase master = mobHdr.getOwner(taskOwner.world);

        if (master != null)
        {
            this.timestamp = master.getRevengeTimer();
        }

        super.startExecuting();
    }

}
