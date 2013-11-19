package com.shirkit.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import codechicken.nei.Button;
import codechicken.nei.ItemPanel.ItemPanelSlot;
import codechicken.nei.LayoutManager;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.Widget;

import com.shirkit.entity.Options;
import com.shirkit.gui.FieldButtonName;
import com.shirkit.gui.FieldCompletedCheckbox;
import com.shirkit.gui.FieldIcon;
import com.shirkit.gui.FieldMainName;

public class Manager implements TaskListener, TaskSelector {

	/* Parameters | Config */

	private static final int MAX_TASKS_ON_SCREEN = 8;

	/* Static | Loading */
	private static Manager instance;

	public static Manager instance() {
		if (instance == null) {
			instance = new Manager();
		}
		return instance;
	}

	/* Manager */

	private List<Task> activeTasks, completedTasks;
	private Stack<Task> stack;
	private HashMap<Task, List<Widget>> widgetMap;
	private List<Widget> drawWidgets;
	private Sorter sorter;
	private Button addTask, nextPage, previousPage;
	private int currentPage = 0;
	// ------------------- Temporary variables ---------------------------
	/**
	 * This variable is only valid right after a Task creation, and no place
	 * else it is guaranteed that it will hold a correct value.
	 */
	private FieldMainName tempName;
	/**
	 * On every selectTask, this variable's reference is cleared, so it must be
	 * set again after a task addition
	 */
	private Task creatingTask;

	private Manager() {
		activeTasks = new ArrayList<Task>();
		completedTasks = new ArrayList<Task>();
		sorter = new Sorter();
		widgetMap = new HashMap<Task, List<Widget>>();
		drawWidgets = new ArrayList<Widget>();
		stack = new Stack<Task>();
	}

	public Collection<Widget> getWidgets() {
		return drawWidgets;
	}

	public void init(GuiContainer gui) {
		boolean edgeAlign = NEIClientConfig.getBooleanSetting("options.edge-align buttons");
		int offsetx = edgeAlign ? 0 : 6;

		previousPage = new Button("<") {

			@Override
			public boolean onButtonPress(boolean rightclick) {
				previousPage();
				return true;
			}

			@Override
			public String getButtonTip() {
				return "Previous page";
			}
		};
		previousPage.height = 20;
		previousPage.width = previousPage.contentWidth() + 6;
		previousPage.x = 6;
		previousPage.y = (int) ((gui.height / 10) * 8.5);
		// -------------------------------
		addTask = new Button("Add task") {

			@Override
			public boolean onButtonPress(boolean rightclick) {
				Task t = new Task();
				//t.setName("New task " + (activeTasks.size() + completedTasks.size()));
				addTask(t);
				selectTask(t);
				tempName.setFocus(true);
				tempName.setText("");
				creatingTask = t;
				return true;
			}
		};
		addTask.height = 20;
		addTask.width = addTask.contentWidth() + 6;
		addTask.x = previousPage.x + previousPage.width + offsetx;
		addTask.y = previousPage.y;
		// -------------------------------
		nextPage = new Button(">") {

			@Override
			public boolean onButtonPress(boolean rightclick) {
				nextPage();
				return true;
			}

			@Override
			public String getButtonTip() {
				return "Next page";
			}
		};
		nextPage.height = 20;
		nextPage.width = nextPage.contentWidth() + 6;
		nextPage.x = addTask.x + addTask.width + offsetx;
		nextPage.y = addTask.y;

		updateMainPage();
	}

	@Override
	public void update(Task task) {
		if (task.isCompleted()) {
			activeTasks.remove(task);
			if (!completedTasks.contains(task))
				completedTasks.add(task);
		} else {
			completedTasks.remove(task);
			if (!activeTasks.contains(task))
				activeTasks.add(task);
		}

		if (activeTasks.contains(task))
			Collections.sort(activeTasks, sorter);
		if (completedTasks.contains(task))
			Collections.sort(completedTasks, sorter);
		if (stack.empty())
			updateMainPage();
	}

	private void nextPage() {
		currentPage++;

		// Discover the fraction of the number of pages to be displayed
		float math = (float) activeTasks.size() / (float) MAX_TASKS_ON_SCREEN + (float) completedTasks.size() / (float) MAX_TASKS_ON_SCREEN;
		// Only add one extra page if we have any fraction
		int maxPages = ((int) math) == math ? (int) math : (int) math + 1;

		if (currentPage < maxPages)
			updateMainPage();
		else
			currentPage--;
	}

	private void previousPage() {
		if (currentPage > 0) {
			currentPage--;
			updateMainPage();
		}
	}

	private void updateMainPage() {
		// Add the basic buttons
		drawWidgets.clear();

		// alignment check
		boolean edgeAlign = NEIClientConfig.getBooleanSetting("options.edge-align buttons");
		int offsety = edgeAlign ? 0 : 3;

		int count = 0;
		// first we display the non-completed tasks
		for (int i = currentPage * MAX_TASKS_ON_SCREEN; i < activeTasks.size() && count < MAX_TASKS_ON_SCREEN; i++, count++) {
			Task t = activeTasks.get(i);
			List<Widget> list = widgetMap.get(t);
			for (Widget w : list)
				w.y = 40 + offsety + count * 20;
			drawWidgets.addAll(list);
		}

		boolean drawCompleted = Options.getInstance().UI_show_completed_tasks.getBoolean(true);

		// only then we show the completed ones
		if (drawCompleted)
			for (int i = currentPage * MAX_TASKS_ON_SCREEN - activeTasks.size() + count; i < completedTasks.size() && count < MAX_TASKS_ON_SCREEN; i++, count++) {
				Task t = completedTasks.get(i);
				List<Widget> list = widgetMap.get(t);
				for (Widget w : list)
					w.y = 40 + offsety + count * 20;
				drawWidgets.addAll(list);
			}

		if ((activeTasks.size() + (drawCompleted ? completedTasks.size() : 0)) > currentPage * MAX_TASKS_ON_SCREEN + MAX_TASKS_ON_SCREEN)
			drawWidgets.add(nextPage);
		if (currentPage > 0)
			drawWidgets.add(previousPage);
		drawWidgets.add(addTask);
	}

	private boolean addTask(final Task task) {
		if (activeTasks.contains(task))
			return false;

		FieldCompletedCheckbox checkbox = new FieldCompletedCheckbox(task);
		checkbox.y = 40 + activeTasks.size() * 20;
		checkbox.height = 17;
		checkbox.width = checkbox.contentWidth() + 6;
		checkbox.x = previousPage.x;

		FieldButtonName textfield = new FieldButtonName(task);
		textfield.x = addTask.x;
		textfield.y = checkbox.y;
		textfield.height = 17;
		textfield.width = 100;
		textfield.setSelector(instance);

		List<Widget> list = new ArrayList<Widget>(2);
		list.add(checkbox);
		list.add(textfield);
		drawWidgets.addAll(list);
		widgetMap.put(task, list);

		activeTasks.add(task);

		updateMainPage();

		return true;
	}

	@Override
	public void selectTask(Task task) {
		if (task == null) {
			stack.pop();
			if (stack.empty())
				updateMainPage();
			else
				displayTask(stack.peek());
		} else {
			stack.push(task);
			displayTask(task);
		}
		creatingTask = null;
	}

	private void deleteTask(Task task) {
		if (stack != null) {
			activeTasks.remove(task);
			completedTasks.remove(task);
			if (stack.peek().equals(task))
				selectTask(null);
		}
	}

	private void displayTask(final Task task) {
		displayTask(task, false);
	}

	private void displayTask(final Task task, boolean focusLastSubTask) {
		drawWidgets.clear();

		Button back = new Button("Back") {
			@Override
			public boolean onButtonPress(boolean rightclick) {
				if (task.equals(creatingTask)) {
					if (tempName.text().trim().isEmpty() && task.getReference() == null && task.getSubtasks().isEmpty()) {
						deleteTask(task);
					} else
						selectTask(null);
				} else
					selectTask(null);
				return true;
			}
		};
		back.height = addTask.height;
		back.width = back.contentWidth() + 6;
		back.x = addTask.x;
		back.y = addTask.y;

		FieldMainName mainName = new FieldMainName(task);
		mainName.x = back.x;
		mainName.y = 44;
		mainName.width = 100;
		mainName.height = back.height;

		FieldIcon icon = new FieldIcon(task) {
			@Override
			public void onGuiClick(int mousex, int mousey) {
				if (LayoutManager.itemPanel.contains(mousex, mousey) && changing) {
					ItemPanelSlot item = LayoutManager.itemPanel.getSlotMouseOver(mousex, mousey);
					if (item != null) {
						ItemStack stack2 = item.getItemStack();
						task.setReference(stack2);
						
						if (task.getName().isEmpty())
							task.setName("Make " + (stack2.getDisplayName().substring(0, 1).matches("[aeiouAEIOU]"	) ? "an" : "a") + " " + stack2.getDisplayName());
						
						changing = false;
					}
				}
			}
		};
		icon.x = mainName.x + mainName.width + 4;
		icon.y = mainName.y + 2;
		icon.width = 18;
		icon.height = 18;

		FieldCompletedCheckbox checkbox = new FieldCompletedCheckbox(task);
		checkbox.width = checkbox.contentWidth() + 6;
		checkbox.height = 11;
		checkbox.y = mainName.y - 1;
		checkbox.x = previousPage.x;

		Button delete = new Button("x") {
			@Override
			public boolean onButtonPress(boolean rightclick) {
				deleteTask(task);
				return true;
			}

			@Override
			public String getButtonTip() {
				return "Delete task";
			}
		};
		delete.x = checkbox.x;
		delete.y = checkbox.y + checkbox.height;
		delete.height = checkbox.height;
		delete.width = delete.contentWidth() + 6;

		int n = 0;
		boolean drawCompleted = Options.getInstance().UI_show_completed_tasks.getBoolean(true);
		for (final Task sub : task.getSubtasks()) {
			if (!sub.isCompleted() || drawCompleted) {
				FieldMainName subName = new FieldMainName(sub) {
					@Override
					public boolean handleClick(int mousex, int mousey, int button) {
						boolean s = super.handleClick(mousex, mousey, button);
						
						if (button == 2) {
							selectTask(sub);
							s = true;
						}
						
						return s;
					}
				};
				subName.x = mainName.x;
				subName.y = mainName.y + (n + 2) * mainName.height + n;
				subName.width = mainName.width;
				subName.height = mainName.height - 2;

				if (focusLastSubTask)
					subName.setFocus(true);

				FieldCompletedCheckbox subCheckbox = new FieldCompletedCheckbox(sub);
				subCheckbox.width = checkbox.width;
				subCheckbox.height = checkbox.height - 1;
				subCheckbox.y = subName.y - 1;
				subCheckbox.x = subName.x - 1 - subCheckbox.width;

				Button subDelete = new Button("x") {

					@Override
					public boolean onButtonPress(boolean rightclick) {
						task.getSubtasks().remove(sub);
						displayTask(task);
						return true;
					}

					@Override
					public String getButtonTip() {
						return "Delete sub-task";
					}
				};
				subDelete.x = subCheckbox.x;
				subDelete.y = subCheckbox.y + subCheckbox.height;
				subDelete.height = subCheckbox.height;
				subDelete.width = subCheckbox.width;
				
				FieldIcon subicon = new FieldIcon(sub) {
					@Override
					public void onGuiClick(int mousex, int mousey) {
						if (LayoutManager.itemPanel.contains(mousex, mousey) && changing) {
							ItemPanelSlot item = LayoutManager.itemPanel.getSlotMouseOver(mousex, mousey);
							if (item != null) {
								ItemStack stack2 = item.getItemStack();
								sub.setReference(stack2);
								
								if (sub.getName().isEmpty())
									sub.setName("Make " + (stack2.getDisplayName().substring(0, 1).matches("[aeiouAEIOU]"	) ? "an" : "a") + " " + stack2.getDisplayName());
								
								changing = false;
							}
						}
					}
				};
				subicon.x = icon.x;
				subicon.y = subName.y + 2;
				subicon.height = icon.height - 2;
				subicon.width = icon.width - 2;
				subicon.offset = 1;

				drawWidgets.add(subDelete);
				drawWidgets.add(subCheckbox);
				drawWidgets.add(subName);
				drawWidgets.add(subicon);
				n++;
			}
		}

		Task nt1 = new Task();
		FieldMainName newSub = new FieldMainName(nt1);
		newSub.x = mainName.x;
		newSub.y = mainName.y + (n + 2) * mainName.height + n;
		newSub.width = mainName.width;
		newSub.height = mainName.height;
		newSub.setListener(new SubTaskListener(task, nt1));

		drawWidgets.add(newSub);
		drawWidgets.add(delete);
		drawWidgets.add(mainName);
		drawWidgets.add(checkbox);
		drawWidgets.add(back);
		drawWidgets.add(icon);

		tempName = mainName;
	}

	private class SubTaskListener implements TaskListener {

		private Task parent;
		private Task child;
		private boolean added = false;

		public SubTaskListener(Task parent, Task child) {
			this.parent = parent;
			this.child = child;
		}

		@Override
		public void update(Task task) {
			if (!added) {
				parent.getSubtasks().add(child);
				added = true;
				displayTask(parent, true);
			}
		}

	}

	private class Sorter implements Comparator<Task> {
		@Override
		public int compare(Task o1, Task o2) {
			return o1.getPrirority() - o2.getPrirority();
		}
	}
}
