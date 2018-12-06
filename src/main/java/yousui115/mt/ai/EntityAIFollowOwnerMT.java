package yousui115.mt.ai;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import yousui115.mt.capability.CapMobHdr;
import yousui115.mt.capability.MobHdr;

public class EntityAIFollowOwnerMT extends EntityAIBase
{
    private final EntityCreature taskOwner;
    private EntityLivingBase master;
    World world;
    private final double followSpeed;
    private final PathNavigate petPathfinder;
    private int timeToRecalcPath;
    float maxDist;
    float minDist;
    private float oldWaterCost;

    public EntityAIFollowOwnerMT(EntityCreature taskOwnerIn, double followSpeedIn, float minDistIn, float maxDistIn)
    {
        this.taskOwner = taskOwnerIn;
        this.world = taskOwnerIn.world;
        this.followSpeed = followSpeedIn;
        this.petPathfinder = taskOwnerIn.getNavigator();
        this.minDist = minDistIn;
        this.maxDist = maxDistIn;
        this.setMutexBits(3);

        if (!(taskOwnerIn.getNavigator() instanceof PathNavigateGround) && !(taskOwnerIn.getNavigator() instanceof PathNavigateFlying))
        {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        //■キャパビリティの取得
        MobHdr hdrM = taskOwner.getCapability(CapMobHdr.MobCap, null);
        if (hdrM == null || hdrM.getOwnerID() == null) { return false; }

        //■ご主人の取得
        EntityLivingBase master = hdrM.getOwner(world);

        //■AI起動条件群
        if (master == null)
        {
            return false;
        }
        else if (master instanceof EntityPlayer && ((EntityPlayer)master).isSpectator())
        {
            return false;
        }
        else if (isSitting())
        {
            return false;
        }
        else if (this.taskOwner.getDistanceSq(master) < (double)(this.minDist * this.minDist))
        {
            return false;
        }
        else
        {
            this.master = master;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return !this.petPathfinder.noPath() && this.taskOwner.getDistanceSq(this.master) > (double)(this.maxDist * this.maxDist) && !isSitting();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.taskOwner.getPathPriority(PathNodeType.WATER);
        this.taskOwner.setPathPriority(PathNodeType.WATER, 0.0F);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        this.master = null;
        this.petPathfinder.clearPath();
        this.taskOwner.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
        this.taskOwner.getLookHelper().setLookPositionWithEntity(this.master, 10.0F, (float)this.taskOwner.getVerticalFaceSpeed());


        if (!isSitting())
        {
            if (--this.timeToRecalcPath <= 0)
            {
                this.timeToRecalcPath = 10;

                if (!this.petPathfinder.tryMoveToEntityLiving(this.master, this.followSpeed))
                {
                    if (!this.taskOwner.getLeashed() && !this.taskOwner.isRiding())
                    {
                        if (this.taskOwner.getDistanceSq(this.master) >= 144.0D)
                        {
                            int i = MathHelper.floor(this.master.posX) - 2;
                            int j = MathHelper.floor(this.master.posZ) - 2;
                            int k = MathHelper.floor(this.master.getEntityBoundingBox().minY);

                            for (int l = 0; l <= 4; ++l)
                            {
                                for (int i1 = 0; i1 <= 4; ++i1)
                                {
                                    if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.isTeleportFriendlyBlock(i, j, k, l, i1))
                                    {
                                        this.taskOwner.setLocationAndAngles((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + i1) + 0.5F), this.taskOwner.rotationYaw, this.taskOwner.rotationPitch);
                                        this.petPathfinder.clearPath();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected boolean isTeleportFriendlyBlock(int x, int p_192381_2_, int y, int p_192381_4_, int p_192381_5_)
    {
        BlockPos blockpos = new BlockPos(x + p_192381_4_, y - 1, p_192381_2_ + p_192381_5_);
        IBlockState iblockstate = this.world.getBlockState(blockpos);
        return iblockstate.getBlockFaceShape(this.world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID && iblockstate.canEntitySpawn(this.taskOwner) && this.world.isAirBlock(blockpos.up()) && this.world.isAirBlock(blockpos.up(2));
    }

    /**
     * ■
     * @return
     */
    protected boolean isSitting()
    {
        MobHdr hdrM = taskOwner.getCapability(CapMobHdr.MobCap, null);
        return hdrM != null && hdrM.isSitting();
    }
}
