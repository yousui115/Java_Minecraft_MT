package yousui115.mt.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import yousui115.mt.MT;

public class GuiChoiceButton extends GuiButton
{
    protected static final ResourceLocation CHOICE_TEXTURES = new ResourceLocation(MT.MOD_ID, "textures/gui/choice.png");


    public GuiChoiceButton(int buttonId, int x, int y, int width, int height, String buttonText)
    {
        super(buttonId, x, y, width, height, buttonText);

        visible = false;
    }

    @Override
    public void drawButton(Minecraft mcIn, int mouseXIn, int mouseYIn, float partialTicksIn)
    {

    }

    /**
     * ■親を参考にした
     */
    public void drawButton(Minecraft mcIn, int mouseXIn, int mouseYIn, float partialTicksIn, float rateWIn)
    {
        //■描画フラグが立ってる。
        if (this.visible)
        {
//            int x = (int)((float)this.x * rateWIn);
//            int y = (int)((float)this.y * rateWIn);
//
//            int w = (int)((float)this.width * rateWIn);
//            int h = (int)((float)this.height * rateHIn);

            //===================
            //前準備
            //■ボタンの当たり判定中にマウスカーソルがあるか否か
            this.hovered = mouseXIn >= this.x &&
                           mouseYIn >= this.y &&
                           mouseXIn < this.x + this.width &&
                           mouseYIn < this.y + this.height;
            //■
            int i = this.getHoverState(this.hovered);


            //■フォントレンダラ取得
            FontRenderer fontrenderer = mcIn.fontRenderer;
            //■画像設定
            mcIn.getTextureManager().bindTexture(CHOICE_TEXTURES);

//            GlStateManager.pushMatrix();

            //■色設定
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            //■ブレンド関連
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            //TODO:何故２回描画してるんだ・・・
//            this.drawTexturedModalRect(this.x,                  this.y,                     0, 46 + i * 20, this.width / 2, this.height);
//            this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);

            double v = this.hovered == true ? 20d : 0d;
            if (hovered == true)
            {
                int i2 = 0;
            }

//            drawModalRectWithCustomSizedTexture(this.x, this.y, 0, v, this.width, this.height, 160, 40);
            GuiScreenNegotiate.drawModalRectWithCustomSizedTexture3(this.x, this.y, this.width, this.height, 0, v, 160, 20, 160, 40, 0);



//            int ww = (int)(160f * rateWIn);
//            int hh = (int)(20f * rateHIn);
//            drawModalRectWithCustomSizedTexture(x, y, 0, v, ww, hh, 160, 40);

//            GlStateManager.popMatrix();

            //■ドラッグ時の描画処理
            this.mouseDragged(mcIn, mouseXIn, mouseYIn);

            //■文字色
            //14737632 (10) = E0 E0 E0 (16)
            int color = 14737632;

            //■FML
            if (packedFGColour != 0)
            {
                color = packedFGColour;
            }
            //■非アクティブ
            else if (this.enabled == false)
            {
                //10526880 (10) = A0 A0 A0 (16)
                color = 10526880;
            }
            //■マウスほばー
            else if (this.hovered)
            {
                //16777120 (10) = FF FF A0 (16)
                color = 16777120;
            }



            //===================================================
            //
            GlStateManager.pushMatrix();

            GlStateManager.translate(this.x + this.width / 2, this.y + (this.height - 8) / 2, 0f);
            GlStateManager.scale(rateWIn, rateWIn, 1f);

//            this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, color);
            this.drawCenteredString(fontrenderer, this.displayString, 0, 0, color);

            GlStateManager.popMatrix();

        }
    }

    public GuiChoiceButton copy(GuiChoiceButton buttonIn)
    {
        this.displayString = buttonIn.displayString;
        this.enabled = buttonIn.enabled;
        this.visible = buttonIn.visible;
        this.zLevel = buttonIn.zLevel;

        return this;
    }
}
