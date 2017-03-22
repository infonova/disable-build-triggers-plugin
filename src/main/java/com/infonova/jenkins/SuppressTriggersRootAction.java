package com.infonova.jenkins;

import hudson.Extension;
import hudson.model.RootAction;
import hudson.security.ACL;
import hudson.security.AccessControlled;
import hudson.security.Permission;
import jenkins.model.Jenkins;
import org.acegisecurity.AccessDeniedException;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;

/**
 * Created by kazesberger on 22.03.17.
 */
@Extension
public class SuppressTriggersRootAction implements RootAction, AccessControlled {

    @Override
    public String getIconFileName() {
        // not used
        return null;
    }

    @Override
    public String getDisplayName() {
        return "switch Suppress TriggerCauses";
    }

    @Override
    public String getUrlName() {
        return "suppress-triggers";
    }

    public void doSuppressTriggersOn(StaplerRequest req, StaplerResponse rsp) throws IOException {
        switchSuppressTriggersTo(true, rsp);
    }

    public void doSuppressTriggersOff(StaplerRequest req, StaplerResponse rsp) throws IOException {
        switchSuppressTriggersTo(false, rsp);
    }

    private void switchSuppressTriggersTo(boolean suppressTriggers, StaplerResponse rsp) throws IOException {
        AccessControlled ac = Jenkins.getInstance();
        Permission p = Jenkins.ADMINISTER;
        ac.checkPermission(p);
        try {
            DisableBuildTriggersConfig config = DisableBuildTriggersConfig.instance();
            config.setSuppressTriggers(suppressTriggers);
            rsp.getWriter().write("Successful. SuppressTriggers = '" + suppressTriggers + "'.");
            rsp.setStatus(200);
        } catch (IOException e) {
            rsp.getWriter().write("failed to turn SuppressTriggers switch to '" + suppressTriggers + "'.");
            rsp.setStatus(500);
            throw e;
        }
    }

    @Override
    public ACL getACL() {
        return null;
    }

    @Override
    public void checkPermission(Permission permission) throws AccessDeniedException {

    }

    @Override
    public boolean hasPermission(Permission permission) {
        return false;
    }
}
