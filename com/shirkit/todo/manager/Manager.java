package com.shirkit.todo.manager;

import net.minecraft.client.gui.inventory.GuiContainer;

import com.shirkit.todo.entity.Category;
import com.shirkit.todo.entity.TaskHolder;
import com.shirkit.todo.gui.Layout;
import com.shirkit.todo.logic.Logic;

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
		holder.init();
		layout.init(gui, holder);
		logic.init(layout, holder);
	}

	public static void finalizeInstances() {
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
