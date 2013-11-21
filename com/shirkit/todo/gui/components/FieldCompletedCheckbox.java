package com.shirkit.todo.gui.components;

import codechicken.nei.Button;

import com.shirkit.todo.entity.Task;
import com.shirkit.todo.logic.TaskListener;

public abstract class FieldCompletedCheckbox extends Button implements TaskListener {

	private Task task;
	private String tip;

	public FieldCompletedCheckbox(Task task) {
		this.task = task;
		task.setListener(this);
		update(task);
	}

	@Override
	public String getButtonTip() {
		return tip;
	}

	@Override
	public void update(Task task) {
		this.label = task.isCompleted() ? "X" : "V";
		this.tip = task.isCompleted() ? "Mark as incompleted" : "Complete task";
	}
	
	public Task getTask() {
		return task;
	}
}
