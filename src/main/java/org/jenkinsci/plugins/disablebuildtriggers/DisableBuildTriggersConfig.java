package org.jenkinsci.plugins.disablebuildtriggers;

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
import java.util.logging.Level;
import java.util.logging.Logger;

@Extension
@ExportedBean
public class DisableBuildTriggersConfig extends GlobalConfiguration {

    private static final Logger LOGGER = Logger.getLogger(DisableBuildTriggersConfig.class.getName());

    private static final List<Class<? extends Cause>> DEFAULTS = Arrays.asList(TimerTrigger.TimerTriggerCause.class, SCMTrigger.SCMTriggerCause.class);

    private String blackList;

    private boolean suppressTriggers;

    public DisableBuildTriggersConfig() {
        // load configfile
        load();
        // create Configfile if not exists
        if (blackList == null) {
            blackList = StringUtils.EMPTY;
            save();
        }
    }

    public boolean isSuppressTriggers() {
        return suppressTriggers;
    }

    public void setSuppressTriggers(boolean suppressTriggers) {
        this.suppressTriggers = suppressTriggers;
    }

    @Override
    public String getDisplayName() {
        return Messages.DisableBuildTriggersConfig_DisplayName();
    }

    public List<Class<? extends Cause>> getParsedBlackList() {

        if (StringUtils.isBlank(blackList)) {
            return  DEFAULTS;
        }

        try {
            return parseBlackList(this.blackList);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, Messages.DisableBuildTriggersConfig_ErrorWhileParsingBlackList(e.getMessage()), e);
            return DEFAULTS;
        }
    }

    private List<Class<? extends Cause>> parseBlackList(String blackList) throws ClassNotFoundException {
        List<Class<? extends Cause>> list = new ArrayList<Class<? extends Cause>>();
        for (String s : blackList.split(",")) {
            if (StringUtils.isNotBlank(s.trim())) {
                ClassLoader classLoader = Jenkins.getInstance().getPluginManager().uberClassLoader;
                list.add((Class<? extends Cause>) Class.forName(s.trim(), true, classLoader));
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

    public FormValidation doCheckBlackList(@QueryParameter String blackList) {
        if (StringUtils.isBlank(blackList)) {
            return FormValidation.ok(Messages.DisableBuildTriggersConfig_UsingDefaultBlacklist(StringUtils.join(DEFAULTS,",")));
        }

        try {
            parseBlackList(blackList);
        } catch (ClassNotFoundException e) {
            return FormValidation.error(e, Messages.DisableBuildTriggersConfig_ClassParseError());
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


