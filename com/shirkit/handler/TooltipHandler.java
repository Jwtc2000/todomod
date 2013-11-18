package com.shirkit.handler;

import java.util.List;

import com.shirkit.logic.Manager;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import codechicken.nei.Widget;
import codechicken.nei.forge.IContainerTooltipHandler;

public class TooltipHandler implements IContainerTooltipHandler {

	@Override
	public List<String> handleTooltipFirst(GuiContainer gui, int mousex, int mousey, List<String> currenttip) {
		if (gui.manager.shouldShowTooltip())
			for (Widget widget : Manager.instance().getWidgets())
				currenttip = widget.handleTooltip(mousex, mousey, currenttip);
		return currenttip;
	}

	@Override
	public List<String> handleItemTooltip(GuiContainer gui, ItemStack itemstack, List<String> currenttip) {
		return currenttip;
	}
}
