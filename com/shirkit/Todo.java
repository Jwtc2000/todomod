package com.shirkit;

import net.minecraftforge.common.MinecraftForge;

import com.shirkit.entity.Options;
import com.shirkit.logic.WorldListener;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "ToDoModShirkit", name = "To-Do Mod", version = "0.1", dependencies = "required-after:NotEnoughItems")
public class Todo {

	public static String VERSION = "@VERSION@";
	
	/** Forge configuration **/
	@Instance
	public static Todo instance;
	
	/** Mod **/

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new WorldListener());
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Options.load(event);

	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}

	@Mod.EventHandler
	public void onServerStarting(FMLServerStartingEvent event) {

	}

}
