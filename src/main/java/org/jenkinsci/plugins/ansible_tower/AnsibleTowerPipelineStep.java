package org.jenkinsci.plugins.ansible_tower;

import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Nonnull;

import org.jenkinsci.plugins.ansible_tower.util.GetUserPageCredentials;
import org.jenkinsci.plugins.ansible_tower.util.TowerInstallation;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Computer;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ListBoxModel;

public class AnsibleTowerPipelineStep extends Step {
    private String towerServer              = "";
    private String towerCredentialsId       = "";
    private String jobTemplate              = "";
    private String jobType                  = "run";
    private String extraVars                = "";
    private String limit                    = "";
    private String jobTags                  = "";
    private String skipJobTags              = "";
    private String inventory                = "";
    private String credential               = "";
    private String scmBranch                = "";
    private Boolean verbose                 = false;
    private String towerLogLevel            = null;
    private Boolean removeColor             = false;
    private String templateType             = "job";
    private Boolean importWorkflowChildLogs = false;
    private Boolean throwExceptionWhenFail  = true;
    private Boolean async                   = false;


    /** @since 0.16.0 */
    @DataBoundConstructor
    public AnsibleTowerPipelineStep(
            @Nonnull String towerServer, @Nonnull String towerCredentialsId, @Nonnull String jobTemplate, String jobType
    ) {
        this.towerServer = towerServer;
        this.towerCredentialsId = towerCredentialsId;
        this.jobTemplate = jobTemplate;
        this.jobType = jobType;
    }


    @Override public final StepExecution start(StepContext context) throws Exception {
    	return new AnsibleTowerStepExecution(this, context);
    }


    @Nonnull
    public String getTowerServer()              { return towerServer; }
    @Nonnull
    public String getJobTemplate()              { return jobTemplate; }
    public String getTowerCredentialsId()       { return towerCredentialsId; }
    public String getExtraVars()                { return extraVars; }
    public String getJobTags()                  { return jobTags; }
    public String getSkipJobTags()              { return skipJobTags; }
    public String getJobType()                  { return jobType;}
    public String getLimit()                    { return limit; }
    public String getInventory()                { return inventory; }
    public String getCredential()               { return credential; }
    public String getScmBranch()                { return scmBranch; }
    public Boolean getVerbose()                 { return verbose; }
    public String getTowerLogLevel()            { return towerLogLevel; }
    public Boolean getRemoveColor()             { return removeColor; }
    public String getTemplateType()             { return templateType; }
    public Boolean getImportWorkflowChildLogs() { return importWorkflowChildLogs; }
    public Boolean getThrowExceptionWhenFail()  { return throwExceptionWhenFail; }
    public Boolean getAsync()                   { return async; }

    @DataBoundSetter
    public void setTowerServer(String towerServer) { this.towerServer = towerServer; }
    @DataBoundSetter
    public void setJobTemplate(String jobTemplate) { this.jobTemplate = jobTemplate; }
    @DataBoundSetter
    public void setTowerCredentialsId(String towerCredentialsId) { this.towerCredentialsId = towerCredentialsId; }
     @DataBoundSetter
    public void setExtraVars(String extraVars) { this.extraVars = extraVars; }
    @DataBoundSetter
    public void setJobTags(String jobTags) { this.jobTags = jobTags; }
    @DataBoundSetter
    public void setSkipJobTags(String skipJobTags) { this.skipJobTags = skipJobTags; }
    @DataBoundSetter
    public void setJobType(String jobType) { this.jobType = jobType; }
    @DataBoundSetter
    public void setLimit(String limit) { this.limit = limit; }
    @DataBoundSetter
    public void setInventory(String inventory) { this.inventory = inventory; }
    @DataBoundSetter
    public void setCredential(String credential) { this.credential = credential; }
    @DataBoundSetter
	public void setScmBranch(String scmBranch) { this.scmBranch = scmBranch; }
	@DataBoundSetter
    public void setVerbose(Boolean verbose) { this.verbose = verbose; }
    @DataBoundSetter
    public void setTowerLogLevel(String towerLogLevel) { this.towerLogLevel = towerLogLevel; }
    @DataBoundSetter
    public void setRemoveColor(Boolean removeColor) { this.removeColor = removeColor; }
    @DataBoundSetter
    public void setTemplateType(String templateType) { this.templateType = templateType; }
    @DataBoundSetter
    public void setImportWorkflowChildLogs(Boolean importWorkflowChildLogs) { this.importWorkflowChildLogs = importWorkflowChildLogs; }
    @DataBoundSetter
    public void setThrowExceptionWhenFail(Boolean throwExceptionWhenFail) { this.throwExceptionWhenFail = throwExceptionWhenFail; }
    @DataBoundSetter
    public void setAsync(Boolean async) { this.async = async; }

    public boolean isGlobalColorAllowed() {
        return true;
    }

    @Extension(optional = true)
    public static final class DescriptorImpl extends StepDescriptor {
        public static final String towerServer              = AnsibleTower.DescriptorImpl.towerServer;
        public static final String towerCredentialsId       = AnsibleTower.DescriptorImpl.towerCredentialsId;
        public static final String jobTemplate              = AnsibleTower.DescriptorImpl.jobTemplate;
        public static final String jobType                  = AnsibleTower.DescriptorImpl.jobType;
        public static final String extraVars                = AnsibleTower.DescriptorImpl.extraVars;
        public static final String limit                    = AnsibleTower.DescriptorImpl.limit;
        public static final String jobTags                  = AnsibleTower.DescriptorImpl.jobTags;
        public static final String skipJobTags              = AnsibleTower.DescriptorImpl.skipJobTags;
        public static final String inventory                = AnsibleTower.DescriptorImpl.inventory;
        public static final String credential               = AnsibleTower.DescriptorImpl.credential;
        public static final String scmBranch                = AnsibleTower.DescriptorImpl.scmBranch;
        public static final Boolean verbose                 = AnsibleTower.DescriptorImpl.verbose;
        public static final String towerLogLevel            = AnsibleTower.DescriptorImpl.towerLogLevel;
        public static final Boolean removeColor             = AnsibleTower.DescriptorImpl.removeColor;
        public static final String templateType             = AnsibleTower.DescriptorImpl.templateType;
        public static final Boolean importWorkflowChildLogs = AnsibleTower.DescriptorImpl.importWorkflowChildLogs;
        public static final Boolean throwExceptionWhenFail  = AnsibleTower.DescriptorImpl.throwExceptionWhenFail;
        public static final Boolean async                   = AnsibleTower.DescriptorImpl.async;

        @Override
        public Set<? extends Class<?>> getRequiredContext()
        {
          Set<Class<?>> contexts = new HashSet<>();
          contexts.add(TaskListener.class);
          contexts.add(Run.class);
          contexts.add(Launcher.class);
          contexts.add(FilePath.class);
          contexts.add(EnvVars.class);
          contexts.add(Computer.class);
          contexts.add(Run.class);
          return contexts;
        }

        @Override
        public String getFunctionName() {
            return "ansibleTowerStep";
        }

        @Override
        public String getDisplayName() {
            return "Have Ansible Tower run a job template";
        }

        public ListBoxModel doFillTowerServerItems() {
            ListBoxModel items = new ListBoxModel();
            items.add(" - None -");
            for (TowerInstallation towerServer : AnsibleTowerGlobalConfig.get().getTowerInstallation()) {
                items.add(towerServer.getTowerDisplayName());
            }
            return items;
        }

        public ListBoxModel doFillTemplateTypeItems() {
            ListBoxModel items = new ListBoxModel();
            items.add("job");
            items.add("workflow");
            return items;
        }
        public ListBoxModel doFillJobTypeItems() {
            ListBoxModel items = new ListBoxModel();
            items.add("run");
            items.add("check");
            return items;
        }

        public ListBoxModel doFillTowerLogLevelItems() {
            ListBoxModel items = new ListBoxModel();
            items.add("Do not import", "false");
            items.add("Import Truncated Logs", "true");
            items.add("Import Full Logs", "full");
            items.add("Process Variables Only", "vars");
            return items;
        }

        public boolean isGlobalColorAllowed() {
            return true;
        }

        // This requires a POST method to protect from CSFR
        @POST
        public ListBoxModel doFillTowerCredentialsIdItems(@AncestorInPath Item item, @QueryParameter String towerCredentialsId) {
            return GetUserPageCredentials.getUserAvailableCredentials(item, towerCredentialsId);
        }
    }


    //public static final class AnsibleTowerStepExecution extends AbstractSynchronousNonBlockingStepExecution<Properties> {
    @SuppressFBWarnings(value="SE_TRANSIENT_FIELD_NOT_RESTORED", justification="Only used when starting.")
    public static final class AnsibleTowerStepExecution extends SynchronousNonBlockingStepExecution<Properties> {
        private static final long serialVersionUID = 1L;

        private transient final AnsibleTowerPipelineStep step;
        private transient TaskListener listener;
        private transient Launcher launcher;
        private transient Run<?,?> run;
        private transient FilePath ws;
        private transient EnvVars envVars;
        private transient Computer computer;

        AnsibleTowerStepExecution(AnsibleTowerPipelineStep step, StepContext context) {
            super(context);
            this.step = step;
            try {
				this.listener = context.get(TaskListener.class);
				this.launcher = context.get(Launcher.class);
				this.computer= context.get(Computer.class);
				this.envVars = context.get(EnvVars.class);
				this.ws = context.get(FilePath.class);
				this.run = context.get(Run.class);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }


        @Override
        protected Properties run() throws AbortException {
            if ((computer == null) || (computer.getNode() == null)) {
                throw new AbortException("The Ansible Tower build step requires to be launched on a node");
            }

            AnsibleTowerRunner runner = new AnsibleTowerRunner();

            // Doing this will make the options optional in the pipeline step.
            String towerCredentialsId = "";
            if(step.getTowerCredentialsId() != null) { towerCredentialsId = step.getTowerCredentialsId(); }
            String extraVars = "";
            if(step.getExtraVars() != null) { extraVars = step.getExtraVars(); }
            String limit = "";
            if(step.getLimit() != null) { limit = step.getLimit(); }
            String tags = "";
            if(step.getJobTags() != null) { tags = step.getJobTags(); }
            String skipTags = "";
            if(step.getSkipJobTags() != null) { skipTags = step.getSkipJobTags(); }
            String jobType = "run";
            if(step.getJobType() != null){ jobType = step.getJobType();}
            String inventory = "";
            if(step.getInventory() != null) { inventory = step.getInventory(); }
            String credential = "";
            if(step.getCredential() != null) { credential = step.getCredential(); }
            String scmBranch = "";
            if(step.getScmBranch() != null) { scmBranch = step.getScmBranch(); }
            boolean verbose = false;
            if(step.getVerbose() != null) { verbose = step.getVerbose(); }
            String towerLogLevel = "false";
            if(step.getTowerLogLevel() != null) { towerLogLevel = step.getTowerLogLevel(); }
            boolean removeColor = false;
            if(step.getRemoveColor() != null) { removeColor = step.getRemoveColor(); }
            String templateType = "job";
            if(step.getTemplateType() != null) { templateType = step.getTemplateType(); }
            boolean importWorkflowChildLogs = false;
            if(step.getImportWorkflowChildLogs() != null) { importWorkflowChildLogs = step.getImportWorkflowChildLogs(); }
            boolean throwExceptionWhenFail = true;
            if(step.getThrowExceptionWhenFail() != null) { throwExceptionWhenFail = step.getThrowExceptionWhenFail(); }
            boolean async = false;
            if(step.getAsync() != null) { async = step.getAsync(); }
            Properties map = new Properties();
            boolean runResult = runner.runJobTemplate(
                    listener.getLogger(), step.getTowerServer(), towerCredentialsId, step.getJobTemplate(), jobType, extraVars,
                    limit, tags, skipTags, inventory, credential, scmBranch, verbose, towerLogLevel, removeColor, envVars,
                    templateType, importWorkflowChildLogs, ws, run, map, async
            );
            if(!runResult && throwExceptionWhenFail) {
                throw new AbortException("Ansible Tower build step failed");
            }
            return map;
        }
    }
}

