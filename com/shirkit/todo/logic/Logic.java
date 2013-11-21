package com.shirkit.todo.logic;

import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;

import com.shirkit.todo.entity.Options;
import com.shirkit.todo.entity.Task;
import com.shirkit.todo.entity.TaskHolder;
import com.shirkit.todo.gui.GuiListener;
import com.shirkit.todo.gui.GuiMessage;
import com.shirkit.todo.gui.Layout;

public class Logic implements GuiListener {

	private Layout layout;
	private TaskHolder holder;
	private Stack<Task> stack;
	private int currentPage;
	private Sorter sorter;

	public Logic() {
		stack = new Stack<Task>();
		this.sorter = new Sorter();
	}

	public void init(Layout layout, TaskHolder holder) {
		this.layout = layout;
		this.holder = holder;
		layout.setListener(this);
		layout.showMainScreen(currentPage);
	}

	@Override
	public void update(GuiMessage message, Task about) {
		switch (message) {
		case ADD_TASK:

			Task newOne = new Task();
			if (!stack.isEmpty()) {
				// has a parent
				stack.peek().getSubtasks().add(newOne);
				layout.showTask(stack.peek());
			} else {
				// no parent
				holder.getActiveTasks().add(newOne);
				stack.push(newOne);
				layout.showTask(newOne);
			}

			break;

		case BACK:
			stack.pop();
			if (!stack.isEmpty())
				layout.showTask(stack.peek());
			else
				layout.showMainScreen(currentPage);

			break;

		case COMPLETE:

			if (about.isCompleted()) {
				about.setCompleted(false);
				// only add if it's on the main completed task list
				if (holder.getCompletedTasks().remove(about))
					holder.getActiveTasks().add(about);
			} else {
				about.setCompleted(true);
				if (holder.getActiveTasks().remove(about))
					holder.getCompletedTasks().add(about);
			}

			if (stack.isEmpty())
				layout.showMainScreen(currentPage);

			break;

		case DELETE:
			if (about.equals(stack.peek())) {
				// deleting current task
				stack.pop();
				if (!holder.getActiveTasks().remove(about)) {
					holder.getCompletedTasks().remove(about);
				}
				layout.showMainScreen(currentPage);
			} else {
				// deleting a sub task
				stack.peek().getSubtasks().remove(about);
				layout.showTask(stack.peek());
			}

			break;

		case NEXT_PAGE:

			currentPage++;

			// Discover the fraction of the number of pages to be displayed
			float math = (float) holder.getActiveTasks().size() / (float) Options.getInstance().getMaxTasksOnScreen();

			if (Options.getInstance().showCompletedTasks())
				math += (float) holder.getCompletedTasks().size() / (float) Options.getInstance().getMaxTasksOnScreen();

			// Only add one extra page if we have any fraction
			int maxPages = ((int) math) == math ? (int) math : (int) math + 1;

			if (currentPage < maxPages)
				layout.showMainScreen(currentPage);
			else
				currentPage--;

			break;

		case PREVIOUS_PAGE:

			if (currentPage > 0) {
				currentPage--;
				layout.showMainScreen(currentPage);
			}

			break;

		case SELECT:

			stack.push(about);
			layout.showTask(about);

			break;

		default:
			break;
		}
	}

	private class Sorter implements Comparator<Task> {
		@Override
		public int compare(Task o1, Task o2) {
			return o1.getPrirority() - o2.getPrirority();
		}
	}
}
