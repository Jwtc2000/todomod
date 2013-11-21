package com.shirkit.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import codechicken.nei.Button;
import codechicken.nei.LayoutManager;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.TextField;
import codechicken.nei.Widget;
import codechicken.nei.ItemPanel.ItemPanelSlot;

import com.shirkit.entity.Options;
import com.shirkit.entity.Task;
import com.shirkit.entity.TaskHolder;
import com.shirkit.gui.components.FieldButtonName;
import com.shirkit.gui.components.FieldCompletedCheckbox;
import com.shirkit.gui.components.FieldIcon;
import com.shirkit.gui.components.FieldMainName;
import com.shirkit.logic.TaskListener;

public class Layout {

	private Button previousPage, nextPage, addTask, back;
	private List<Widget> toDraw;
	private HashMap<Task, List<Widget>> widgetMap;
	private TaskHolder holder;
	private GuiListener listener;
	private boolean getFocus = false;

	public Layout() {
		toDraw = new ArrayList<Widget>();
		widgetMap = new HashMap<Task, List<Widget>>();
	}

	private void sendMessage(GuiMessage toSend) {
		sendMessage(toSend, null);
	}

	private void sendMessage(GuiMessage toSend, Task task) {
		listener.update(toSend, task);
	}

	public void setListener(GuiListener listener) {
		this.listener = listener;
	}

	public void init(GuiContainer gui, TaskHolder holder) {
		this.holder = holder;

		boolean edgeAlign = NEIClientConfig.getBooleanSetting("options.edge-align buttons");
		int offsetx = edgeAlign ? 0 : 6;

		previousPage = new Button("<") {

			@Override
			public boolean onButtonPress(boolean rightclick) {
				sendMessage(GuiMessage.PREVIOUS_PAGE);
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
				sendMessage(GuiMessage.ADD_TASK);
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
				sendMessage(GuiMessage.NEXT_PAGE);
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
		// --------------------
		back = new Button("Back") {

			@Override
			public boolean onButtonPress(boolean rightclick) {
				sendMessage(GuiMessage.BACK);
				return false;
			}
		};
		back.height = addTask.height;
		back.width = back.contentWidth() + 6;
		back.x = addTask.x;
		back.y = addTask.y;
	}

	public void showMainScreen(int currentPage) {
		// Cleaer current screen
		toDraw.clear();

		// alignment check
		boolean edgeAlign = NEIClientConfig.getBooleanSetting("options.edge-align buttons");
		int offsety = edgeAlign ? 0 : 3;

		int count = 0;
		// first we display the non-completed tasks
		for (int i = currentPage * Options.getInstance().getMaxTasksOnScreen(); i < holder.getActiveTasks().size() && count < Options.getInstance().getMaxTasksOnScreen(); i++, count++) {
			Task t = holder.getActiveTasks().get(i);

			FieldCompletedCheckbox checkbox = new FieldCompletedCheckbox(t) {
				@Override
				public boolean onButtonPress(boolean rightclick) {
					sendMessage(GuiMessage.COMPLETE, getTask());
					return true;
				}
			};
			checkbox.y = 40 + offsety + count * 20;
			checkbox.height = 17;
			checkbox.width = checkbox.contentWidth() + 6;
			checkbox.x = previousPage.x;

			FieldButtonName textfield = new FieldButtonName(t, 17, 100) {
				@Override
				public boolean onButtonPress(boolean rightclick) {
					sendMessage(GuiMessage.SELECT, this.getTask());
					return true;
				}
			};
			textfield.x = addTask.x;
			textfield.y = checkbox.y;

			toDraw.add(checkbox);
			toDraw.add(textfield);
		}

		boolean drawCompleted = Options.getInstance().showCompletedTasks();

		// only then we show the completed ones
		if (drawCompleted)
			for (int i = currentPage * Options.getInstance().getMaxTasksOnScreen() - holder.getActiveTasks().size() + count; i < holder.getCompletedTasks().size() && count < Options.getInstance().getMaxTasksOnScreen(); i++, count++) {
				Task t = holder.getCompletedTasks().get(i);

				FieldCompletedCheckbox checkbox = new FieldCompletedCheckbox(t) {
					@Override
					public boolean onButtonPress(boolean rightclick) {
						sendMessage(GuiMessage.COMPLETE, getTask());
						return true;
					}
				};
				checkbox.y = 40 + offsety + count * 20;
				checkbox.height = 17;
				checkbox.width = checkbox.contentWidth() + 6;
				checkbox.x = previousPage.x;

				FieldButtonName textfield = new FieldButtonName(t, 17, 100) {
					@Override
					public boolean onButtonPress(boolean rightclick) {
						sendMessage(GuiMessage.SELECT, this.getTask());
						return true;
					}
				};
				textfield.x = addTask.x;
				textfield.y = checkbox.y;

				toDraw.add(checkbox);
				toDraw.add(textfield);
			}

		// Disable previous/next page buttons
		if ((holder.getActiveTasks().size() + (drawCompleted ? holder.getCompletedTasks().size() : 0)) > currentPage * Options.getInstance().getMaxTasksOnScreen() + Options.getInstance().getMaxTasksOnScreen())
			nextPage.state = 0;
		else
			nextPage.state = 2;

		if (currentPage > 0)
			previousPage.state = 0;
		else
			previousPage.state = 2;

		// Add basic buttons
		toDraw.add(previousPage);
		toDraw.add(nextPage);
		toDraw.add(addTask);
	}

	public void showTask(final Task task) {

		toDraw.clear();

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
							task.setName("Make " + (stack2.getDisplayName().substring(0, 1).matches("[aeiouAEIOU]") ? "an" : "a") + " " + stack2.getDisplayName());

						changing = false;
					}
				}
			}
		};
		icon.x = mainName.x + mainName.width + 4;
		icon.y = mainName.y + 2;
		icon.width = 18;
		icon.height = 18;

		FieldCompletedCheckbox checkbox = new FieldCompletedCheckbox(task) {
			@Override
			public boolean onButtonPress(boolean rightclick) {
				sendMessage(GuiMessage.COMPLETE, getTask());
				return false;
			}
		};
		checkbox.width = checkbox.contentWidth() + 6;
		checkbox.height = 11;
		checkbox.y = mainName.y - 1;
		checkbox.x = previousPage.x;

		Button delete = new Button("x") {
			@Override
			public boolean onButtonPress(boolean rightclick) {
				sendMessage(GuiMessage.DELETE, task);
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

		toDraw.add(mainName);
		toDraw.add(checkbox);
		toDraw.add(icon);
		toDraw.add(delete);

		int n = 0;
		for (final Task sub : task.getSubtasks()) {
			if (!sub.isCompleted() || Options.getInstance().showCompletedTasks()) {
				FieldMainName subName = new FieldMainName(sub);
				subName.x = mainName.x;
				subName.y = mainName.y + (n + 2) * mainName.height + n;
				subName.width = mainName.width;
				subName.height = mainName.height - 2;
				if (getFocus)
					subName.setFocus(true);

				FieldCompletedCheckbox subCheckbox = new FieldCompletedCheckbox(sub) {
					@Override
					public boolean onButtonPress(boolean rightclick) {
						sendMessage(GuiMessage.COMPLETE, getTask());
						return true;
					}
				};
				subCheckbox.width = checkbox.width;
				subCheckbox.height = checkbox.height - 1;
				subCheckbox.y = subName.y - 1;
				subCheckbox.x = subName.x - 1 - subCheckbox.width;

				Button subDelete = new Button("x") {

					@Override
					public boolean onButtonPress(boolean rightclick) {
						sendMessage(GuiMessage.DELETE, sub);
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
									sub.setName("Make " + (stack2.getDisplayName().substring(0, 1).matches("[aeiouAEIOU]") ? "an" : "a") + " " + stack2.getDisplayName());

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

				toDraw.add(subDelete);
				toDraw.add(subCheckbox);
				toDraw.add(subName);
				toDraw.add(subicon);
				n++;
			}
		}

		if (!task.isCompleted()) {
			TextField newSub = new TextField("") {
				private boolean changed = false;

				@Override
				public void onTextChange(String oldText) {
					if (!changed) {
						getFocus = true;
						changed = true;
						sendMessage(GuiMessage.ADD_TASK);
					}
				}

				@Override
				public List<String> handleTooltip(int mx, int my, List<String> tooltip) {
					if (!contains(mx, my))
						return tooltip;

					tooltip.add("Right click to enable");
					return tooltip;
				}

				@Override
				public void setFocus(boolean focus) {
				}

			};
			newSub.x = mainName.x;
			newSub.y = mainName.y + (n + 2) * mainName.height + n;
			newSub.width = mainName.width;
			newSub.height = mainName.height;
			toDraw.add(newSub);
		}

		getFocus = false;

		toDraw.add(back);

	}

	public List<Widget> getToDraw() {
		return toDraw;
	}
}
