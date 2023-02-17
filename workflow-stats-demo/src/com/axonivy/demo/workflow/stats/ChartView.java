package com.axonivy.demo.workflow.stats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;

import ch.ivyteam.ivy.elasticsearch.client.agg.AggOperator;
import ch.ivyteam.ivy.elasticsearch.client.agg.AggregationQuery;
import ch.ivyteam.ivy.elasticsearch.client.agg.Bucket;
import ch.ivyteam.ivy.elasticsearch.client.agg.Operator.DateBucketOperator.Interval;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.workflow.stats.WorkflowStats;
import ch.ivyteam.ivy.workflow.task.TaskBusinessState;

@ManagedBean
public class ChartView {

  private static final Map<TaskBusinessState, String> COLORS = Map.of(
          TaskBusinessState.OPEN, "rgb(255, 205, 86)",
          TaskBusinessState.IN_PROGRESS, "rgb(54, 162, 235)",
          TaskBusinessState.DONE, "rgb(105, 165, 131)",
          TaskBusinessState.DESTROYED, "rgb(240, 168, 76)",
          TaskBusinessState.ERROR, "rgb(255, 99, 132)",
          TaskBusinessState.DELAYED, "rgb(173, 116, 96)");

  public BarChartModel getTasksByState() {
    var stats = WorkflowStats.of(ISecurityContext.current());
    var query = AggregationQuery.create().agg(AggOperator.stringBuckets("state")).toAggregationQuery();
    var result = stats.task().aggregate(query);
    var buckets = result.aggs().get("state");
    var barDataSet = new BarChartDataSet();
    barDataSet.setLabel("Task by states");
    if (buckets != null) {
      var counts = buckets.stream().map(Bucket.class::cast).map(Bucket::count).map(Number.class::cast)
              .toList();
      barDataSet.setData(counts);
    }
    var data = new ChartData();
    if (buckets != null) {
      var labels = buckets.stream().map(Bucket.class::cast).map(Bucket::key).toList();
      data.setLabels(labels);
    }
    data.addChartDataSet(barDataSet);
    var model = new BarChartModel();
    model.setData(data);
    return model;
  }

  public BarChartModel getTasksByMonthAndState() {
    var stats = WorkflowStats.of(ISecurityContext.current());
    var query = AggregationQuery.create()
            .agg(AggOperator.dateBuckets("startedAt", Interval.HOUR, AggOperator.stringBuckets("state")))
            .toAggregationQuery();
    var result = stats.task().aggregate(query);
    var buckets = result.aggs().get("startedAt");
    var data = new ChartData();
    for (var state : TaskBusinessState.values()) {
      var barDataSet = new BarChartDataSet();
      barDataSet.setLabel(state.name());
      barDataSet.setBackgroundColor(COLORS.get(state));
      var counts = new ArrayList<Number>();
      if (buckets != null) {
        for (var bucket : buckets.stream().map(Bucket.class::cast).toList()) {
          var count = bucket.aggs().get("state")
                  .stream()
                  .map(Bucket.class::cast)
                  .filter(b -> b.key().equals(state.name()))
                  .map(Bucket::count)
                  .map(Number.class::cast)
                  .findAny()
                  .orElse(0);
          counts.add(count);
        }
      }
      barDataSet.setData(counts);
      data.addChartDataSet(barDataSet);
    }
    List<String> labels = List.of();
    if (buckets != null) {
      labels = buckets.stream().map(Bucket.class::cast).map(Bucket::key).toList();
    }
    data.setLabels(labels);
    var model = new BarChartModel();
    model.setData(data);
    return model;
  }
}
