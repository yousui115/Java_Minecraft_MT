package yousui115.mt.capability;

import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class MobHdr implements ICapabilitySerializable<NBTTagCompound>

{
    //■手懐けているか否か
//    private boolean isTamed = false;
//    public boolean isTamed() { return isTamed; }
//    public void setTamed(boolean isTamedIn) { isTamed = isTamedIn; }

    //■オーナーのUUID
    private UUID ownerID = null;
    public UUID getOwnerID() { return ownerID; }
    public void setOwnerID(UUID ownerIDIn) { ownerID = ownerIDIn; }
    public void setOwnerID(EntityLivingBase livingIn) { ownerID = livingIn.getUniqueID(); }
    public boolean isOwner(EntityLivingBase livingIn) { return ownerID.compareTo(livingIn.getUniqueID()) == 0; }
    public EntityLivingBase getOwner(World world) { return world.getPlayerEntityByUUID(ownerID); }

    //■待機状態(座らせる必要はない)
    private boolean sitting = false;
    public boolean isSitting() { return sitting; }
    public void setSitting(boolean sittingIn) { sitting = sittingIn; }

    //■好感度
    private int favor = 0;
    public int getFavor() { return favor; }
    public void setFavor(int favorIn) { favor = MathHelper.clamp(favorIn, 0, 255); }
    public void addFavor(int valueIn) { setFavor(favor + valueIn); System.out.println("add value = " + valueIn + " : values = " + favor); }

    //■同期フラグ
    private boolean isDirty = false;
    public boolean isDirty() { return isDirty; }
    public void setDirty() { isDirty = true; }
    public void cleanDirty() { isDirty = false; }

    //======================================================================


    /**
     * ■キャパビリティのチェック
     */
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == CapMobHdr.MobCap;
    }
    /**
     * ■キャパビリティの取得
     */
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return capability == CapMobHdr.MobCap ? (T)this : null;
    }

    /**
     * ■NBTへの書込（セーブ）
     */
    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();

        //■オーナー
        if (getOwnerID() == null)
        {
            nbt.setString("OwnerUUID", "");
        }
        else
        {
            nbt.setString("OwnerUUID", getOwnerID().toString());
        }

        //■待機
        nbt.setBoolean("Sitting", isSitting());

        //■好感度
        nbt.setInteger("Favor", getFavor());
        return nbt;
    }

    /**
     * ■NBTから読込（ロード）
     */
    @Override
    public void deserializeNBT(NBTTagCompound nbtIn)
    {
        String s = null;

        //■オーナー
        if (nbtIn.hasKey("OwnerUUID", 8))
        {
            s = nbtIn.getString("OwnerUUID");

            if (!s.isEmpty())
            {
                try
                {
                    setOwnerID(UUID.fromString(s));
                }
                catch (Throwable var4)
                {
                }
            }
        }

        //■待機
        setSitting(nbtIn.getBoolean("Sitting"));

        //■好感度
        setFavor(nbtIn.getInteger("Favor"));
    }

}
