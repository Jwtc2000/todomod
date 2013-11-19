package com.shirkit.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import codechicken.nei.forge.GuiContainerManager;

import com.shirkit.logic.Manager;

@XmlRootElement
public class TaskHolder {

	private List<Task> activeTasks, completedTasks;

	public void loadTasks() {
		if (activeTasks != null)
			for (Task task : activeTasks)
				loadTask(task);
		if (completedTasks != null)
			for (Task task : completedTasks)
				loadTask(task);
	}

	private void loadTask(Task task) {
		ItemStack stack = new ItemStack(Item.itemsList[task.getItemID()]);
		stack.setItemDamage(task.getItemDamage());
		task.setReference(stack);
		for (Task sub : task.getSubtasks())
			loadTask(sub);
	}

	public List<Task> getActiveTasks() {
		return activeTasks;
	}

	public List<Task> getCompletedTasks() {
		return completedTasks;
	}

	public void setActiveTasks(List<Task> activeTasks) {
		this.activeTasks = activeTasks;
	}

	public void setCompletedTasks(List<Task> completedTasks) {
		this.completedTasks = completedTasks;
	}

}
