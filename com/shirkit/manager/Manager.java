package com.shirkit.manager;

import net.minecraft.client.gui.inventory.GuiContainer;

import com.shirkit.entity.TaskHolder;
import com.shirkit.gui.Layout;
import com.shirkit.logic.Logic;

public class Manager {

	private static Layout layout;
	private static TaskHolder holder;
	private static Logic logic;

	public static void newInstances() {
		layout = new Layout();
		holder = new TaskHolder();
		logic = new Logic();
	}

	public static void init(GuiContainer gui) {
		layout.init(gui, holder);
		logic.init(layout, holder);
	}

	public static TaskHolder getHolder() {
		return holder;
	}

	public static Layout getLayout() {
		return layout;
	}

	public static Logic getLogic() {
		return logic;
	}

}
