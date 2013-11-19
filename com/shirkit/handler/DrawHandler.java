package com.shirkit.handler;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import codechicken.nei.Widget;
import codechicken.nei.forge.IContainerDrawHandler;

import com.shirkit.logic.Manager;

public class DrawHandler implements IContainerDrawHandler {

	public static boolean init = true;

	@Override
	public void onPreDraw(GuiContainer gui) {
		if (init) {
			Manager.instance().init(gui);
			init = false;
		}
	}

	@Override
	public void renderObjects(GuiContainer gui, int mousex, int mousey) {
		for (Widget widget : Manager.instance().getWidgets()) {
			widget.draw(mousex, mousey);
		}
	}

	@Override
	public void postRenderObjects(GuiContainer gui, int mousex, int mousey) {
	}

	@Override
	public void renderSlotUnderlay(GuiContainer gui, Slot slot) {
	}

	@Override
	public void renderSlotOverlay(GuiContainer gui, Slot slot) {
	}

}
