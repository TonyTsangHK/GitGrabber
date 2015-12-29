package utils.git.log;

import ch.qos.logback.core.PropertyDefinerBase;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2015-12-29
 * Time: 12:24
 */
public class PathDefiner extends PropertyDefinerBase {
    private String gitGrabberLogPath;

    public PathDefiner() {
        try {
            // Derive log path from lib path
            gitGrabberLogPath = new File(
                PathDefiner.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()
            ).getParent() + "/../logs";
        } catch (URISyntaxException e) {
            System.err.println("Unable to get program log path");
        }
    }

    public String getGitGrabberLogPath() {
        return gitGrabberLogPath;
    }

    public void setGitGrabberLogPath(String gitGrabberLogPath) {
        this.gitGrabberLogPath = gitGrabberLogPath;
    }

    @Override
    public String getPropertyValue() {
        return getGitGrabberLogPath();
    }
}
