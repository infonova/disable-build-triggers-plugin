package org.jenkinsci.plugins.disablebuildtriggers;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Cause;
import hudson.model.CauseAction;
import hudson.model.Queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            if (config.getParsedBlackList().contains(c.getClass())) {
                return false;
            }
        }
        return true;
    }

    private List<Cause> getCauses(List<Action> actions) {
        List<Cause> causes = new ArrayList<Cause>();

        for (Action a : actions) {
            if (a instanceof CauseAction) {
                causes.addAll(((CauseAction) a).getCauses());
            }
        }

        return Collections.unmodifiableList(causes);
    }
}
