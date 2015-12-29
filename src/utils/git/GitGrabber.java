package utils.git;

import com.jcraft.jsch.Session;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.archive.TarFormat;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.git.data.RepositoryConfig;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2015-01-23
 * Time: 10:04
 */
public class GitGrabber {
    private static Logger LOGGER = LoggerFactory.getLogger(GitGrabber.class);

    private GitGrabber() {}

    static {
        SshSessionFactory.setInstance(
            new JschConfigSessionFactory() {
                @Override
                protected void configure(OpenSshConfig.Host hc, Session session) {
                    session.setConfig("StrictHostKeyChecking", "yes");
                }
            }
        );
    }

    public static boolean checkoutBranch(File repoFolder, String branch) {
        if (repoFolder == null) {
            return false;
        } else {
            try {
                File gitFolder = new File(repoFolder, ".git"), actualProcessFolder = repoFolder;
                if (gitFolder.exists()) {
                    actualProcessFolder = gitFolder;
                }

                Repository repo = new FileRepository(actualProcessFolder);

                if (repo.getBranch().equals(branch)) {
                    LOGGER.info("Project already at branch {}, nothing to do.\n", branch);
                    return true;
                }

                Git git = new Git(repo);

                // Issue??
                CheckoutCommand checkoutCmd = git.checkout().setCreateBranch(true).setName(branch)
                    .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).setStartPoint("origin/"+branch);

                try {
                    Ref ref = checkoutCmd.call();

                    git.getRepository().close();

                    if (ref != null) {
                        LOGGER.debug("Current branch: " + ref.getName());
                        ref.getName();

                        return true;
                    } else {
                        return false;
                    }
                } catch (GitAPIException e) {
                    LOGGER.error("Error occurred during pull.", e);
                    return false;
                }
            } catch (IOException e) {
                LOGGER.error("Error occurred during repo creation.", e);
                return false;
            }
        }
    }

    public static boolean checkoutBranch(RepositoryConfig repositoryConfig) {
        return checkoutBranch(repositoryConfig.getTargetFolder(), repositoryConfig.getDefaultBranch());
    }

    public static boolean pullRepo(File repoFolder) {
        if (repoFolder == null) {
            return false;
        } else {
            try {
                File gitFolder = new File(repoFolder, ".git"), actualProcessFolder = repoFolder;
                if (gitFolder.exists()) {
                    actualProcessFolder = gitFolder;
                }

                Repository repo = new FileRepository(actualProcessFolder);

                Git git = new Git(repo);

                PullCommand pullCmd = git.pull();

                try {
                    PullResult result = pullCmd.call();

                    FetchResult fetchResult = result.getFetchResult();
                    MergeResult mergeResult = result.getMergeResult();

                    LOGGER.debug("Fetch result: " + fetchResult.getMessages());
                    Map<String, int[][]> mergeConflictMap = mergeResult.getConflicts();

                    git.getRepository().close();

                    if (mergeConflictMap == null || mergeConflictMap.isEmpty()) {
                        return true;
                    } else {
                        for (String p : mergeConflictMap.keySet()) {
                            LOGGER.debug("Conflicting path: " + p);
                        }
                        return false;
                    }
                } catch (GitAPIException e) {
                    LOGGER.error("Error occurred during pull.", e);
                    return false;
                }
            } catch (IOException e) {
                LOGGER.error("Error occurred during repo creation.", e);
                return false;
            }
        }
    }

    public static boolean pullRepo(RepositoryConfig repositoryConfig) {
        return pullRepo(repositoryConfig.getTargetFolder());
    }

    public static boolean cloneRepo(RepositoryConfig repositoryConfig) {
        String remoteUrl = repositoryConfig.getRemoteURL();
        File targetFolder = repositoryConfig.getTargetFolder();

        if (targetFolder == null || remoteUrl == null) {
            return false;
        } else {
            boolean isEmptyFolder;
            isEmptyFolder = !targetFolder.exists() || targetFolder.list().length == 0;

            if (isEmptyFolder) {
                if (targetFolder.exists() || targetFolder.mkdirs()) {
                    CloneCommand cloneCmd = Git.cloneRepository();

                    cloneCmd.setBare(false).setCloneAllBranches(true).setDirectory(targetFolder).setURI(remoteUrl);

                    try {
                        Git resultGit = cloneCmd.call();

                        resultGit.getRepository().close();

                        return true;
                    } catch (GitAPIException e) {
                        LOGGER.error("Error occurred during clone!", e);
                        return false;
                    }
                } else {
                    LOGGER.error("Failed to created target folder, " + targetFolder.getAbsolutePath());
                    return false;
                }
            } else {
                LOGGER.error("Target folder " + targetFolder.getAbsolutePath() + " exists and not empty!");
                return false;
            }
        }
    }
}
