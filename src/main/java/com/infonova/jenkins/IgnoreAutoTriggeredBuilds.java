package com.infonova.jenkins;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Cause;
import hudson.model.CauseAction;
import hudson.model.Queue;

import java.util.Collections;
import java.util.List;

/**
 * Created by kazesberger on 21.03.17.
 */
@Extension
public class IgnoreAutoTriggeredBuilds extends Queue.QueueDecisionHandler {

    @Override
    public boolean shouldSchedule(Queue.Task p, List<Action> actions) {
        DisableBuildTriggersConfig config = DisableBuildTriggersConfig.instance();
        if (!config.isSuppressTriggers()) {
            return true;
        }
        List<Cause> causes = getCauses(actions);
        for (Cause c : causes) {
            if (config.getBlackListParsed().contains(c.getClass())) {
                return false;
            }
        }
        return true;
    }

    private List<Cause> getCauses(List<Action> actions) {
        for (Action a : actions) {
            if (a instanceof CauseAction) {
                return Collections.unmodifiableList(((CauseAction) a).getCauses());
            }
        }
        return null;
    }
}
