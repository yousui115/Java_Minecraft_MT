package yousui115.mt.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.mt.MT;

//@SuppressWarnings("WeakerAccess") // forge needs public access to fields here to write values from config
@Config(modid = MT.MOD_ID, category = "")
public class Configs {
    private Configs() {
    }

//    @SuppressWarnings({ "unused", "squid:S3985" })
    @Mod.EventBusSubscriber(modid = MT.MOD_ID)
    private static class EventHandler {

        private EventHandler() {
        }

        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(MT.MOD_ID)) {
                ConfigManager.sync(MT.MOD_ID, Config.Type.INSTANCE);
            }
        }
    }

    @Config.LangKey("configgui.mt.category.general")
    @Config.Comment("General")
    public static final General GENERAL = new General();

    public static class General {
        /*
         * SKY
         */
        @Config.LangKey("entity.mt.ender_dragon_girl.name")
        @Config.Comment("Sample Integer")
//        @Config.RequiresMcRestart //MC再起動要請
        public int sampleInteger = 4;
    }
}