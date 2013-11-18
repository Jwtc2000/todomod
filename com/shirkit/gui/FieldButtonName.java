package com.shirkit.gui;

import codechicken.nei.Button;

import com.shirkit.logic.Task;
import com.shirkit.logic.TaskListener;
import com.shirkit.logic.TaskSelector;

public class FieldButtonName extends Button implements TaskListener {

	private Task task;
	private TaskSelector selector;

	public FieldButtonName(Task task) {
		this.task = task;
		this.task.setListener(this);
		this.label = task.getName();
	}

	@Override
	public boolean onButtonPress(boolean rightclick) {
		if (selector != null)
			selector.selectTask(task);
		return true;
	}

	public void setSelector(TaskSelector selector) {
		this.selector = selector;
	}

	@Override
	public void update(Task task) {
		this.label = task.getName();
		if (task.isCompleted())
			state = 2;
		else
			state = 0;
	}
}