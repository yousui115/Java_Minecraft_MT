package yousui115.mt.util;

import java.lang.reflect.Method;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.mt.MT;
import yousui115.mt.ai.EntityAIFollowOwnerMT;
import yousui115.mt.ai.EntityAIMasterRiding;
import yousui115.mt.ai.EntityAIOwnerHurtByTargetMT;
import yousui115.mt.ai.EntityAIOwnerHurtTargetMT;
import yousui115.mt.capability.CapMobHdr;
import yousui115.mt.capability.MobHdr;
import yousui115.mt.config.ConfigsJson;

public class Utils
{


    //========================  Tame  ===============================

    /**
     * ■MT対象モブの選別
     * @param livingIn
     * @return
     */
    public static boolean canTamed(@Nullable Entity entityIn)
    {
        //TODO:細かい設定(PathNavi,AI等と相談だ)

        //■EntityMob継承クラスのみ
        if (entityIn instanceof EntityMob)
        {
            return true;
        }

        return false;
    }

    /**
     * ■MT対象モブ（手懐け済）か否か
     */
    public static boolean isTamed(@Nonnull EntityLiving livingIn)
    {
        MobHdr mobHdr = livingIn.getCapability(CapMobHdr.MobCap, null);
        return mobHdr != null && mobHdr.getOwnerID() != null;
    }


    public static void tamedMob(@Nonnull EntityLiving targetIn, @Nonnull EntityLivingBase masterIn)
    {
        MobHdr hdrT = targetIn.getCapability(CapMobHdr.MobCap, null);
        if (hdrT == null) { return; }

        hdrT.setOwnerID(masterIn);

        Utils.reeducationAI(targetIn);
    }

    //========================  AI  ===============================

    /**
     * ■再教育
     * @param mobIn
     */
    public static void reeducationAI(@Nonnull EntityLiving livingIn)
    {
        //■行動関連
        // ▼
        tameAI(livingIn);


        //■ターゲット関連
        // ▼忘却
        removeTargetAI(livingIn);
        // ▼再教育
        tameTargetAI(livingIn);

        //■ルート検索の設定（扉を無視した探索）
        if (Utils.canDoorThrough(livingIn) == true &&
            livingIn.getNavigator() instanceof PathNavigateGround)
        {
            ((PathNavigateGround)livingIn.getNavigator()).setBreakDoors(true);
            livingIn.setCanPickUpLoot(true);

            livingIn.tasks.addTask(6, new EntityAIOpenDoor(livingIn, true));
        }

        //■デフォ名をカスタム名に登録しとくよ！
        livingIn.setCustomNameTag(livingIn.getName());
        //■デスポーン禁止！
        livingIn.enablePersistence();

        //■とりあえず落ち着いて。
        livingIn.setAttackTarget(null);
        livingIn.setLastAttackedEntity(null);
        livingIn.setRevengeTarget(null);
    }

    private static void tameAI(@Nonnull EntityLiving livingIn)
    {
        if (livingIn instanceof EntityMob)
        {
            livingIn.tasks.addTask(1, new EntityAIMasterRiding(livingIn));
            livingIn.tasks.addTask(6, new EntityAIFollowOwnerMT((EntityMob)livingIn, 1.0D, 7.0F, 2.0F));
        }
    }

    /**
     * ■教育されている、攻撃対象を忘却させる
     * @param livingIn
     */
    private static void removeTargetAI(@Nonnull EntityLiving livingIn)
    {
        Object[] sets = livingIn.targetTasks.taskEntries.toArray();

        for (int i = 0; i < sets.length; i++)
        {
            EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry = (EntityAITasks.EntityAITaskEntry)sets[i];
            EntityAIBase entityaibase = entityaitasks$entityaitaskentry.action;
            livingIn.targetTasks.removeTask(entityaibase);
        }
    }

    /**
     * ■攻撃(保護)対象の再教育
     * @param mobIn
     */
    private static void tameTargetAI(@Nonnull EntityLiving livingIn)
    {
        //TODO
        if (livingIn instanceof EntityMob)
        {
            livingIn.targetTasks.addTask(1, new EntityAIOwnerHurtByTargetMT((EntityMob)livingIn));
            livingIn.targetTasks.addTask(2, new EntityAIOwnerHurtTargetMT((EntityMob)livingIn));
            livingIn.targetTasks.addTask(3, new EntityAIHurtByTarget((EntityMob)livingIn, true, new Class[0]));
        }
    }

    //======================== ride ==============================


    /**
     * ■手懐け済のMobだけが来る（はず）
     * @param livingIn
     * @param ridderIn
     */
    public static void ridingMob(@Nonnull EntityLiving livingIn, @Nonnull EntityLivingBase ridderIn)
    {
        //■サーバーのみ
        if (ridderIn.world.isRemote) { return; }

        //■
        ridderIn.startRiding(livingIn, true);
    }


    /**
     * ■EntityLiving
     */
    public static void travelMob(@Nonnull EntityLiving livingIn, float strafe, float vertical, float forward)
    {
        //■きゃぱきゃぱ
        MobHdr hdrA = livingIn.getCapability(CapMobHdr.MobCap, null);
        if (hdrA == null || hdrA.getOwnerID() == null) { return; }

        //■搭乗者（Playerに限定）
        Entity passenger = livingIn.getPassengers().isEmpty() ? null : livingIn.getPassengers().get(0);
        if (passenger instanceof EntityPlayer == false) { return; }
        EntityPlayer player = (EntityPlayer)passenger;

        //■搭乗者が主人なら、指示に従います。
        if (hdrA.getOwnerID().compareTo(player.getUniqueID()) == 0)
        {
            livingIn.rotationYaw = player.rotationYaw;
            livingIn.prevRotationYaw = livingIn.rotationYaw;
            livingIn.rotationPitch = player.rotationPitch * 0.5F;

//            livingIn.setRotation(livingIn.rotationYaw, livingIn.rotationPitch);
            livingIn.rotationYaw = livingIn.rotationYaw % 360.0F;
            livingIn.rotationPitch = livingIn.rotationPitch % 360.0F;

            livingIn.renderYawOffset = livingIn.rotationYaw;
            livingIn.rotationYawHead = livingIn.renderYawOffset;
            strafe = player.moveStrafing * 0.5F;
            forward = player.moveForward;

            if (forward <= 0.0F)
            {
                forward *= 0.25F;
            }

            //■ジャンプ関連
            //isJumping
            boolean isJump = (boolean)ObfuscationReflectionHelper.getPrivateValue(EntityLivingBase.class, player, 48);
            int jumpTicks = (int)ObfuscationReflectionHelper.getPrivateValue(EntityLivingBase.class, livingIn, 65);

            if (livingIn.onGround && isJump == true && jumpTicks == 0)
            {
                try
                {
                    //TODO:難読化変換のもっとスマートなやり方が、きっとあるはず。
                    Method method = EntityLivingBase.class.getDeclaredMethod(MT.isDebug ? "jump" : "func_70664_aZ");
                    method.setAccessible(true);
                    method.invoke(livingIn);
                }
                catch (Exception e)
                {
                    throw new RuntimeException();
                }

                //jumpTicks
                ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, livingIn, 10, 65);
            }


//            if (livingIn.onGround && livingIn.jumpPower == 0.0F && livingIn.isRearing() && !livingIn.allowStandSliding)
//            {
//                strafe = 0.0F;
//                forward = 0.0F;
//            }
//
//            if (livingIn.jumpPower > 0.0F && !livingIn.isHorseJumping() && livingIn.onGround)
//            {
//                livingIn.motionY = livingIn.getHorseJumpStrength() * (double)livingIn.jumpPower;
//
//                if (livingIn.isPotionActive(MobEffects.JUMP_BOOST))
//                {
//                    livingIn.motionY += (double)((float)(livingIn.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
//                }
//
//                livingIn.setHorseJumping(true);
//                livingIn.isAirBorne = true;
//
//                if (forward > 0.0F)
//                {
//                    float f = MathHelper.sin(livingIn.rotationYaw * 0.017453292F);
//                    float f1 = MathHelper.cos(livingIn.rotationYaw * 0.017453292F);
//                    livingIn.motionX += (double)(-0.4F * f * livingIn.jumpPower);
//                    livingIn.motionZ += (double)(0.4F * f1 * livingIn.jumpPower);
//                    livingIn.playSound(SoundEvents.ENTITY_HORSE_JUMP, 0.4F, 1.0F);
//                }
//
//                livingIn.jumpPower = 0.0F;
//            }
//
//            livingIn.jumpMovementFactor = livingIn.getAIMoveSpeed() * 0.1F;

//            if (livingIn.canPassengerSteer())
            {

                livingIn.setAIMoveSpeed((float)livingIn.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
//                super.travel(strafe, vertical, forward);
                livingIn.travel(strafe, vertical, forward);
            }
//            else if (player instanceof EntityPlayer)
//            {
//                livingIn.motionX = 0.0D;
//                livingIn.motionY = 0.0D;
//                livingIn.motionZ = 0.0D;
//            }

//            if (livingIn.onGround)
//            {
//                livingIn.jumpPower = 0.0F;
//                livingIn.setHorseJumping(false);
//            }

            livingIn.prevLimbSwingAmount = livingIn.limbSwingAmount;
            double d1 = livingIn.posX - livingIn.prevPosX;
            double d0 = livingIn.posZ - livingIn.prevPosZ;
            float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;

            if (f2 > 1.0F)
            {
                f2 = 1.0F;
            }

            livingIn.limbSwingAmount += (f2 - livingIn.limbSwingAmount) * 0.4F;
            livingIn.limbSwing += livingIn.limbSwingAmount;


        }
        else
        {
            livingIn.jumpMovementFactor = 0.02F;

//            super.travel(strafe, vertical, forward);
            livingIn.travel(strafe, vertical, forward);
        }
    }


    /**
     * ■このModで騎乗できるようになったMobか否か
     * @param ridderIn
     * @return
     */
    public static boolean isRidingTamedMob(EntityLivingBase ridderIn)
    {
        //■Mobである
        //TODO:現状、キャパビリティ保持EntityはEntityMobの子クラスのみだが、
        //     仕様変更時に嵌る原因になりそうなので、対策必須。
        Entity riding = ridderIn.getRidingEntity();
        if (riding instanceof EntityMob == false) { return false; }

        //■キャパビリティ取得
        MobHdr hdrR = riding.getCapability(CapMobHdr.MobCap, null);

        return hdrR != null;
    }

    //========================  view  ================================

    public static final Vec3d ZERO = new Vec3d(0, 0, 0);

    /**
     * ■
     * @param ridingIn
     * @param isFirstPView
     */
    @SideOnly(Side.CLIENT)
    public static Vec3d offsetRidingView(@Nonnull EntityLiving ridingIn, boolean isFirstPView)
    {
        Vec3d offset = ZERO;
        if (ConfigsJson.VIEW_OFFSETS.containsKey(ridingIn.getClass().getSimpleName()))
        {
            offset = isFirstPView ? ConfigsJson.VIEW_OFFSETS.get(ridingIn.getClass().getSimpleName()).firstPerson :
                                    ConfigsJson.VIEW_OFFSETS.get(ridingIn.getClass().getSimpleName()).thirdPerson;
        }

        return offset;
    }


    //========================  other  ===============================

    /**
     *
     * @param livingIn
     * @return
     */
    public static boolean canDoorThrough(@Nonnull EntityLiving livingIn)
    {
        return livingIn.width <= 0.8F && livingIn.height <= 1.99F;
    }


    /**
     * ■同じ主人に仕える仲間。
     */
    public static boolean isSameOwner(@Nonnull EntityLiving attackerIn, @Nonnull EntityLiving revengerIn)
    {
        MobHdr hdrA = attackerIn.getCapability(CapMobHdr.MobCap, null);
        MobHdr hdrR = revengerIn.getCapability(CapMobHdr.MobCap, null);

        if (hdrA != null && hdrA.getOwnerID() != null && hdrR != null && hdrR.getOwnerID() != null)
        {
            return hdrA.getOwnerID().compareTo(hdrR.getOwnerID()) == 0;
        }

        return false;
    }

    /**
     * ■主人じゃん
     */
    public static boolean isOwner(@Nonnull EntityLivingBase ownerIn, @Nonnull EntityLiving livingIn)
    {
        MobHdr hdrL = livingIn.getCapability(CapMobHdr.MobCap, null);
        return hdrL != null && hdrL.isOwner(ownerIn);
    }
}
