package com.shirkit.gui;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import codechicken.nei.Button;

import com.shirkit.entity.Task;
import com.shirkit.logic.TaskListener;
import com.shirkit.logic.TaskSelector;

public class FieldButtonName extends Button implements TaskListener {

	private Task task;
	private TaskSelector selector;
	private String realLabel;

	public FieldButtonName(Task task) {
		this.task = task;
		this.task.setListener(this);
		this.label = task.getName();
		this.realLabel = this.label;
		update(task);
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
		this.realLabel = task.getName();
		this.label = realLabel;

		int textwidth = contentWidth();
		int maxLength = width / 6 + 1;
		if (label.length() > maxLength)
			label = realLabel.substring(0, maxLength).concat("..");
		if (task.isCompleted())
			state = 2;
		else
			state = 0;
	}

	@Override
	public List<String> handleTooltip(int mx, int my, List<String> tooltip) {
		if (!contains(mx, my))
			return tooltip;

		Pattern regex = Pattern.compile("(.{1,20}(?:\\s|$))|(.{0,20})", Pattern.DOTALL);
		Matcher regexMatcher = regex.matcher(realLabel);
		while (regexMatcher.find()) {
			if (!regexMatcher.group().isEmpty())
				tooltip.add(regexMatcher.group());
		}
		
		return tooltip;
	}
}