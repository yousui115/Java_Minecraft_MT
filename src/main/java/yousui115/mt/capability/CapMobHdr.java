package yousui115.mt.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import yousui115.mt.MT;

public class CapMobHdr
{
    //■きゃぱびりてぃ
    @CapabilityInject(MobHdr.class)
    public static Capability<MobHdr> MobCap = null;

    //■鍵
    public final static ResourceLocation KYE = new ResourceLocation(MT.MOD_ID, "mob_data");

    /**
     * ■登録
     */
    public static void register()
    {
        CapabilityManager.INSTANCE.register(MobHdr.class, new Capability.IStorage<MobHdr>()
        {
            @Override
            public NBTBase writeNBT(Capability<MobHdr> capability, MobHdr instance, EnumFacing side)
            {
                NBTTagList nbtTagList = new NBTTagList();
                return nbtTagList;
            }

            @Override
            public void readNBT(Capability<MobHdr> capability, MobHdr instance, EnumFacing side, NBTBase base)
            {
            }
        }, MobHdr::new);
    }
}
