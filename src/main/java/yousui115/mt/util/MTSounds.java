package yousui115.mt.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import yousui115.mt.MT;

public class MTSounds
{
    public static SoundEvent EYE_CATCH;

    public static SoundEvent NEGOTIATE;

    public static void init()
    {
        EYE_CATCH = new SoundEvent(new ResourceLocation(MT.MOD_ID, "eye-catch"));

        NEGOTIATE = new SoundEvent(new ResourceLocation(MT.MOD_ID, "negotiate"));
    }

}
