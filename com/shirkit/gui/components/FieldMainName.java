package com.shirkit.gui.components;

import static codechicken.core.gui.GuiDraw.drawString;
import static codechicken.core.gui.GuiDraw.getStringWidth;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.input.Keyboard;

import codechicken.nei.TextField;

import com.shirkit.entity.Task;
import com.shirkit.logic.TaskListener;

public class FieldMainName extends TextField implements TaskListener {

	private Task task;
	private TaskListener listener;
	private int move = 0;

	public FieldMainName(Task task) {
		super(task.toString());
		this.task = task;
		this.task.setListener(this);
		setText(task.getName());
	}

	public void setListener(TaskListener listener) {
		this.listener = listener;
	}

	@Override
	public void onTextChange(String oldText) {
		move = 0;
		if (updating) {
			return;
		}
		task.setName(this.text());
		if (listener != null)
			listener.update(task);
	}

	@Override
	public void draw(int mousex, int mousey) {
		drawBox();

		String drawtext = text();

		if (text().length() > getMaxTextLength()) {
			int startOffset = drawtext.length() - getMaxTextLength();
			if (startOffset < 0 || startOffset > drawtext.length())
				startOffset = 0;
			drawtext = drawtext.substring(startOffset + move, getMaxTextLength() + move + startOffset);
			if (move != 0)
				drawtext += "..";
			if (move != -text().length() + getMaxTextLength())
				drawtext = ".." + drawtext;
		}

		if (focused() && (cursorCounter / 6) % 2 == 0)
			drawtext = drawtext + '_';

		int textWidth = getStringWidth(text());
		int textx = centered ? x + (width - textWidth) / 2 : x + 4;
		int texty = y + (height + 1) / 2 - 3;

		drawString(drawtext, textx, texty, getTextColour());

	}

	private int getMaxTextLength() {
		return width / 6 + 1;
	}

	@Override
	public boolean handleKeyPress(int keyID, char keyChar) {
		boolean s = true;
		if (Keyboard.KEY_LEFT == keyID)
			move -= 1;
		else if (Keyboard.KEY_RIGHT == keyID)
			move += 1;
		else {
			s = super.handleKeyPress(keyID, keyChar);
			move = 0;
		}

		if (move > 0)
			move = 0;
		else
			move = Math.max(move, -text().length() + getMaxTextLength());
		return s;
	}

	@Override
	public boolean onMouseWheel(int i, int mousex, int mousey) {
		if (!contains(mousex, mousey))
			return false;
		move += i;
		if (move > 0)
			move = 0;
		else
			move = Math.max(move, -text().length() + getMaxTextLength());
		return true;
	}

	@Override
	public int getTextColour() {
		if (task.isCompleted())
			return focused() ? 0xFFAA0000 : 0xFFFF0000;
		else
			return focused() ? 0xFFE0E0E0 : 0xFF909090;
	}

	@Override
	public List<String> handleTooltip(int mx, int my, List<String> tooltip) {
		if (!contains(mx, my) || focused())
			return tooltip;

		Pattern regex = Pattern.compile("(.{1,20}(?:\\s|$))|(.{0,20})",
				Pattern.DOTALL);
		Matcher regexMatcher = regex.matcher(text());
		while (regexMatcher.find()) {
			if (!regexMatcher.group().isEmpty())
				tooltip.add(regexMatcher.group());
		}

		return tooltip;
	}

	private boolean updating = false;

	@Override
	public void update(Task task) {
		updating = true;
		setText(task.getName());
		updating = false;
	}

}