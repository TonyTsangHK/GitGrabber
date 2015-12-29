package utils.git.data;

import utils.data.DataManipulator;

import java.io.File;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2015-01-23
 * Time: 17:54
 */
public class ProjectEntry {
    private String name;
    private RepositoryConfig repositoryConfig;
    private boolean updateWhenExists;

    // fromMap is the only way to construct ProjectEntry
    public static ProjectEntry fromMap(Map<String, Object> entryMap, HostEntry hostEntry, File parentFolder) {
        if (entryMap.containsKey("name") && entryMap.containsKey("local")) {
            ProjectEntry entry = new ProjectEntry();

            String localPath = DataManipulator.getStringValue(entryMap, "local", "");

            entry.name = DataManipulator.getStringValue(entryMap, "name", "");
            File localFolder = new File(parentFolder, localPath);

            if (entryMap.containsKey("updateWhenExists")) {
                entry.updateWhenExists = DataManipulator.extractBoolean(entryMap.get("updateWhenExists"), false);
            } else {
                entry.updateWhenExists = false;
            }

            String defaultBranch = DataManipulator.getStringValue(entryMap, "defaultBranch", "");

            String fullRemoteUrl;

            if (entryMap.containsKey("fullRemoteUrl")) {
                fullRemoteUrl = DataManipulator.getStringValue(entryMap, "fullRemoteUrl", "");

                entry.repositoryConfig = new RepositoryConfig(localFolder, fullRemoteUrl, defaultBranch);
            } else if (entryMap.containsKey("remoteUrl")) {
                fullRemoteUrl = hostEntry.buildRemoteUrl(DataManipulator.getStringValue(entryMap, "remoteUrl", ""));

                entry.repositoryConfig = new RepositoryConfig(localFolder, fullRemoteUrl, defaultBranch);
            } else {
                throw new IllegalArgumentException("Entry map must consist at least remoteUrl / fullRemoteUrl");
            }

            return entry;
        } else {
            throw new IllegalArgumentException("Entry must has name and local field!");
        }
    }

    private ProjectEntry() {}

    public String getName() {
        return name;
    }

    public RepositoryConfig getRepositoryConfig() {
        return repositoryConfig;
    }

    public boolean isUpdateWhenExists() {
        return updateWhenExists;
    }
}
