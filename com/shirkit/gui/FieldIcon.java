package com.shirkit.gui;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.item.ItemStack;
import codechicken.core.gui.GuiDraw;
import codechicken.nei.ItemPanelStack;
import codechicken.nei.Widget;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;

public class FieldIcon extends Widget {

	private ItemPanelStack stack;
	public boolean changing;
	public int x, y;

	public FieldIcon(ItemStack itemstack) {
		super();
		this.stack = new ItemPanelStack(itemstack);
	}

	@Override
	public void draw(int mousex, int mousey) {
		GuiDraw.drawRect(x - 2, y - 2, 20, 20, 0xffA0A0A0);

		if (changing)
			GuiDraw.drawRect(x - 1, y - 1, 18, 18, 0xee555555);
		else
			GuiDraw.drawRect(x - 1, y - 1, 18, 18, 0xee000000);

		if (stack.item != null)
			stack.draw(x, y);
	}

	@Override
	public boolean contains(int posx, int posy) {
		if (posx >= x && posy >= y)
			if (posx <= (x + 15) && posy <= (y + 15))
				return true;
		return false;
	}

	@Override
	public boolean handleClick(int mousex, int mousey, int button) {
		switch (button) {
		case 0:
			if (stack.item != null)
				GuiCraftingRecipe.openRecipeGui("item", stack.item);
			changing = false;
			return true;

		case 1:
			if (stack.item != null)
				GuiUsageRecipe.openRecipeGui("item", stack.item);
			changing = false;
			return true;

		case 2:
			changing = true;
			return true;

		default:
			return super.handleClick(mousex, mousey, button);
		}
	}

	@Override
	public boolean handleKeyPress(int keyID, char keyChar) {
		if (changing) {
			if (keyID == Keyboard.KEY_ESCAPE) {
				changing = false;
				return true;
			}
		}
		return super.handleKeyPress(keyID, keyChar);
	}

	@Override
	public List<String> handleTooltip(int mx, int my, List<String> tooltip) {
		if (contains(mx, my)) {
			if (stack.item != null) {
				tooltip.add(stack.item.getDisplayName());
				tooltip.add("Middle click to change");
			} else
				tooltip.add("Middle click to select an item");

			if (changing)
				tooltip.add("ESC to cancel");
		}
		return tooltip;
	}
}
