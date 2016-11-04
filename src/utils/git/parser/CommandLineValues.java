package utils.git.parser;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Localizable;
import org.kohsuke.args4j.Option;
import utils.string.StringUtil;

import java.io.File;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2015-02-02
 * Time: 16:36
 */
public class CommandLineValues {
    @Option(name = "-v", aliases = "--version", required = false, usage = "Check GitGrabber's version")
    private boolean checkVersion;

    @Option(name = "-h", aliases = "--help", required = false, usage = "Print help message")
    private boolean printHelp;

    @Option(name = "-g", aliases = {"--gui"}, required = false, usage = "Run with GUI")
    private boolean runGUI;

    @Option(name = "-c", aliases = {"--config"}, required = false, usage = "Provide config file")
    private File configFile;

    @Option(name = "-t", aliases = {"--target"}, required = false, usage = "Target path of main project (must be used together with remote url, branch (optional, default: master) & dependency (optional, default: dependency.json))")
    private String targetPath;
    @Option(name = "-r", aliases = {"--remote", "--remoteUrl"}, required = false, usage = "Remote repository url (must be used together with target, branch (optional, default: master) & dependency (optional, default: dependency.json))")
    private String remoteUrl;
    @Option(name = "-b", aliases = {"--branch"}, required = false, usage = "Default branch of repository (must be used together with target, remote url & dependency (optional, default: dependency.json))")
    private String defaultBranch;
    @Option(name = "-d", aliases = {"--dependency"}, required = false, usage = "dependency file (must be used together with target, remote url & branch (optional, default: master))")
    private String refFile;

    private CmdLineParser parser;

    public CommandLineValues(String ... args) {
        parser = new CmdLineParser(this);

        parser.getProperties().withUsageWidth(80);

        Localizable errorConfigFileErrorMessage = new Localizable() {
            @Override
            public String formatWithLocale(Locale locale, Object ... args) {
                if (getConfigFile() != null) {
                    return getConfigFile().getName() + " is not a valid config file!";
                } else {
                    return "Config file not exists!";
                }
            }

            @Override
            public String format(Object... args) {
                if (getConfigFile() != null) {
                    return getConfigFile().getName() + " is not a valid config file!";
                } else {
                    return "Config file not exists!";
                }
            }
        };

        try {
            parser.parseArgument(args);

            if (getConfigFile() != null && !getConfigFile().isFile()) {
                throw new CmdLineException(parser, errorConfigFileErrorMessage, args);
            }
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }

        if (args.length == 0) {
            parser.printUsage(System.err);
        }
    }

    public void printUsage() {
        parser.printUsage(System.out);
    }

    public boolean isPrintHelp() {
        return printHelp;
    }

    public boolean isRunGUI() {
        return runGUI;
    }
    
    public boolean isDirectRemoteGrab() {
        return !StringUtil.isEmptyString(targetPath) && !StringUtil.isEmptyString(remoteUrl);
    }
    
    public boolean isDefaultBranchSpecified() {
        return !StringUtil.isEmptyString(defaultBranch);
    }
    
    public boolean isDependencySpecified() {
        return !StringUtil.isEmptyString(refFile);
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public String getRefFile() {
        return refFile;
    }

    public boolean isCheckVersion() {
        return checkVersion;
    }

    public File getConfigFile() {
        return configFile;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public String getTargetPath() {
        return targetPath;
    }
}
