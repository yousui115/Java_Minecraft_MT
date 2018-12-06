package yousui115.mt;

import java.io.IOException;

import org.apache.logging.log4j.Logger;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.oredict.OreDictionary;
import yousui115.mt.capability.CapMobHdr;
import yousui115.mt.config.ConfigsJson;
import yousui115.mt.event.EventMob;
import yousui115.mt.network.PacketHandler;
import yousui115.mt.proxy.CommonProxy;
import yousui115.mt.text.TextReader;
import yousui115.mt.util.GuiHandler;
import yousui115.mt.util.MTSounds;

@Mod(modid = MT.MOD_ID, name = MT.MOD_NAME, version = MT.MOD_VERSION)
public class MT
{
    public static final String MOD_ID = "yousui115.mt";
    public static final String MOD_NAME = "MT";
    public static final String MOD_VERSION = "M1122_F2705_v1";

    //■インスタント
    @Mod.Instance(MOD_ID)
    public static MT instance;

    //■ぷろくし
    @SidedProxy(clientSide = MOD_ID + ".proxy.ClientProxy", serverSide = MOD_ID + "proxy.CommonProxy")
    public static CommonProxy proxy;

    //■本番環境か否か
    public static boolean isDebug;

    //■
    private static Logger logger;


    /**
     * ■初期化処理（前処理）
     * @param event
     * @throws IOException
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) throws IOException
    {
        //■環境の識別
        isDebug = event.getSourceFile().isDirectory() && (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

        //■ロガー
        logger = event.getModLog();

        //■コンフィグ
        try
        {
            ConfigsJson.read(event);
        }
        catch(IOException e)
        {
            throw e;
        }

        //■Capabilityの登録
        CapMobHdr.register();

        //■Eventの登録
        MinecraftForge.EVENT_BUS.register(new EventMob());

        //■パケット
        PacketHandler.register();

        //■鉱石辞書
        OreDictionary.registerOre("emerald", new ItemStack(Items.EMERALD));

        //■てきすとりーだー
        TextReader.readText(event.getSourceFile());
    }

    /**
     * ■初期化処理（本処理）
     * @param event
     */
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        //■サポート対象のModを導入しているか否か
        for (ModContainer cont : Loader.instance().getModList())
        {
            //■Hostile Mobs and Girls
            if (cont.getModId().equals("hmag") == true)
            {
                ConfigsJson.isInstHMaG = true;
            }
            else if (cont.getModId().equals("grimoireofgaia") == true)
            {
                ConfigsJson.isInstGog = true;
            }
        }

        //■GUIの登録
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        //■サウンド
        MTSounds.init();
    }
}
