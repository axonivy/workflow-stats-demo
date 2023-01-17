package com.axonivy.demo.workflow.stats;

import java.util.ArrayList;
import java.util.HashMap;

import javax.faces.bean.ManagedBean;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;

import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.workflow.internal.stats.task.TaskByStateResultImpl;
import ch.ivyteam.ivy.workflow.stats.WorkflowStats;
import ch.ivyteam.ivy.workflow.task.TaskBusinessState;

@SuppressWarnings("restriction")
@ManagedBean
public class ChartView {

	public BarChartModel getTasksByState() {
		var model = new BarChartModel();
		var data = new ChartData();

		var stats = WorkflowStats.of(ISecurityContext.current());
		var result = stats.task().byState();

		var barDataSet = new BarChartDataSet();
		barDataSet.setLabel("Task by states");

		var counts = new ArrayList<Number>();
		var labels = new ArrayList<String>();
		for (var entry : result.get().entrySet()) {
			counts.add(entry.getValue());
			labels.add(entry.getKey().toString());
		}
		barDataSet.setData(counts);
		data.setLabels(labels);
		data.addChartDataSet(barDataSet);
		model.setData(data);
		return model;
	}

	public BarChartModel getTasksByMonthAndState() {
		var model = new BarChartModel();
		var data = new ChartData();

		var stats = WorkflowStats.of(ISecurityContext.current());
		var result = stats.task().byMonthAndState();

		var m = new HashMap<TaskBusinessState, Long>();
		m.put(TaskBusinessState.OPEN, 10L);
		m.put(TaskBusinessState.IN_PROGRESS, 4L);
		m.put(TaskBusinessState.DONE, 14L);
		m.put(TaskBusinessState.DESTROYED, 2L);
		m.put(TaskBusinessState.ERROR, 1L);
		m.put(TaskBusinessState.DELAYED, 4L);
		result.get().put("2023-02", new TaskByStateResultImpl(m));

		m = new HashMap<TaskBusinessState, Long>();
		m.put(TaskBusinessState.OPEN, 50L);
		m.put(TaskBusinessState.IN_PROGRESS, 5L);
		m.put(TaskBusinessState.DONE, 44L);
		m.put(TaskBusinessState.DESTROYED, 2L);
		m.put(TaskBusinessState.ERROR, 1L);
		m.put(TaskBusinessState.DELAYED, 4L);
		result.get().put("2023-03", new TaskByStateResultImpl(m));

		var colors = new HashMap<>();
		colors.put(TaskBusinessState.OPEN, "rgb(255, 205, 86)");
		colors.put(TaskBusinessState.IN_PROGRESS, "rgb(54, 162, 235)");
		colors.put(TaskBusinessState.DONE, "rgb(105, 165, 131)");
		colors.put(TaskBusinessState.DESTROYED, "rgb(240, 168, 76)");
		colors.put(TaskBusinessState.ERROR, "rgb(255, 99, 132)");
		colors.put(TaskBusinessState.DELAYED, "rgb(173, 116, 96)");

		for (var states : TaskBusinessState.values()) {
			var barDataSet = new BarChartDataSet();
			barDataSet.setLabel(states.name());

			var counts = new ArrayList<Number>();

			for (var entry : result.get().entrySet()) {
				var map = entry.getValue().get();
				var st = map.getOrDefault(states, 0L);
				counts.add(st);
			}

			barDataSet.setBackgroundColor(colors.get(states));

			barDataSet.setData(counts);
			data.addChartDataSet(barDataSet);
		}

		var labels = new ArrayList<String>();
		for (var entry : result.get().entrySet()) {
			labels.add(entry.getKey().toString());
		}
		data.setLabels(labels);
		model.setData(data);
		return model;
	}
}
