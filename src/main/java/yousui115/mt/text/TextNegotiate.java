package yousui115.mt.text;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class TextNegotiate
{
    //recipeにしようと思ったけど、ごっちゃになるとイカんのでpaperに変更。
    public List<TextPaper> intro = Lists.newArrayList();

    public List<TextPaper> giveme = Lists.newArrayList();

    public List<TextPaper> question = Lists.newArrayList();

    public List<TextPaper> end = Lists.newArrayList();

    public List<TextPaper> interval = Lists.newArrayList();

    public List<TextPaper> tamed = Lists.newArrayList();


    //===========================================
    //
    public class TextPaper
    {
        public String type;

        public TextMain main;

        public String[] choice;

        public TextAnswer[] answer;

        public void conversion()
        {
//            if (main.item != null && main.item.isEmpty() == false)
//            {
//                NonNullList<ItemStack> stacks = OreDictionary.getOres(main.item);
//                if (stacks != null && stacks.isEmpty() == false)
//                {
//                    main.stack = stacks.get(0);
//                }
//            }
        }

        //===============================================
        //
        public class TextMain
        {
            public String text = "";

            public String item = "";

            public int num = 0;

            /**
             * ■
             */
            public String getText()
            {
                if (this.item == null || this.item.isEmpty()) { return this.text; }

                ItemStack stack = getStack();
                if (stack == ItemStack.EMPTY) { return this.text; }

                this.text = this.text.replace("/i", stack.getDisplayName());
                this.text = this.text.replace("/n", Integer.toString(this.num));
                return this.text;
            }

            /**
             *
             * @return
             */
            public ItemStack getStack()
            {
                List<ItemStack> stacks = OreDictionary.getOres(this.item);
                if (stacks == null || stacks.size() == 0) { return ItemStack.EMPTY; }

                return stacks.get(0);
            }
        }

        //================================================
        //
        public class TextAnswer
        {
            public String text;

            public int point;

            public boolean end = false;
        }
    }


}
