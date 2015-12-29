package utils.git.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.file.FileUtil;
import utils.git.GitGrabber;
import utils.git.data.DependencyConfig;
import utils.git.data.ProjectEntry;
import utils.git.data.RepositoryConfig;
import utils.git.output.OutputHandler;
import utils.git.parser.ConfigParser;
import utils.string.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2015-01-26
 * Time: 09:43
 */
public class GitDependencyHandlerConsole extends GitDependencyHandlerBase {
    private static Logger LOGGER = LoggerFactory.getLogger(GitDependencyHandlerConsole.class);

    private static GitDependencyHandlerConsole instance;

    private GitDependencyHandlerConsole() {
        super(new ConsoleOutputHandler());
    }

    public static GitDependencyHandlerConsole getInstance() {
        if (instance == null) {
            instance = new GitDependencyHandlerConsole();
        }

        return instance;
    }

    public static void process(File configFile) {
        getInstance().doMain(configFile);
    }

    public static void processRemoteRef(String targetPath, String remoteUrl, String defaultBranch, String refFile) {
        getInstance().doMain(targetPath, remoteUrl, defaultBranch, refFile);
    }

    public static class ConsoleOutputHandler implements OutputHandler {
        @Override
        public void debug(String msg) {
            System.out.println(msg);
        }

        @Override
        public void info(String msg) {
            System.out.println(msg);
        }

        @Override
        public void error(String msg, Throwable e) {
            System.err.println(msg);
            LOGGER.debug(msg, e);
        }
    }
}
