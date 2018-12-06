package yousui115.mt.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

/**
 * このText関連もいつかリファクタリングしよう。そう、いつか。
 */
public class TextReader
{
    public static void readText(File rootFolder) throws IOException
    {
        Gson gson = new Gson();

        //■テスト環境
        if (rootFolder.isDirectory() == true)
        {
            //■
            File textFolder = new File(rootFolder.getPath() + "\\assets\\yousui115.mt\\text");

            //■ファイル一覧取得
            String[] jsons = textFolder.list();

            for (String json : jsons)
            {
                try(FileInputStream in = new FileInputStream(textFolder.getPath() + "\\" + json);)
                {
                    TextNegotiate nego = readJson(gson, in);

                    TextMgr.setText(json.replace(".json", ""), nego);
                }
                catch(IOException e)
                {
                    throw e;
                }
            }
        }
        //■本番環境
        else
        {
            //この記述でjar内のtextフォルダ内だけ見てくれる訳じゃなかった。
            URL url = TextReader.class.getResource("/assets/yousui115.mt/text/");

            //DEBUG
//            System.out.println("URL = " + url.getPath());
//            System.out.println("protcol = " + url.getProtocol());

            if ("jar".equals(url.getProtocol()))
            {
                JarURLConnection jarUrlConnection = (JarURLConnection)url.openConnection();

                try(JarFile jarFile = jarUrlConnection.getJarFile();)
                {
                    Enumeration<JarEntry> e = jarFile.entries();

                    while (e.hasMoreElements())
                    {
                        JarEntry entry = e.nextElement();

                        Pattern targetJson = Pattern.compile(".*assets/yousui115.mt/text/(.*\\.json)$");
                        Matcher m = targetJson.matcher(entry.getName());
//                        if (entry.getName().contains("assets/yousui115.mt/text/") &&
                        if (m.find() && entry.isDirectory() == false)
                        {
                            //DEBUG
//                            System.out.println(entry.getName());

                            InputStream stream = jarFile.getInputStream(entry);

                            TextNegotiate nego = readJson(gson, stream);

                            //DEBUG
//                            System.out.println("nego.intro_1 = " + nego.intro_1);

                            //■保管
                            //TODO:純粋に汚い
                            String json = entry.getName().replaceFirst("assets/yousui115.mt/text/", "").replace(".json", "");
                            TextMgr.setText(json, nego);

                            //DEBUG
                            System.out.println("json = " + json);
                        }
                    }
                }
                catch (Exception e)
                {
                    throw e;
                }
            }
            else
            {
                //こっちに入るのは、そもそも想定外なので、例外なげろー
                throw new IOException();
            }
        }
    }


    /**
     * ■
     * @param gsonIn
     * @param isIn
     * @return
     * @throws IOException
     */
    public static TextNegotiate readJson(Gson gsonIn, InputStream isIn) throws IOException
    {
        TextNegotiate nego = new TextNegotiate();

        try (InputStreamReader isr = new InputStreamReader(isIn, "UTF-8");
             JsonReader reader = new JsonReader(isr);)
        {
            //■前処理
            reader.beginArray();

            while (reader.hasNext())
            {
                //■Json読み込み
                TextNegotiate.TextPaper paper = gsonIn.fromJson(reader, TextNegotiate.TextPaper.class);

                if (paper == null) { continue; }

                //■変換
                paper.conversion();

                //TODO:typeが増える毎に書かないといけないのは面倒だなぁ。
                if ("intro".compareTo(paper.type) == 0)
                {
                    nego.intro.add(paper);
                }
                else if ("giveme".compareTo(paper.type) == 0)
                {
                    nego.giveme.add(paper);
                }
                else if ("question".compareTo(paper.type) == 0)
                {
                    nego.question.add(paper);
                }
                else if ("end".compareTo(paper.type) == 0)
                {
                    nego.end.add(paper);
                }
                else if ("interval".compareTo(paper.type) == 0)
                {
                    nego.interval.add(paper);
                }
                else if ("tamed".compareTo(paper.type) == 0)
                {
                    nego.tamed.add(paper);
                }
            }

            //■後処理
            reader.endArray();
        }
        catch(IOException e)
        {
            throw e;
        }

        return nego;
    }
}
