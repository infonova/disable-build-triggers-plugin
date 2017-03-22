package com.infonova.jenkins;

import hudson.Extension;
import hudson.model.Cause;
import hudson.triggers.SCMTrigger;
import hudson.triggers.TimerTrigger;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.ExportedBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Extension
@ExportedBean
public class DisableBuildTriggersConfig extends GlobalConfiguration {

    public static final List<Class<? extends Cause>> DEFAULTS = Arrays.asList(TimerTrigger.TimerTriggerCause.class, SCMTrigger.SCMTriggerCause.class);

    private String blackList;

    private boolean suppressTriggers;

    public DisableBuildTriggersConfig() {
        // load configfile
        load();
        // create Configfile if not exists
        if (blackList == null) {
            blackList = "";
            save();
        }
    }

    public boolean isSuppressTriggers() {
        return suppressTriggers;
    }

    public void setSuppressTriggers(boolean suppressTriggers) {
        this.suppressTriggers = suppressTriggers;
    }

    public List<Class<? extends Cause>> getBlackListParsed() {

        if (StringUtils.isBlank(blackList)) {
            return  DEFAULTS;
        }

        try {
            return parseBlackList();
        } catch (ClassNotFoundException e) { // should not be possible because of validation
            e.printStackTrace();
            return DEFAULTS;                 // but just in case ... use the defaults
        }
    }

    private List<Class<? extends Cause>> parseBlackList() throws ClassNotFoundException {
        return parseBlackList(this.blackList);
    }

    private List<Class<? extends Cause>> parseBlackList(String blackList) throws ClassNotFoundException {
        List<Class<? extends Cause>> list = new ArrayList<Class<? extends Cause>>();
        for (String s : blackList.split(",")) {
            if (StringUtils.isNotBlank(s.trim())) {
                list.add((Class<? extends Cause>) Class.forName(s.trim()));
            }
        }
        return list;
    }

    public String getBlackList() {
        return blackList;
    }
    public void setBlackList(String blackList) {
        this.blackList = blackList;
    }
    public FormValidation doCheckBlackList(
            @QueryParameter String blackList) {
        if (StringUtils.isBlank(blackList)) {
            return FormValidation.ok("using defaults.");
        }
        try {
            parseBlackList(blackList);
        } catch (ClassNotFoundException e) {
            return FormValidation.error(e, "could not parse Classes.");
        }
        return FormValidation.ok();
    }
    public static DisableBuildTriggersConfig instance() {
        return (DisableBuildTriggersConfig) Jenkins.getInstance().getDescriptor(DisableBuildTriggersConfig.class);
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json)
            throws FormException {
        req.bindJSON(this, json);
        save();
        return true;
    }

}


