package yousui115.mt.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.mt.MT;
import yousui115.mt.capability.CapMobHdr;
import yousui115.mt.capability.MobHdr;
import yousui115.mt.text.TextMgr;
import yousui115.mt.text.TextNegotiate;
import yousui115.mt.util.Utils;

public class EventMob
{
    /**
     * ■キャパビリティの追加
     *   (この処理はEntityコンストラクタ内から呼び出される。
     *    よって、下記のjoinWorldMobより先に実行される。)
     * @param event
     */
    @SubscribeEvent
    public void addCapability(AttachCapabilitiesEvent<Entity> event)
    {
        //■MT対象モブの選別
        if (Utils.canTamed(event.getObject()) == false) { return; }

        //■手懐け可能なら、強制的にキャパビリティの追加
        event.addCapability(CapMobHdr.KYE, new MobHdr());
    }

    /**
     * ■対象モブが世界に降り立った瞬間
     *   (この時点で、追加Capabilityを保持している。
     *    また、AIの設定(initEntityAI())も終了している。)
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void joinWorldMob(EntityJoinWorldEvent event)
    {
        //■手懐け対象になり得るのか。
        if (Utils.canTamed(event.getEntity()) == false) { return; }
        EntityMob mob = (EntityMob)event.getEntity();

        //■手懐けていないなら、以降の処理は行わない。
        if (Utils.isTamed(mob) == false) { return; }

        //■手懐け済
        if (event.getWorld().isRemote)
        {
            //クライアント側の処理（同期催促とか）
        }
        else
        {
            //サーバ側の処理

            //再教育(AI再設定)
            Utils.reeducationAI(mob);
        }
    }

    /**
     * ■アップデートイベント
     * @param event
     */
    @SubscribeEvent
    public void updateMob(LivingUpdateEvent event)
    {
        //■キャパビリティ取得
        MobHdr hdrE = event.getEntity().getCapability(CapMobHdr.MobCap, null);
        if (hdrE == null) { return; }

        //■型変換
        //TODO:現状、キャパビリティ保持EntityはEntityMobの子クラスのみだが、
        //     仕様変更時に嵌る原因になりそうなので、対策必須。
        EntityMob mob = (EntityMob)event.getEntity();

        //■
        Utils.travelMob(mob, mob.moveStrafing, mob.moveVertical, mob.moveForward);
    }


    /**
     * ■右クリック！
     * @param event
     */
    @SubscribeEvent
    public void interactMob(PlayerInteractEvent.EntityInteract event)
    {
        //■サーバーのみ
        if (event.getWorld().isRemote) { return; }

        //■メインハンドのみ
        if (event.getHand() != EnumHand.MAIN_HAND) { return; }

        //■対象者:手懐け可能Mob
        if (Utils.canTamed(event.getTarget()) == false) { return; }
        EntityMob mob = (EntityMob)event.getTarget();

        EntityPlayer player = event.getEntityPlayer();

        //■
        if (Utils.isTamed(mob))
        {
            //手懐け済
            Utils.ridingMob((EntityLiving)mob, player);
        }
        //■
        else if (player.getHeldItemMainhand().getItem() == Items.EMERALD)
        {
            TextNegotiate nego = TextMgr.getText(mob.getClass().getSimpleName());
            if (nego != null)
            {
                //GUIをおーぷん
                player.openGui(MT.MOD_ID, 0, player.world, mob.getEntityId(), 0, 0);
            }
            else
            {
                //野生
                tameMob(mob, player);
            }
        }

    }

    /**
     *
     * @param mobIn
     * @param playerIn
     */
    private void tameMob(EntityMob mobIn, EntityLivingBase playerIn)
    {
        //■アレ を持っている
        if (playerIn.getHeldItemMainhand().getItem() != Items.EMERALD) { return; }

        //■一個使う
        playerIn.getHeldItemMainhand().shrink(1);

        //■腕を振る。
        playerIn.swingArm(EnumHand.MAIN_HAND);

        //■お返事。
        mobIn.playLivingSound();

        //■手懐け！
        Utils.tamedMob(mobIn, playerIn);
    }


    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void thirdPersonViewOffsetPre(RenderPlayerEvent.Pre event)
    {
//        if (Configs.isInstGog && Utils_Gaia.isRidingArachne(event.getEntityPlayer()))
        if (Utils.isRidingTamedMob(event.getEntityPlayer()))
        {
            EntityMob mob = (EntityMob)event.getEntityPlayer().getRidingEntity();

            GlStateManager.pushMatrix();

            Vec3d offset = Utils.offsetRidingView(mob, false);

            //■回転角を算出
            float head = event.getRenderer().getMainModel().bipedHead.rotateAngleY;
            float yaw  = (event.getEntityPlayer().rotationYaw % 360f) * (float)Math.PI / 180f;
            offset = offset.rotateYaw(head - yaw);

//            System.out.println("head = " + head);
//            System.out.println("yaw  = " + yaw);
//            System.out.println("====================================");

            GlStateManager.translate(offset.x, offset.y, offset.z);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void thirdPersonViewOffsetPost(RenderPlayerEvent.Post event)
    {
        if (Utils.isRidingTamedMob(event.getEntityPlayer()))
        {
            GlStateManager.popMatrix();
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void firstPersonViewOffset(EntityViewRenderEvent.CameraSetup event)
    {
        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 &&
            event.getEntity() instanceof EntityLivingBase &&
            Utils.isRidingTamedMob((EntityLivingBase)event.getEntity()))
        {
            //■3.再設定
            GlStateManager.rotate(event.getRoll(), 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(event.getPitch(), 1.0F, 0.0F, 0.0F);
//            GlStateManager.rotate(event.getYaw(), 0.0F, 1.0F, 0.0F);

            //■2.補正
            EntityMob mob = (EntityMob)event.getEntity().getRidingEntity();
            Vec3d offset = Utils.offsetRidingView(mob, true);
            GlStateManager.translate(offset.x, offset.y, offset.z);

            //■1.相殺
//            GlStateManager.rotate(-event.getYaw(), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-event.getPitch(), 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-event.getRoll(), 0.0F, 0.0F, 1.0F);
        }
    }
}
