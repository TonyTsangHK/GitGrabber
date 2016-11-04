package utils.git.app;

import utils.file.FileUtil;
import utils.git.parser.CommandLineValues;

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2015-01-26
 * Time: 12:50
 */
public class GitDependencyHandler {
    private GitDependencyHandler() {}

    public static void main(String[] args) {
        CommandLineValues values = new CommandLineValues(args);

        if (values.isCheckVersion()) {
            System.out.println(getVersion());
        } else if (values.isPrintHelp()) {
            values.printUsage();
        } else if (values.isRunGUI()) {
            GitDependencyHandlerGui.process();
        } else if (values.isDirectRemoteGrab()) {
            if (values.isDefaultBranchSpecified() && values.isDependencySpecified()) {
                GitDependencyHandlerConsole.processRemoteRef(
                    values.getTargetPath(), values.getRemoteUrl(), values.getDefaultBranch(), values.getRefFile()
                );
            } else {
                if (values.isDefaultBranchSpecified()) {
                    GitDependencyHandlerConsole.processRemoteRefWithPresetDependency(
                        values.getTargetPath(), values.getRemoteUrl(), values.getDefaultBranch()
                    );
                } else if (values.isDependencySpecified()) {
                    GitDependencyHandlerConsole.processRemoteRefWithPresetBranch(
                        values.getTargetPath(), values.getRemoteUrl(), values.getRefFile()
                    );
                } else {
                    GitDependencyHandlerConsole.processRemoteRefWithPresetBranchAndDependency(
                        values.getTargetPath(), values.getRemoteUrl()
                    );
                }
            }
        } else {
            if (values.getConfigFile() != null) {
                GitDependencyHandlerConsole.process(values.getConfigFile());
            } else {
                System.err.println("No input parameters, assuming GUI.");
                GitDependencyHandlerGui.process();
            }
        }
    }

    public static String getVersion() {
        return FileUtil.getFileContent(
            GitDependencyHandler.class.getResourceAsStream("/version")
        );
    }
}
