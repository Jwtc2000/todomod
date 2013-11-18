package com.shirkit.gui;

import codechicken.nei.Button;

import com.shirkit.logic.Task;
import com.shirkit.logic.TaskListener;

public class FieldCompletedCheckbox extends Button implements TaskListener {

	private Task task;
	private String tip;

	public FieldCompletedCheckbox(Task task) {
		this.task = task;
		task.setListener(this);
		update(task);
	}

	@Override
	public boolean onButtonPress(boolean rightclick) {
		task.setCompleted(!task.isCompleted());
		return true;
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
}
