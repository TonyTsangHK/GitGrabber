package utils.git.data;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2015-01-23
 * Time: 12:25
 */
public class RepositoryConfig {
    private File targetFolder;
    private String remoteURL, targetPath, defaultBranch;

    public RepositoryConfig(String targetPath, String remoteURL, String defaultBranch) {
        this.remoteURL = remoteURL;
        this.targetPath = targetPath;
        this.defaultBranch = defaultBranch;
        this.targetFolder = new File(targetPath);
    }

    public RepositoryConfig(File targetFolder, String remoteURL, String defaultBranch) {
        this.remoteURL = remoteURL;
        this.targetFolder = targetFolder;
        this.defaultBranch = defaultBranch;
        this.targetPath = this.targetFolder.getAbsolutePath();
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public String getRemoteURL() {
        return remoteURL;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public File getTargetFolder() {
        return targetFolder;
    }
}
