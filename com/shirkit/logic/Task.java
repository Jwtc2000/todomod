package com.shirkit.logic;

import java.util.ArrayList;
import java.util.List;

public class Task {

	private String name;
	private int prirority;
	private boolean completed;
	private Object reference;
	private List<Task> subtasks;
	private List<TaskListener> listeners;

	public Task() {
		this.name = "";
		this.prirority = 0;
		this.completed = false;
		this.reference = null;
		this.subtasks = new ArrayList<>();
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

	public Object getReference() {
		return reference;
	}

	public void setReference(Object reference) {
		this.reference = reference;
		updateListener();
	}

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
