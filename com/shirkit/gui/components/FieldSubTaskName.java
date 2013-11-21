package com.shirkit.gui.components;

import org.lwjgl.input.Keyboard;

import com.shirkit.entity.Task;

import codechicken.nei.TextField;

public class FieldSubTaskName extends TextField {

	private Task task;

	public FieldSubTaskName(Task task) {
		super(task.toString());
		this.task = task;
		setText(task.getName());
	}

	@Override
	public void onTextChange(String oldText) {
		task.setName(this.text());
	}

	@Override
	public boolean handleKeyPress(int keyID, char keyChar) {
		boolean result = super.handleKeyPress(keyID, keyChar);

		if (result && keyID == Keyboard.KEY_TAB)
			nextField();

		return result;
	}

	public void nextField() {
	}

}
