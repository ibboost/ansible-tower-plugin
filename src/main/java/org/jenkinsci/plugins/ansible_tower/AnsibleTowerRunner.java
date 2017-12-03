package org.jenkinsci.plugins.ansible_tower;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.Result;
import org.jenkinsci.plugins.ansible_tower.util.TowerConnector;
import org.jenkinsci.plugins.ansible_tower.util.TowerInstallation;

import java.io.PrintStream;

public class AnsibleTowerRunner {
    public boolean runJobTemplate(
            PrintStream logger, String towerServer, String jobTemplate, String extraVars, String limit,
            String jobTags, String inventory, String credential, boolean verbose, boolean importTowerLogs,
            boolean removeColor, EnvVars envVars, String templateType, boolean importWorkflowChildLogs
    ) {
        if (verbose) {
            logger.println("Beginning Ansible Tower Run on " + towerServer);
        }

        AnsibleTowerGlobalConfig myConfig = new AnsibleTowerGlobalConfig();
        TowerInstallation towerConfigToRunOn = myConfig.getTowerInstallationByName(towerServer);
        if (towerConfigToRunOn == null) {
            logger.println("ERROR: Ansible tower server " + towerServer + " does not exist in Ansible Tower configuration");
            return false;
        }

        if(templateType == null || (!templateType.equalsIgnoreCase(TowerConnector.WORKFLOW_TEMPLATE_TYPE) && !templateType.equalsIgnoreCase(TowerConnector.JOB_TEMPLATE_TYPE))) {
            logger.println("ERROR: Template type "+ templateType +" was invalid");
            return false;
        }

        TowerConnector myTowerConnection = towerConfigToRunOn.getTowerConnector();

        // Expand all of the parameters
        String expandedJobTemplate = envVars.expand(jobTemplate);
        String expandedExtraVars = envVars.expand(extraVars);
        String expandedLimit = envVars.expand(limit);
        String expandedJobTags = envVars.expand(jobTags);
        String expandedInventory = envVars.expand(inventory);
        String expandedCredential = envVars.expand(credential);

        if (verbose) {
            if(expandedJobTemplate != null && !expandedJobTemplate.equals(jobTemplate)) {
                logger.println("Expanded job template to " + expandedJobTemplate);
            }
            if(expandedExtraVars != null && !expandedExtraVars.equals(extraVars)) {
                logger.println("Expanded extra vars to " + expandedExtraVars);
            }
            if(expandedLimit != null && !expandedLimit.equals(limit)) {
                logger.println("Expanded limit to " + expandedLimit);
            }
            if(expandedJobTags != null && !expandedJobTags.equals(jobTags)) {
                logger.println("Expanded job tags to " + expandedJobTags);
            }
            if(expandedInventory != null && !expandedInventory.equals(inventory)) {
                logger.println("Expanded inventory to " + expandedInventory);
            }
            if(expandedCredential != null && !expandedCredential.equals(credential)) {
                logger.println("Expanded credentials to " + expandedCredential);
            }
        }

        if(verbose) {
            logger.println("Requesting tower to run " + templateType + " template " + jobTemplate);
        }
        int myJobID;
        try {
            System.out.println("The template type is "+ templateType);
            myJobID = myTowerConnection.submitTemplate(expandedJobTemplate, expandedExtraVars, expandedLimit, expandedJobTags, expandedInventory, expandedCredential, templateType);
        } catch (AnsibleTowerException e) {
            logger.println("ERROR: Unable to request job template invocation " + e.getMessage());
            return false;
        }

        String jobURL = myTowerConnection.getJobURL(myJobID, templateType);

        logger.println("Template Job URL: "+ jobURL);

        boolean jobCompleted = false;
        while(!jobCompleted) {
            // First log any events if the user wants them
            try {
                if (importTowerLogs) { myTowerConnection.logEvents(myJobID, logger, removeColor, templateType, importWorkflowChildLogs); }
            } catch(AnsibleTowerException e) {
                logger.println("ERROR: Failed to get job events from tower: "+ e.getMessage());
                return false;
            }
            try {
                jobCompleted = myTowerConnection.isJobCommpleted(myJobID, templateType);
            } catch(AnsibleTowerException e) {
                logger.println("ERROR: Failed to get job status from Tower: "+ e.getMessage());
                return false;
            }
            if(!jobCompleted) {
                try {
                    Thread.sleep(3000);
                } catch(InterruptedException ie) {
                    logger.println("ERROR: Got interrupted while sleeping");
                    return false;
                }
            }
        }

        try {
            if(myTowerConnection.isJobFailed(myJobID, templateType)) {
                logger.println("Tower failed to complete the requeted job");
                return false;
            } else {
                if(verbose) { logger.println("Tower completed the requested job"); }
                return true;
            }
        } catch(AnsibleTowerException e) {
            logger.println("ERROR: Failed to job failure status from Tower: "+ e.getMessage());
            return false;
        }
    }
}