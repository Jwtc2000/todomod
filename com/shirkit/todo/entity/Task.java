package com.shirkit.todo.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import com.shirkit.todo.logic.TaskListener;

import net.minecraft.item.ItemStack;

@XmlSeeAlso({ Category.class, Category.Any.class })
public class Task {

	private String name;
	private int prirority;
	private boolean completed;
	private ItemStack reference;
	private List<TaskListener> listeners;
	private int itemID, itemDamage;
	@XmlElement
	protected List<Task> subtasks;
	private List<Task> immutable;

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

	public void addTask(Task toAdd) {
		subtasks.add(toAdd);
	}

	public boolean removeTask(Task toRemove) {
		return subtasks.remove(toRemove);
	}

	/**
	 * Do not try to edit this collection, as it's immutable.
	 * 
	 * @return
	 */
	public List<Task> listSubtasks() {
		if (immutable == null)
			immutable = Collections.unmodifiableList(subtasks);
		return immutable;
	}

	public void setListener(TaskListener listener) {
		this.listeners.add(listener);
	}

	public void updateListener() {
		for (TaskListener listener : listeners)
			listener.update(this);
	}

	@XmlElement
	int getItemID() {
		return itemID;
	}

	@XmlElement
	int getItemDamage() {
		return itemDamage;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		return false;
	}

}
