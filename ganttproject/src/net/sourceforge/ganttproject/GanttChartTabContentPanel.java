/*
GanttProject is an opensource project management tool.
Copyright (C) 2005-2011 GanttProject Team

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 3
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package net.sourceforge.ganttproject;

import biz.ganttproject.core.option.ChangeValueEvent;
import biz.ganttproject.core.option.ChangeValueListener;
import net.sourceforge.ganttproject.action.BaselineDialogAction;
import net.sourceforge.ganttproject.action.CalculateCriticalPathAction;
import net.sourceforge.ganttproject.chart.Chart;
import net.sourceforge.ganttproject.chart.overview.GPToolbar;
import net.sourceforge.ganttproject.chart.overview.ToolbarBuilder;
import net.sourceforge.ganttproject.gui.TaskTreeUIFacade;
import net.sourceforge.ganttproject.gui.UIConfiguration;
import net.sourceforge.ganttproject.gui.UIFacade;
import net.sourceforge.ganttproject.gui.view.GPView;

import javax.swing.*;
import java.awt.*;

class GanttChartTabContentPanel extends ChartTabContentPanel implements GPView {
  private final Container myTaskTree;
  private final JComponent myGanttChart;
  private final TaskTreeUIFacade myTreeFacade;
  private final UIFacade myWorkbenchFacade;
  private final CalculateCriticalPathAction myCriticalPathAction;
  private final BaselineDialogAction myBaselineAction;
  private JComponent myComponent;

  GanttChartTabContentPanel(IGanttProject project, UIFacade workbenchFacade, TaskTreeUIFacade treeFacade,
      JComponent ganttChart, UIConfiguration uiConfiguration) {
    super(project, workbenchFacade, workbenchFacade.getGanttChart());
    myWorkbenchFacade = workbenchFacade;
    myTreeFacade = treeFacade;
    myTaskTree = (Container) treeFacade.getTreeComponent();
    myGanttChart = ganttChart;
    // FIXME KeyStrokes of these 2 actions are not working...
    myCriticalPathAction = new CalculateCriticalPathAction(project.getTaskManager(), uiConfiguration, workbenchFacade);
    myBaselineAction = new BaselineDialogAction(project, workbenchFacade);
    addChartPanel(createSchedulePanel());
  }

  private Component createSchedulePanel() {
    return new ToolbarBuilder()
        .withGapFactory(ToolbarBuilder.Gaps.VDASH)
        .withBackground(myWorkbenchFacade.getGanttChart().getStyle().getSpanningHeaderBackgroundColor())
        .addButton(myCriticalPathAction)
        .addButton(myBaselineAction)
        .build()
        .getToolbar();
  }

  JComponent getComponent() {
    if (myComponent == null) {
      myComponent = createContentComponent();
    }
    return myComponent;
  }

  @Override
  protected Component createButtonPanel() {
//    JToolBar buttonBar = new JToolBar();
//    buttonBar.setFloatable(false);
//    buttonBar.setBorderPainted(false);
//    for (AbstractAction a : myTreeFacade.getTreeActions()) {
//      buttonBar.add(new TestGanttRolloverButton(a));
//    }
//
//    JPanel buttonPanel = new JPanel(new BorderLayout());
//    buttonPanel.add(buttonBar, BorderLayout.WEST);
//    return buttonPanel;
    ToolbarBuilder builder = new ToolbarBuilder().withButtonWidth(24).withDpiOption(myWorkbenchFacade.getDpiOption());
    for (AbstractAction a : myTreeFacade.getTreeActions()) {
      builder.addButton(a);
    }
    final GPToolbar toolbar = builder.build();
    myWorkbenchFacade.getDpiOption().addChangeValueListener(new ChangeValueListener() {
      @Override
      public void changeValue(ChangeValueEvent event) {
        toolbar.updateButtons();
      }
    });
    return toolbar.getToolbar();
  }

  @Override
  protected Component getChartComponent() {
    return myGanttChart;
  }

  @Override
  protected Component getTreeComponent() {
    return myTaskTree;
  }

  // //////////////////////////////////////////////
  // GPView
  @Override
  public void setActive(boolean active) {
    if (active) {
      myTaskTree.requestFocus();
      myTreeFacade.getNewAction().updateAction();
    }
  }

  @Override
  public Chart getChart() {
    return myWorkbenchFacade.getGanttChart();
  }

  @Override
  public Component getViewComponent() {
    return getComponent();
  }
}
