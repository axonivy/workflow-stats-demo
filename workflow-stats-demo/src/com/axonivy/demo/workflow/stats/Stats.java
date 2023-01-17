package com.axonivy.demo.workflow.stats;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ch.ivyteam.ivy.workflow.stats.WorkflowStats;
import ch.ivyteam.ivy.workflow.task.TaskBusinessState;

@Path("stats2")
public class Stats {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Result get() {
    return new Result(WorkflowStats.current().task().byState().get());
  }

  public static final class Result {

    public final List<TaskBusinessState> states;
    public final List<Long> counts;

    public Result(Map<TaskBusinessState, Long> states) {
      this.states = states.keySet().stream().toList();
      this.counts = states.values().stream().toList();
    }
  }
}
