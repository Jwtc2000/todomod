package com.shirkit.logic;

import java.io.File;
import java.lang.reflect.Field;

import javax.swing.text.MaskFormatter;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.shirkit.entity.Task;
import com.shirkit.entity.TaskHolder;
import com.shirkit.handler.DrawHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;

public class WorldListener {

	private boolean wasLoaded = false;
	private String serverName = "";
	private File dir, server;

	@ForgeSubscribe
	public void onWorldLoad(WorldEvent.Load event) {
		// Hacked the Minecraft class to get the Server name!
		try {
			if (event.world.provider.dimensionId == 0 && !wasLoaded) {

				Field[] fields = Minecraft.getMinecraft().getClass().getDeclaredFields();
				for (Field field : fields) {
					if (field.getGenericType().toString().equals(ServerData.class.toString())) {
						boolean b = field.isAccessible();
						field.setAccessible(true);
						ServerData obj = (ServerData) field.get(Minecraft.getMinecraft());
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
				if (!server.exists())
					Manager.newInstance();
				else {
					JAXBContext context = JAXBContext.newInstance(TaskHolder.class, Task.class);
					Unmarshaller unmarshaller = context.createUnmarshaller();
					TaskHolder th = (TaskHolder) unmarshaller.unmarshal(server);
					th.loadTasks();
					Manager.newInstance(th);
				}

				wasLoaded = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@ForgeSubscribe
	public void onWorldUnload(WorldEvent.Unload event) {
		if (event.world.provider.dimensionId == 0 && wasLoaded) {
			wasLoaded = false;
			DrawHandler.init = true;

			TaskHolder th = new TaskHolder();
			th.setActiveTasks(Manager.instance().getActiveTasks());
			th.setCompletedTasks(Manager.instance().getCompletedTasks());

			JAXBContext context;
			try {
				context = JAXBContext.newInstance(TaskHolder.class, Task.class);
				Marshaller marshaller = context.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.marshal(th, server);
			} catch (JAXBException e) {
				e.printStackTrace();
			}

		}
	}

}
