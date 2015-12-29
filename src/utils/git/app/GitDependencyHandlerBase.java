package utils.git.app;

import utils.git.GitGrabber;
import utils.git.data.DependencyConfig;
import utils.git.data.ProjectEntry;
import utils.git.data.RepositoryConfig;
import utils.git.output.OutputHandler;
import utils.git.parser.ConfigParser;
import utils.string.StringUtil;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2015-01-26
 * Time: 10:36
 */
public abstract class GitDependencyHandlerBase {
    protected OutputHandler outputHandler;

    protected GitDependencyHandlerBase(OutputHandler outputHandler) {
        this.outputHandler = outputHandler;
    }

    protected void doMain(File configFile) {
        if (configFile == null || !configFile.exists()) {
            outputHandler.info("Config file is null or does not exists, quitting!");
        } else {
            ConfigParser configParser = ConfigParser.getInstance();

            DependencyConfig dependencyConfig = configParser.parseConfig(configFile);

            if (dependencyConfig == null) {
                outputHandler.info("Failed to parse dependency config, exiting ...");
            } else {
                outputHandler.info("Start processing dependencies ...");
                processGitDependency(dependencyConfig);
                outputHandler.info("Done!");
            }
        }
    }

    protected void doMain(String targetPath, String remoteUrl, String defaultBranch, String refFile) {
        if (prepareMainRepo(new RepositoryConfig(targetPath, remoteUrl, defaultBranch))) {
            outputHandler.info("Preparation finished, start grabbing the project dependencies!");
            doMain(new File(targetPath, refFile));
        } else {
            outputHandler.info("Can't clone remote repository: " + remoteUrl + ", defaultBranch: " + defaultBranch);
        }
    }

    private boolean updateRepo(ProjectEntry projectEntry) {
        outputHandler.info("Updating project " + projectEntry.getName() + " ...");
        if (GitGrabber.pullRepo(projectEntry.getRepositoryConfig())) {
            outputHandler.info(projectEntry.getName() + " is updated successfully!");
            return true;
        } else {
            outputHandler.info("Failed to update " + projectEntry.getName() + "!");
            return false;
        }
    }

    private boolean prepareMainRepo(RepositoryConfig repositoryConfig) {
        outputHandler.info("Trying to clone " + repositoryConfig.getRemoteURL() + " to " + repositoryConfig.getTargetPath());
        if (GitGrabber.cloneRepo(repositoryConfig)) {
            outputHandler.info("Checking out " + repositoryConfig.getDefaultBranch() + " branch!");
            if (GitGrabber.checkoutBranch(repositoryConfig)) {
                outputHandler.info("Main project is now on branch " + repositoryConfig.getDefaultBranch() + "!");
                return true;
            } else {
                return false;
            }
        } else {
            if (new File(repositoryConfig.getTargetFolder(), ".git").exists()) {
                outputHandler.info("Main project already exists, checking out default branch!");
                if (GitGrabber.checkoutBranch(repositoryConfig)) {
                    outputHandler.info("Main project is now on branch " + repositoryConfig.getDefaultBranch() + "!");
                    if (GitGrabber.pullRepo(repositoryConfig)) {
                        outputHandler.info("Successfully updated the main project!");
                        return true;
                    } else {
                        outputHandler.info("Failed to update main project!");
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    private boolean cloneRepo(ProjectEntry projectEntry) {
        RepositoryConfig repositoryConfig = projectEntry.getRepositoryConfig();

        outputHandler.info("Cloning " + projectEntry.getName() + " to " + repositoryConfig.getTargetPath() + " ... ");
        if (GitGrabber.cloneRepo(repositoryConfig)) {
            outputHandler.info(projectEntry.getName() + " is cloned successfully!");
            return true;
        } else {
            outputHandler.info("Failed to clone " + projectEntry.getName() + "!");
            return false;
        }
    }

    private boolean checkoutDefaultBranch(ProjectEntry projectEntry) {
        RepositoryConfig repositoryConfig = projectEntry.getRepositoryConfig();
        outputHandler.info("Checking out branch " + repositoryConfig.getDefaultBranch() + " of " + projectEntry.getName() + " ...");
        if (GitGrabber.checkoutBranch(repositoryConfig)) {
            outputHandler.info("Project is now on branch " + repositoryConfig.getDefaultBranch() + "!");
            return true;
        } else {
            outputHandler.info("Failed to checkout branch " + repositoryConfig.getDefaultBranch() + "!");
            return false;
        }
    }

    private void processProjectEntry(ProjectEntry projectEntry) {
        RepositoryConfig repositoryConfig = projectEntry.getRepositoryConfig();

        if (repositoryConfig.getTargetFolder().exists()) {
            File gitFolder = new File(repositoryConfig.getTargetFolder(), ".git");
            if (gitFolder.exists()) {
                if (projectEntry.isUpdateWhenExists()) {
                    updateRepo(projectEntry);
                } else {
                    outputHandler.info("Project " + projectEntry.getName() + " exists, skipping!");
                }
            } else {
                if (cloneRepo(projectEntry)) {
                    if (!StringUtil.isEmptyString(repositoryConfig.getDefaultBranch())) {
                        checkoutDefaultBranch(projectEntry);
                    }
                }
            }
        } else {
            if (cloneRepo(projectEntry)) {
                if (!StringUtil.isEmptyString(repositoryConfig.getDefaultBranch())) {
                    checkoutDefaultBranch(projectEntry);
                }
            }
        }
    }

    public void processGitDependency(DependencyConfig dependencyConfig) {
        ProjectEntry mainProjectEntry = dependencyConfig.getMainProjectEntry();

        processProjectEntry(mainProjectEntry);

        for (int i = 0; i < dependencyConfig.getProjectEntryCount(); i++) {
            ProjectEntry projectEntry = dependencyConfig.getProjectEntry(i);

            processProjectEntry(projectEntry);
        }
    }
}
