package yousui115.mt.text;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

public class TextMgr
{
    //■保管庫 <modid, <entityname, obj> >
    public static Map<String, TextNegotiate> mapText = Maps.newHashMap();

    /**
     * ■
     */
    public static void setText(String entitynameIn, TextNegotiate negoIn)
    {
        if (mapText.containsKey(entitynameIn) == false)
        {
            mapText.put(entitynameIn, negoIn);
        }
    }

    /**
     * ■
     */
    @Nullable
    public static TextNegotiate getText(String entitynameIn)
    {
        if (mapText.containsKey(entitynameIn))
        {
            return mapText.get(entitynameIn);
        }

        return null;
    }
}
