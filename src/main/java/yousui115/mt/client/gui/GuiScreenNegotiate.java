package yousui115.mt.client.gui;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.mt.MT;
import yousui115.mt.capability.CapMobHdr;
import yousui115.mt.capability.MobHdr;
import yousui115.mt.network.MessageFav;
import yousui115.mt.network.PacketHandler;
import yousui115.mt.util.MTSounds;

@SideOnly(Side.CLIENT)
public class GuiScreenNegotiate extends GuiScreen
{
    //■イメージ
    private static final ResourceLocation NEGOTIATE_GUI_TEX = new ResourceLocation(MT.MOD_ID, "textures/gui/negotiate_256x256.png");
    private static final ResourceLocation ARACHNE_GUI_TEX = new ResourceLocation(MT.MOD_ID, "textures/gui/arachne.png");
    private static final ResourceLocation MASK_GUI_TEX = new ResourceLocation(MT.MOD_ID, "textures/gui/mask.png");

    //■GUIを開いたプレイヤー
    protected EntityPlayer player;

    //■交渉相手
    protected EntityMob target;
    protected MobHdr hdrM;

//    //■セリフ集
//    private TextNegotiate texts;
//    //■今喋ってるセリフ
//    private List<ITextComponent> drawText;
//    private int drawTextNum;

    //■セリフウィンドウ
    private TextWindow window;

    //■文字送り、点滅　等
    private int updateCount;

    //■画像拡縮比率
    public float rateW;
//    public float rateH;
    public static final float BASE_W = 480f;
//    public static final float BASE_H = 251f;

    //■マスク回転角
    private float rotate;
    private float preRotate;

    //■ボタン
    protected GuiChoiceButton[] buttons = new GuiChoiceButton[4];

    //■フェーズ
    private GuiScreenNegotiate.Phase phase = Phase.Intro;


    /**
     * ■
     */
    public GuiScreenNegotiate(EntityPlayer playerIn, EntityMob targetIn)
    {
        //■交渉Entity
        player = playerIn;
        target = targetIn;

        //■Mob全種が持ってる（はず）
        hdrM = target.getCapability(CapMobHdr.MobCap, null);

        //■クラス名
        String name = target.getClass().getSimpleName();

        //■テキストウィンドウ
        window = new TextWindow(this, name);

        //■ボタン作成
//        for (int idx = 0; idx < buttons.length ; idx++)
//        {
//            buttons[idx] = addButton(new GuiChoiceButton(idx, 75 + 170 * (idx % 2), 215 - 25 * (idx / 2) , ""));
//            buttons[idx].visible = false;
//        }
    }


    /**
     * ■本処理(毎Tick)
     */
    @Override
    public void updateScreen()
    {
        super.updateScreen();
        ++this.updateCount;

        //========================
        //イントロ
        if (phase == Phase.Intro)
        {
            //■マスク回転角
            preRotate = rotate;
            rotate = updateCount < 10 ? (float)updateCount * 6f : 60f;

            if (updateCount == 1)
            {
                //■BGM開始
                startSound(MTSounds.EYE_CATCH);
            }
            else if (updateCount == 60)
            {
                //■60tickで状態遷移
                phase = Phase.Negotiate;
                updateCount = 0;
            }

        }
        //===================================
        //交渉中
        else if (phase == Phase.Negotiate)
        {

            window.update();

            if (updateCount % 770 == 1)
            {
                //■BGM開始（力技ループ）
                startSound(MTSounds.NEGOTIATE);
            }
        }
    }

    private void startSound(SoundEvent soundIn)
    {
        player.playSound(soundIn, 1f, 1f);
    }

    /**
     * ■前処理(width,height変更済み)
     */
    @Override
    public void initGui()
    {
    }

    /**
     * ■後処理
     */
    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();

        this.mc.getSoundHandler().stopSounds();

        PacketHandler.INSTANCE.sendToServer(new MessageFav(hdrM.getFavor(), target.getEntityId()));

    }

    /**
     * ■GUI中のボタンが押された。
     */
    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        //■生きてるボタンのみ。
        if (button.enabled)
        {
            window.clicked(button.id);
        }

    }

    /**
     * ■最低限必要な材料
     */
    @Override
    public void setWorldAndResolution(Minecraft mcIn, int widthIn, int heightIn)
    {
        //MEMO:width, height には、GUIの解像度が入っている。(環境依存)
        //     windowモード + GuiSize:Auto なら、427x240。
        //     fscreenモード + GuiSize:Auto なら、480x270
        //     windowモード + GuiSize:Small なら、854x480
        //     fscreenモード + GuiSize:Small なら、1920x1080

        super.setWorldAndResolution(mcIn, widthIn, heightIn);

        //■拡縮比率
        rateW = this.width / BASE_W;
//        rateH = this.height / BASE_H;

        //■横幅を比率16とし、乗算ベースを算出
//        float rateBase = (float)widthIn / 16f;



        //TODO:GuiButton継承自作クラスで、うまい事作れ
        //■ボタン登録
//        for (int idx = 0; idx < buttons.length ; idx++)
//        {
//            addButton(buttons[idx]);
//        }

        //■ボタン生成＋とう
        for (int idx = 0; idx < buttons.length ; idx++)
        {
            int id = idx;
            int posX = (int)((float)(75 + 170 * (idx % 2)) * rateW);
            int posY = (int)((float)(220 - 25 * (idx / 2)) * rateW);
            int width = (int)(160f * rateW);
            int height = (int)(20f * rateW);

            GuiChoiceButton tmp = new GuiChoiceButton(id, posX, posY, width, height, "");

            if (buttons[idx] != null) { tmp.copy(buttons[idx]); }

            buttons[idx] = addButton(tmp);
        }
    }


    /**
     * ■キー入力があれば呼ばれる。
     */
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (window.endOfNegotiate() == true)
        {
            super.keyTyped(typedChar, keyCode);
        }

    }
    /**
     * ■
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        window.clicked(-1);
    }


    /**
     * ■描画処理
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        //================================
        //前処理
        Tessellator tessellator = Tessellator.getInstance();

        GlStateManager.pushMatrix();

        GlStateManager.scale(rateW, rateW, 1f);

        //メモ：OpenGLの半透明画像の描画について
        //      不透明画像を描画して、それよりも手前に半透明画像を描画すると、透けて見える。
        //      半透明画像を描画して、それよりも奥に不透明画像を描画すると、透けて見えない。
        //      駄菓子菓子、完全に透明な部分は、奥の不透明画像が描画される。

        //================================
        //マスク画像
        //■前処理
        this.mc.getTextureManager().bindTexture(MASK_GUI_TEX);

        GlStateManager.pushMatrix();

        float color = (float)hdrM.getFavor();
        GlStateManager.color(color / 255f, 0.0f, (255f - color) / 255f, 1.0f);

        //3.回転
        float r = preRotate + (rotate - preRotate) * partialTicks;
        GlStateManager.rotate(r, 0f, 0f, 1f);
        //2.移動
        GlStateManager.translate(0f, -500f, 0f);
        //1.拡大
        GlStateManager.scale(2f, 2f, 1f);

        //■描画
        this.drawModalRectWithCustomSizedTexture2(0, 0, 0, 0, 330, 400, 330, 400, 0);

        GlStateManager.popMatrix();


        //==================================
        //立ち絵画像
        //■前処理
        this.mc.getTextureManager().bindTexture(ARACHNE_GUI_TEX);

        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        //3.移動
        GlStateManager.translate(-50f, 80f, 0f);
        //2.回転
        GlStateManager.rotate(-30f, 0f, 0f, 1f);
        //1.縮小
//        GlStateManager.scale(x, y, z);

        //■描画
        this.drawModalRectWithCustomSizedTexture2(0, 0, 0, 0, 330, 400, 330, 400, -1);

        //■後処理
        GlStateManager.popMatrix();


        GlStateManager.popMatrix();

//        GlStateManager.scale(1f, 1f, 1f);

        //===================================
        //セリフ
        if (this.phase == Phase.Negotiate)
        {
            window.draw(rateW, mouseX, mouseY, partialTicks);

        }

        //===================================
        //後処理

        //ボタン描画はTextWindow.classでやる。
//        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * ■Gui.drawModalRectWithCustomSizedTexture に、Z位置調整用Layerを追加
     */
    protected static void drawModalRectWithCustomSizedTexture2(int x, int y, float u, float v, int width, int height, float texW, float texH, double layer)
    {
        float rateW = 1.0F / texW;
        float rateH = 1.0F / texH;
        double dx = (double)x;
        double dy = (double)y;
        double dw = (double)width;
        double dh = (double)height;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(dx     , dy + dh, layer).tex((double)( u       * rateW), (double)((v + dh) * rateH)).endVertex();//左下
        bufferbuilder.pos(dx + dw, dy + dh, layer).tex((double)((u + dw) * rateW), (double)((v + dh) * rateH)).endVertex();//右下
        bufferbuilder.pos(dx + dw, dy     , layer).tex((double)((u + dw) * rateW), (double)( v       * rateH)).endVertex();//右上
        bufferbuilder.pos(dx     , dy     , layer).tex((double)( u       * rateW), (double)( v       * rateH)).endVertex();//左上

        tessellator.draw();
    }


    protected static void drawModalRectWithCustomSizedTexture3(double dx, double dy, double dw, double dh,
                                                                     double uO, double vO, double texW, double texH,
                                                                     float texFullW, float texFullH, double layer)
    {
        double rateW = 1d / texFullW;
        double rateH = 1d / texFullH;

        uO = uO * rateW;
        vO = vO * rateH;
        double uP = texW * rateW;
        double vP = texH * rateH;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(dx     , dy + dh, layer).tex(uO     , vO + vP).endVertex();//左下
        bufferbuilder.pos(dx + dw, dy + dh, layer).tex(uO + uP, vO + vP).endVertex();//右下
        bufferbuilder.pos(dx + dw, dy     , layer).tex(uO + uP, vO     ).endVertex();//右上
        bufferbuilder.pos(dx     , dy     , layer).tex(uO     , vO     ).endVertex();//左上

        tessellator.draw();
    }

    /**
     * ■
     */
    @Override
    public boolean handleComponentClick(ITextComponent component)
    {
        return super.handleComponentClick(component);
    }

    public List<GuiButton> getButtons() { return buttonList; }

    public FontRenderer getFR() { return this.fontRenderer; }

    //==========================================================================================



    /**
     * ■処理状況によって、Stateパターンへ移行する。
     *
     */
    public enum Phase
    {
        Intro,
        Negotiate,
        OK,
        NG
    }


}
