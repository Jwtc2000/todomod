package com.shirkit.handler;

import java.util.List;

import com.shirkit.manager.Manager;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import codechicken.nei.Widget;
import codechicken.nei.forge.IContainerTooltipHandler;

public class TooltipHandler implements IContainerTooltipHandler {

	@Override
	public List<String> handleTooltipFirst(GuiContainer gui, int mousex, int mousey, List<String> currenttip) {
		// if (gui.manager.shouldShowTooltip()) Can't use this and obfuscate the code
		for (Widget widget : Manager.getLayout().getToDraw())
			currenttip = widget.handleTooltip(mousex, mousey, currenttip);
		return currenttip;
	}

	@Override
	public List<String> handleItemTooltip(GuiContainer gui, ItemStack itemstack, List<String> currenttip) {
		return currenttip;
	}
}
