package utils.git.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.data.DataManipulator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2015-01-26
 * Time: 09:37
 */
public class DependencyConfig {
    private HostEntry hostEntry;
    private ProjectEntry mainProjectEntry;
    private List<ProjectEntry> projectEntries;

    private static Logger LOGGER = LoggerFactory.getLogger(DependencyConfig.class);

    public static DependencyConfig fromMap(Map<String, Object> configMap, File parentFolder) {
        HostEntry hostEntry = HostEntry.fromMap(configMap);
        ProjectEntry mainProjectEntry = null;
        List<ProjectEntry> projectEntries = new ArrayList<>();

        if (configMap.containsKey("mainProject")) {
            mainProjectEntry = ProjectEntry.fromMap(
                (Map<String, Object>)configMap.get("mainProject"), hostEntry, parentFolder
            );
        }

        if (configMap.containsKey("requiredProjects")) {
            try {
                List<Map<String, Object>> projectConfigMaps = (List<Map<String, Object>>) configMap.get("requiredProjects");

                for (Map<String, Object> projectConfigMap : projectConfigMaps) {
                    projectEntries.add(ProjectEntry.fromMap(projectConfigMap, hostEntry, parentFolder));
                }
            } catch (ClassCastException cce) {
                LOGGER.error("Error parsing project entry configs.", cce);
                return null;
            }
        }
        return new DependencyConfig(hostEntry, mainProjectEntry, projectEntries);
    }

    public DependencyConfig(HostEntry hostEntry) {
        this.hostEntry = hostEntry;
        this.projectEntries = new ArrayList<>();
    }

    public DependencyConfig(HostEntry hostEntry, ProjectEntry mainProjectEntry, List<ProjectEntry> projectEntries) {
        this.hostEntry = hostEntry;
        this.mainProjectEntry = mainProjectEntry;
        this.projectEntries = projectEntries;
    }

    public HostEntry getHostEntry() {
        return hostEntry;
    }

    public void addProjectEntry(ProjectEntry projectEntry) {
        this.projectEntries.add(projectEntry);
    }

    public int getProjectEntryCount() {
        return this.projectEntries.size();
    }

    public ProjectEntry getProjectEntry(int i) {
        if (i >= 0 && i < projectEntries.size()) {
            return projectEntries.get(i);
        } else {
            return null;
        }
    }

    public ProjectEntry getMainProjectEntry() {
        return mainProjectEntry;
    }

    public List<ProjectEntry> getProjectEntries() {
        return projectEntries;
    }
}
