package com.shirkit.todo;

import java.io.File;
import java.lang.reflect.Field;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;

import com.shirkit.todo.entity.Category;
import com.shirkit.todo.entity.Options;
import com.shirkit.todo.entity.Task;
import com.shirkit.todo.entity.TaskHolder;
import com.shirkit.todo.handler.DrawHandler;
import com.shirkit.todo.manager.Manager;

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
		MinecraftForge.EVENT_BUS.register(this);
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

	private boolean wasLoaded = false;
	private String serverName = "";
	private File dir, server;

	@ForgeSubscribe
	public void onWorldLoad(WorldEvent.Load event) {
		// Hacked the Minecraft class to get the Server name!
		if (event.world.provider.dimensionId == 0 && !wasLoaded) {

			Field[] fields = Minecraft.getMinecraft().getClass().getDeclaredFields();
			for (Field field : fields) {
				if (field.getGenericType().toString().equals(ServerData.class.toString())) {
					boolean b = field.isAccessible();
					field.setAccessible(true);
					ServerData obj = null;
					try {
						obj = (ServerData) field.get(Minecraft.getMinecraft());
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					if (obj == null)
						serverName = MinecraftServer.getServer().getWorldName();
					else
						serverName = obj.serverName;

					if (!b)
						field.setAccessible(false);
				}
			}

			dir = new File(Minecraft.getMinecraft().mcDataDir, "saves" + File.separator + "todomod");
			dir.mkdirs();
			server = new File(dir, serverName);

			Manager.newInstances();
			DrawHandler.init = true;
			wasLoaded = true;

			if (server.exists()) {
				try {
					JAXBContext context = JAXBContext.newInstance(TaskHolder.class, Task.class, Category.class, Category.Any.class);
					Unmarshaller unmarshaller = context.createUnmarshaller();
					TaskHolder th = (TaskHolder) unmarshaller.unmarshal(server);
					Manager.getHolder().setCategories(th.getCategories());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@ForgeSubscribe
	public void onWorldUnload(WorldEvent.Unload event) {
		if (event.world.provider.dimensionId == 0 && wasLoaded) {
			wasLoaded = false;
			DrawHandler.init = true;

			JAXBContext context;
			try {
				context = JAXBContext.newInstance(TaskHolder.class, Task.class, Category.class, Category.Any.class);
				Marshaller marshaller = context.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.marshal(Manager.getHolder(), server);
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
	}

}
