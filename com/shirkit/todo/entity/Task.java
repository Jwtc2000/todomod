package com.shirkit.todo.entity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.shirkit.todo.logic.TaskListener;

import net.minecraft.item.ItemStack;

public class Task {
	
	private String name;
	private int prirority;
	private boolean completed;
	private ItemStack reference;
	private List<Task> subtasks;
	private List<TaskListener> listeners;
	@XmlElement
	private int itemID, itemDamage;

	public Task() {
		this.name = "";
		this.prirority = 0;
		this.completed = false;
		this.reference = null;
		this.subtasks = new ArrayList<Task>();
		this.listeners = new ArrayList<TaskListener>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		updateListener();
	}

	public int getPrirority() {
		return prirority;
	}

	public void setPrirority(int prirority) {
		this.prirority = prirority;
		updateListener();
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
		updateListener();
	}

	@XmlTransient
	public ItemStack getReference() {
		return reference;
	}

	public void setReference(ItemStack reference) {
		this.reference = reference;
		this.itemID = reference.itemID;
		this.itemDamage = reference.getItemDamage();
		updateListener();
	}

	@XmlElement
	public List<Task> getSubtasks() {
		return subtasks;
	}

	public void setListener(TaskListener listener) {
		this.listeners.add(listener);
	}

	public void updateListener() {
		for (TaskListener listener : listeners)
			listener.update(this);
	}
	
	int getItemID() {
		return itemID;
	}
	
	int getItemDamage() {
		return itemDamage;
	}

	// TODO need task it to synch with server?
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		return false;
	}

}
