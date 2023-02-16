package com.axonivy.demo.workflow.stats;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ch.ivyteam.ivy.elasticsearch.client.agg.AggOperator;
import ch.ivyteam.ivy.elasticsearch.client.agg.AggregationQuery;
import ch.ivyteam.ivy.elasticsearch.client.agg.AggregationResult;
import ch.ivyteam.ivy.elasticsearch.client.agg.Bucket;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.workflow.stats.WorkflowStats;

@Path("stats2")
public class Stats {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Result get() {
	var query = AggregationQuery.create().agg(AggOperator.stringBuckets("state")).toAggregationQuery();
    return new Result(WorkflowStats.current().task().aggregate(query));
  }

  public static final class Result {

    public final List<String> states;
    public final List<Long> counts;

    public Result(AggregationResult result) {
      var buckets = result.aggs().get("state");
      if (buckets == null) {
    	  this.states = List.of();
    	  this.counts = List.of();
    	  return;
      }
      this.states = buckets.stream().map(Bucket.class::cast).map(b -> b.key()).toList();
      this.counts = buckets.stream().map(Bucket.class::cast).map(b -> b.count()).toList();
    }
  }
}
