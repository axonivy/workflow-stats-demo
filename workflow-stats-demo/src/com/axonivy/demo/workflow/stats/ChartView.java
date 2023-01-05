package com.axonivy.demo.workflow.stats;

import javax.faces.bean.ManagedBean;

import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.workflow.stats.WorkflowStats;
import ch.ivyteam.ivy.workflow.task.TaskBusinessState;

@ManagedBean
public class ChartView {

  public BarChartModel getTasksByState() {
    var model = new BarChartModel();
    var stats = WorkflowStats.of(ISecurityContext.current());
    var result = stats.task().byState();
    var series = new ChartSeries();

    model.setTitle("Task by states");
    series.setLabel("States");

    for (var state : TaskBusinessState.values()) {
      var count = result.get().getOrDefault(state, 0L);
      series.set(state.name(), count);
    }
    model.addSeries(series);
    return model;
  }
}
