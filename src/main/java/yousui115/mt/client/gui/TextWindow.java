package yousui115.mt.client.gui;

import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.mt.MT;
import yousui115.mt.network.MessageGiveMe;
import yousui115.mt.network.MessageTamed;
import yousui115.mt.network.PacketHandler;
import yousui115.mt.text.TextMgr;
import yousui115.mt.text.TextNegotiate;
import yousui115.mt.text.TextNegotiate.TextPaper;

/**
 * 仕様があいまいみー過ぎからの修正修正また修正で、てんやわんやの善助餅だよ！
 */
@SideOnly(Side.CLIENT)
public class TextWindow
{
    //■画像
    private static final ResourceLocation MESSAGE_GUI_TEX = new ResourceLocation(MT.MOD_ID, "textures/gui/message_boad.png");

    //■親GUI
    protected GuiScreenNegotiate gui;

    //■今喋ってるセリフ
    private List<ITextComponent> listText;

    //■状態
    protected State state;
    public State getState() { return state; }

    //■セリフ集
    protected TextNegotiate nego;

    protected Random rnd;

    protected Style style = new Style();

    /**
     * ■
     */
    public TextWindow(GuiScreenNegotiate guiIn, String entityNameIn)
    {
        gui = guiIn;

        nego = TextMgr.getText(entityNameIn);

        state = new StateOpen();

        rnd = Minecraft.getMinecraft().world.rand;

        //■文字スタイル
        style.setBold(true);
        style.setColor(TextFormatting.WHITE);
    }

    /**
     * ■
     */
    public void update()
    {
        //■アップデート
        state.update();

        //■描画文字列
        String str = state.getText();

        if (str != null && str.isEmpty() == false)
        {
            ITextComponent itextcomponent = new TextComponentString(str).setStyle(style);
            listText = itextcomponent != null ? GuiUtilRenderComponents.splitText(itextcomponent, 500, gui.getFR(), true, true) : null;
        }

        //■状態遷移チェック
        state = state.nextState();
    }


    /**
     * ■
     */
    public void draw(float rateWIn, int mouseX, int mouseY, float partialTicks)
    {
        float fW = state.wndX * rateWIn;
        float fH = state.wndY * rateWIn;

        //==================================
        //テキストウィンドウ画像
        //■前処理
        Minecraft.getMinecraft().getTextureManager().bindTexture(MESSAGE_GUI_TEX);

        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        //1.移動
        GlStateManager.translate(fW, fH, 0f);

        //■描画
        int wndWidth = (int)(350f * rateWIn);
        int wndHeight = (int)(100f * rateWIn);
//        gui.drawModalRectWithCustomSizedTexture2(0, 0, 0, 0, wndWidth, wndHeight, 350, 80, 0);
        gui.drawModalRectWithCustomSizedTexture3(0, 0, wndWidth, wndHeight, 0, 0, 350, 80, 350, 80, 0);

        //■後処理
        GlStateManager.popMatrix();

        //======================================
        //テキスト
        if (listText != null)
        {

            for (int idx = 0; idx < listText.size(); idx++)
            {
                GlStateManager.pushMatrix();

                float www = (state.wndX + 10) * rateWIn;
                float hhh = (state.wndY + 10 + gui.getFR().FONT_HEIGHT * idx) * rateWIn;
                GlStateManager.translate(www, hhh, 0);
                GlStateManager.scale(rateWIn, rateWIn, 1f);

//                gui.getFR().drawString(listText.get(idx).getFormattedText() , (int)fW + 10, (int)fH + 10 + gui.getFR().FONT_HEIGHT * idx, 0xffffff);
                gui.getFR().drawString(listText.get(idx).getFormattedText() , 0, 0, 0xffffff);

                GlStateManager.popMatrix();
            }
        }

        //======================================
        //選択肢（ボタン）
        List<GuiButton> buttons = gui.getButtons();
        if (buttons != null && buttons.isEmpty() == false)
        {
            for (int i = 0; i < buttons.size(); ++i)
            {
                ((GuiChoiceButton)buttons.get(i)).drawButton(gui.mc, mouseX, mouseY, partialTicks, rateWIn);
            }
        }
    }

    /**
     * ■
     */
    public boolean endOfNegotiate()
    {
        return this.state instanceof StateStayText && (this.state.type == TypeText.END || this.state.type == TypeText.TAMED);
    }
    /**
     * ■
     */
    public void clicked(int buttonIdIn)
    {
        state.clicked(buttonIdIn);
    }

    //=====================================================================

    private abstract static class State
    {
        //TODO:なぜここに持たせたのか
        //==================================================
        //static member
        public static float wndX = 65f;

        protected static final float WND_Y_UP = 150f;
        protected static final float WND_Y_DOWN = 250f;
        public static float wndY = 150f;

        //===================================================
        //member
        public int tick = 0;

        //■セリフ
        public TextPaper paper;

        //■
        public TypeText type = TypeText.INTRO;

        //====================================================
        //method
        public abstract void update();
        public abstract State nextState();
        //buttonId(0 - 4) は各種ボタン。-1は画面クリック
        public abstract void clicked(int buttonIdIn);
        public String getText() { return ""; }


    }



    //=========================
    //
    private class StateOpen extends State
    {
        public StateOpen()
        {
            wndY = WND_Y_DOWN;
        }

        @Override
        public void update()
        {
            tick++;

            wndY -= (float)tick * 2f;

            if (wndY < WND_Y_UP)
            {
                wndY = WND_Y_UP;
            }
        }

        @Override
        public void clicked(int buttonIdIn)
        {
            //何もしない
        }

        @Override
        public State nextState()
        {
            return (int)wndY == (int)WND_Y_UP ? new StateDrawText(TypeText.INTRO, null, null) : this;
        }
    }

    //=========================
    //
    private class StateDrawText extends State
    {
        private TextPaper.TextAnswer answer;
        private String drawText = "";

        public StateDrawText(TypeText typeIn, TextPaper paperIn, TextPaper.TextAnswer answerIn)
        {
            type = typeIn;
            paper = paperIn;
            answer = answerIn;

            int num = 0;

            if (answer == null)
            {
                switch(type)
                {
                    case INTRO:
                        num = gui.mc.world.rand.nextInt(nego.intro.size());
                        paper = nego.intro.get(num);
                        break;

                    case GIVEME:
                        num = gui.mc.world.rand.nextInt(nego.giveme.size());
                        paper = nego.giveme.get(num);
                        break;

                    case QUESTION:
                        num = gui.mc.world.rand.nextInt(nego.question.size());
                        paper = nego.question.get(num);
                        break;

    //                case ANSWER:
    //                    answer = answerIn;
    //                    break;

                    case INTERVAL:
                        num = gui.mc.world.rand.nextInt(nego.interval.size());
                        paper = nego.interval.get(num);
                        break;

                    case TAMED:
                        paper = nego.tamed.get(0);
                        break;

                    case END:
                        paper = nego.end.get(0);
                        break;

                    default:
                        break;
                }
            }
            else
            {
                //ANSWER
                gui.hdrM.addFavor(answer.point);
            }

            //TODO
            //■表示する文字列のポインタ取得
            drawText = answer != null ? answer.text : paper.main.getText();
        }

        @Override
        public void update()
        {
            tick += tick < drawText.length() ? 1 : 0;
        }

        @Override
        public void clicked(int buttonIdIn)
        {
            if (buttonIdIn == -1)
            {
                tick = drawText.length();
            }
        }

        @Override
        public State nextState()
        {
            return drawText.length() == tick ? new StateStayText(type, paper, answer) : this;
        }

        //TODO
        @Override
        public String getText()
        {
            if (tick > drawText.length()) { tick = drawText.length(); }
            return drawText.substring(0, tick);
        }
    }

    //=========================
    //
    private class StateStayText extends State
    {
        private static final int DEFAULT = 999;
        private int buttonId = DEFAULT;

        private TextPaper.TextAnswer answer;

        public StateStayText(TypeText typeIn, TextPaper paperIn, TextPaper.TextAnswer answerIn)
        {
            type = typeIn;
            paper = paperIn;
            answer = answerIn;

            if (answer == null)
            {
                switch(type)
                {
//                    case ANSWER:
//                        break;

                    case INTRO:
                    case QUESTION:
                    case GIVEME:
                    case INTERVAL:
                        for (int idx = 0; idx < paper.choice.length; idx++)
                        {
                            ITextComponent itextcomponent = new TextComponentString(paper.choice[idx]).setStyle(style);
                            gui.buttons[idx].displayString = itextcomponent.getFormattedText();
                            gui.buttons[idx].visible = true;
                        }

                        break;

                    default:
                        //TODO:なにすりゃいいだろう。
                        break;
                }
            }
        }

        @Override
        public void update()
        {

        }

        @Override
        public void clicked(int buttonIdIn)
        {
            if (buttonId == DEFAULT)
            {
                buttonId = buttonIdIn;
            }
        }

        @Override
        public State nextState()
        {
            State st = this;

            if (answer == null)
            {
                int favor = 0;
                switch(type)
                {
                    case INTRO:
                    case INTERVAL:
                        if (buttonId == 0)
                        {
                            //TODO:話す
                            st = new StateDrawText(type, paper, paper.answer[buttonId]);
                        }
                        else if (buttonId == 1)
                        {
                            //TODO:ウィンドウを閉じる
                            st = new StateDrawText(TypeText.END, paper, paper.answer[buttonId]);
                        }
                        break;

//                    case ANSWER:
//                        if (buttonId == -1)
//                        {
//                            TypeText tt = rnd.nextBoolean() ? TypeText.GIVEME : TypeText.QUESTION;
//                            st = new StateDrawText(tt, null);
//                        }
//                        break;

                    case QUESTION:
                        if (0 <= buttonId && buttonId <= 3)
                        {
                            st = new StateDrawText(type, paper, paper.answer[buttonId]);
                        }
                        break;

                    case GIVEME:

                        if (0 <= buttonId && buttonId <= 1)
                        {
                            //TODO:もう実装が面倒なので、手持ちアイテムだけ見てる。

                            boolean haveItem = false;

                            //欲しいもの
                            ItemStack stack = paper.main.getStack();
                            //手に持ってるもの
                            ItemStack current = gui.player.inventory.getCurrentItem();

                            if (current.isItemEqual(stack) == true && current.getCount() >= paper.main.num)
                            {
                                //持ってる
                                haveItem = true;
                            }

                            buttonId = buttonId + (haveItem == false ? 2 : 0);

                            if (buttonId == 0)
                            {
                                gui.player.inventory.getCurrentItem().shrink(paper.main.num);

                                PacketHandler.INSTANCE.sendToServer(new MessageGiveMe(paper.main.num));
                            }

                            st = new StateDrawText(type, paper, paper.answer[buttonId]);

                        }

                        break;

                    case TAMED:
                        PacketHandler.INSTANCE.sendToServer(new MessageTamed(gui.target.getEntityId()));
                        break;

                    case END:
                    default:
                        //■何もしない
                        break;
                }
            }
            else
            {
                //ANSWER
                if (answer.end == false && buttonId == -1)
                {
                    switch(type)
                    {
                        case INTRO:
                        case INTERVAL:
                            TypeText type = rnd.nextBoolean() ? TypeText.GIVEME : TypeText.QUESTION;
                            st = new StateDrawText(type, null, null);
                            break;

                        default:
                            if (gui.hdrM.getFavor() >= 250)
                            {
                                st = new StateDrawText(TypeText.TAMED, null, null);
                            }
                            else
                            {
                                st = new StateDrawText(TypeText.INTERVAL, null, null);
                            }
                            break;
                    }
                }
            }


            //■リセット
            buttonId = DEFAULT;

            //■次ステートに移るので、ボタン非表示
            if (st != this)
            {
                for (GuiButton button : gui.buttons)
                {
                    button.visible = false;
                }
            }

            return st;
        }

    }



    public enum TypeText
    {
        INTRO,
        GIVEME,
        QUESTION,
        INTERVAL,
//        ANSWER,
        TAMED,
        END
    }

}
