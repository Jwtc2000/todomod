package com.shirkit.entity;

import java.io.File;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class Options { 

	private static Options instance;

	public static Options getInstance() {
		return instance;
	}

	public static Options load(FMLPreInitializationEvent event) {
		instance = new Options();
		instance.config = new Configuration(new File(event.getModConfigurationDirectory(), "Todo mod.cfg"));
		instance.config.load();

		instance.config.addCustomCategoryComment("UI", "UI definitions");
		instance.UI_show_completed_tasks = instance.config.get("UI", "showCompletedTasks", false, "If tasks that are already completed should be shown or not");
		instance.UI_max_tasks_on_screen = instance.config.get("UI", "maximumTasksOnScreen", 8, "How many tasks should be displayed on the main screen");
		
		instance.config.save();

		return instance;
	}
	
	public boolean showCompletedTasks() {
		return UI_show_completed_tasks.getBoolean(true);
	}
	
	public int getMaxTasksOnScreen() {
		return UI_show_completed_tasks.getInt(8);
	}

	private Configuration config;
	private Property UI_show_completed_tasks;
	private Property UI_max_tasks_on_screen;

}
