package yousui115.mt.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import yousui115.mt.MT;

public class ConfigsJson
{
    //■「Hostile Mobs and Girls」が入っているか否か
    public static boolean isInstHMaG = false;

    //■「Grmoire of Gaia」が入っているか否か
    public static boolean isInstGog = false;

    public static final HashMap<String, RidingViewOffset> VIEW_OFFSETS = Maps.newHashMap();

    /**
     * ■コンフィグファイルのよみこみ。
     *   TODO:もっとスマートに書けんものか。
     * @param event
     */
    public static void read(FMLPreInitializationEvent event) throws IOException
    {
//        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
        //■じーさん
        Gson gson = new Gson();

        //■設定用Jsonファイルパス
        File cfgJson = new File(event.getModConfigurationDirectory(), MT.MOD_ID  + "\\ViewOffset.json");

        //■ファイルが存在しない場合は、新規作成する。
        if (cfgJson.exists() == false)
        {
            try
            {
                //■ディレクトリ作成
                File cfgDir = new File(event.getModConfigurationDirectory(), MT.MOD_ID);
                cfgDir.mkdirs();

                //■新規作成
                cfgJson.createNewFile();

                //■ファイル開く
                FileOutputStream out = new FileOutputStream(cfgJson);
                JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));

                //■前処理
                writer.setIndent("  ");
                writer.beginArray();

                //■フォーマット(アラクネch@n)
                RidingViewOffset offsetTmp = new RidingViewOffset("EntityGaiaArachne", new Vec3d(0, 0.7, 0), new Vec3d(0, -0.7, 0));
                List<RidingViewOffset> listOffset = Lists.newArrayList(offsetTmp);

                //■Java -> JSON(String)
                for (RidingViewOffset offset : listOffset)
                {
                    gson.toJson(offset, RidingViewOffset.class, writer);
                }

                //■後処理
                writer.endArray();
                writer.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
                throw e;
            }

        }

        //■ファイルの読み込み
        try(FileInputStream in = new FileInputStream(cfgJson);
            JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));)
        {
            //■ファイル開く
//            FileInputStream in = new FileInputStream(cfgJson);
//            JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

            //■前処理
            reader.beginArray();

            //■読み込み
            while (reader.hasNext())
            {
                RidingViewOffset offset = gson.fromJson(reader, RidingViewOffset.class);
                VIEW_OFFSETS.put(offset.className, offset);
            }

            //■後処理
            reader.endArray();
//            reader.close();
        }
        catch(IOException e)
        {
//            e.printStackTrace();
            throw e;
        }

    }
}
