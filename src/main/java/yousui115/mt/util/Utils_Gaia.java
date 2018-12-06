package yousui115.mt.util;

public class Utils_Gaia
{
    /**
     * ■手懐け済のMobだけが来る（はず）
     * @param livingIn
     * @param ridderIn
     */
//    public static void ridingArachne(@Nonnull EntityLiving livingIn, @Nonnull EntityLivingBase ridderIn)
//    {
//        //■サーバーのみ
//        if (ridderIn.world.isRemote) { return; }
//
//        //■アラクネのみ
//        if (livingIn instanceof EntityGaiaArachne == false) { return; }
//
//        //■
//        ridderIn.startRiding(livingIn, true);
//    }


    /**
     * ■
     * @param livingIn
     */
//    public static void controllArachne(@Nonnull EntityLiving livingIn)
//    {
//        //■サーバーのみ
//        if (livingIn.world.isRemote) { return; }
//
//        //■主人がいるアラクネのみ
//        if (livingIn instanceof EntityGaiaArachne == false) { return; }
//        MobHdr hdrA = livingIn.getCapability(CapMobHdr.MobCap, null);
//        if (hdrA == null || hdrA.getOwnerID() == null) { return; }
//
//        //■搭乗者
//        Entity passenger = livingIn.getPassengers().isEmpty() ? null : livingIn.getPassengers().get(0);
//
//        //■AIコントロール
//        boolean isRidingMaster = passenger != null && hdrA.getOwnerID().compareTo(passenger.getUniqueID()) == 0;
//        boolean mustSwim = livingIn.getRidingEntity() instanceof EntityBoat == false;
//        livingIn.tasks.setControlFlag(1, !isRidingMaster);
//        livingIn.tasks.setControlFlag(4, !isRidingMaster && mustSwim);
//        livingIn.tasks.setControlFlag(2, !isRidingMaster);
//
//        livingIn.targetTasks.setControlFlag(1, !isRidingMaster);
//    }

    /**
     * ■
     */
//    public static void travelArachne(@Nonnull EntityLiving livingIn, float strafe, float vertical, float forward)
//    {
//        //■主人がいるアラクネのみ
//        if (livingIn instanceof EntityGaiaArachne == false) { return; }
//        MobHdr hdrA = livingIn.getCapability(CapMobHdr.MobCap, null);
//        if (hdrA == null || hdrA.getOwnerID() == null) { return; }
//
//        //■搭乗者
//        Entity passenger = livingIn.getPassengers().isEmpty() ? null : livingIn.getPassengers().get(0);
//        if (passenger instanceof EntityPlayer == false) { return; }
//        EntityPlayer player = (EntityPlayer)passenger;
//
////        if (this.isBeingRidden() && this.canBeSteered() && this.isHorseSaddled())
//        //■搭乗者が主人なら、指示に従います。
//        if (hdrA.getOwnerID().compareTo(passenger.getUniqueID()) == 0)
//        {
////            player.setPosition(livingIn.posX, livingIn.posY + livingIn.getMountedYOffset() - 10, livingIn.posZ);
//
////            EntityLivingBase entitylivingbase = (EntityLivingBase)this.getControllingPassenger();
//            livingIn.rotationYaw = player.rotationYaw;
//            livingIn.prevRotationYaw = livingIn.rotationYaw;
//            livingIn.rotationPitch = player.rotationPitch * 0.5F;
//
////            livingIn.setRotation(livingIn.rotationYaw, livingIn.rotationPitch);
//            livingIn.rotationYaw = livingIn.rotationYaw % 360.0F;
//            livingIn.rotationPitch = livingIn.rotationPitch % 360.0F;
//
//            livingIn.renderYawOffset = livingIn.rotationYaw;
//            livingIn.rotationYawHead = livingIn.renderYawOffset;
//            strafe = player.moveStrafing * 0.5F;
//            forward = player.moveForward;
//
//            if (forward <= 0.0F)
//            {
//                forward *= 0.25F;
////                livingIn.gallopTime = 0;
//            }
//
//            //■ジャンプ関連
////            if (livingIn.onGround && livingIn.jumpPower == 0.0F && livingIn.isRearing() && !livingIn.allowStandSliding)
////            {
////                strafe = 0.0F;
////                forward = 0.0F;
////            }
////
////            if (livingIn.jumpPower > 0.0F && !livingIn.isHorseJumping() && livingIn.onGround)
////            {
////                livingIn.motionY = livingIn.getHorseJumpStrength() * (double)livingIn.jumpPower;
////
////                if (livingIn.isPotionActive(MobEffects.JUMP_BOOST))
////                {
////                    livingIn.motionY += (double)((float)(livingIn.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
////                }
////
////                livingIn.setHorseJumping(true);
////                livingIn.isAirBorne = true;
////
////                if (forward > 0.0F)
////                {
////                    float f = MathHelper.sin(livingIn.rotationYaw * 0.017453292F);
////                    float f1 = MathHelper.cos(livingIn.rotationYaw * 0.017453292F);
////                    livingIn.motionX += (double)(-0.4F * f * livingIn.jumpPower);
////                    livingIn.motionZ += (double)(0.4F * f1 * livingIn.jumpPower);
////                    livingIn.playSound(SoundEvents.ENTITY_HORSE_JUMP, 0.4F, 1.0F);
////                }
////
////                livingIn.jumpPower = 0.0F;
////            }
////
////            livingIn.jumpMovementFactor = livingIn.getAIMoveSpeed() * 0.1F;
//
////            if (livingIn.canPassengerSteer())
//            {
//
//                livingIn.setAIMoveSpeed((float)livingIn.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
////                super.travel(strafe, vertical, forward);
//                livingIn.travel(strafe, vertical, forward);
//            }
////            else if (player instanceof EntityPlayer)
////            {
////                livingIn.motionX = 0.0D;
////                livingIn.motionY = 0.0D;
////                livingIn.motionZ = 0.0D;
////            }
//
////            if (livingIn.onGround)
////            {
////                livingIn.jumpPower = 0.0F;
////                livingIn.setHorseJumping(false);
////            }
//
//            livingIn.prevLimbSwingAmount = livingIn.limbSwingAmount;
//            double d1 = livingIn.posX - livingIn.prevPosX;
//            double d0 = livingIn.posZ - livingIn.prevPosZ;
//            float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;
//
//            if (f2 > 1.0F)
//            {
//                f2 = 1.0F;
//            }
//
//            livingIn.limbSwingAmount += (f2 - livingIn.limbSwingAmount) * 0.4F;
//            livingIn.limbSwing += livingIn.limbSwingAmount;
//
//
//        }
//        else
//        {
//            livingIn.jumpMovementFactor = 0.02F;
//
////            super.travel(strafe, vertical, forward);
//            livingIn.travel(strafe, vertical, forward);
//        }
//    }


    /**
     * ■
     */
//    public static boolean isRidingArachne(EntityLivingBase ridderIn)
//    {
//        return ridderIn.getRidingEntity() instanceof EntityGaiaArachne;
//    }
}
