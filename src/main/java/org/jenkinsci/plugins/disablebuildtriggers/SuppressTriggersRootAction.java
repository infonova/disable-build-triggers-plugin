package org.jenkinsci.plugins.disablebuildtriggers;

import hudson.Extension;
import hudson.model.RootAction;
import hudson.security.ACL;
import hudson.security.AccessControlled;
import hudson.security.Permission;
import jenkins.model.Jenkins;
import org.acegisecurity.AccessDeniedException;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Extension
public class SuppressTriggersRootAction implements RootAction, AccessControlled {

    private static final Logger LOGGER = Logger.getLogger(SuppressTriggersRootAction.class.getName());

    @Override
    public String getIconFileName() {
        // not used
        return null;
    }

    @Override
    public String getDisplayName() {
        return Messages.DisableBuildTriggersConfig_DisplayName();
    }

    @Override
    public String getUrlName() {
        return Messages.SuppressTriggersRootAction_UrlName();
    }

    @SuppressWarnings("unused")
    public String doSuppressTriggersOn(StaplerRequest req, StaplerResponse rsp) throws IOException {
        return switchSuppressTriggersTo(true, rsp);
    }

    @SuppressWarnings("unused")
    public String doSuppressTriggersOff(StaplerRequest req, StaplerResponse rsp) throws IOException {
        return switchSuppressTriggersTo(false, rsp);
    }

    private String switchSuppressTriggersTo(boolean suppressTriggers, StaplerResponse rsp) throws IOException {
        checkPermission(Jenkins.ADMINISTER);

        try {
            DisableBuildTriggersConfig config = DisableBuildTriggersConfig.instance();
            config.setSuppressTriggers(suppressTriggers);
            rsp.setStatus(StaplerResponse.SC_OK);
            return Messages.SuppressTriggersRootAction_SuccessfullySetTrigger(suppressTriggers);
        } catch (Exception e) {
            rsp.setStatus(StaplerResponse.SC_INTERNAL_SERVER_ERROR);
            String errorMessage = Messages.SuppressTriggersRootAction_FailedToSetTrigger(suppressTriggers, e.getMessage());
            LOGGER.log(Level.SEVERE, errorMessage, e);
            return errorMessage;
        }
    }

    @Override
    @Nonnull
    public ACL getACL() {
        return Jenkins.getInstance().getACL();
    }

    @Override
    public void checkPermission(@Nonnull Permission permission) throws AccessDeniedException {
        Jenkins.getInstance().checkPermission(permission);
    }

    @Override
    public boolean hasPermission(@Nonnull Permission permission) {
        return false;
    }
}
