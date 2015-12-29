package utils.git.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.data.DataManipulator;
import utils.git.data.DependencyConfig;
import utils.json.parser.JsonParser;
import utils.stream.CharacterStreamHandler;
import utils.string.StringUtil;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2015-01-26
 * Time: 09:46
 */
public class ConfigParser {
    public static final String CONFIG_START_TAG = "/*@->", CONFIG_END_TAG = "<-@*/";
    private static Logger LOGGER = LoggerFactory.getLogger(ConfigParser.class);
    private static ConfigParser instance = new ConfigParser();

    private ConfigParser() {}

    public static ConfigParser getInstance() {
        return instance;
    }

    public DependencyConfig parseConfig(File configFile) {
        try {
            Reader reader = new InputStreamReader(new FileInputStream(configFile), "UTF-8");
            CharacterStreamHandler hdl = new CharacterStreamHandler(reader);

            String configContent = readConfigContent(hdl);

            hdl.close();

            if (configContent != null) {
                JsonParser jsonParser = JsonParser.getInstance();

                Map<String, Object> configMap = jsonParser.parseMap(configContent);

                File parentFolder;

                if (configMap.containsKey("parentFolder")) {
                    String parentFolderPath = DataManipulator.getStringValue(configMap, "parentFolder", "");

                    if (StringUtil.isEmptyString(parentFolderPath)) {
                        parentFolder = configFile.getParentFile();
                    } else {
                        Path path = Paths.get(parentFolderPath);
                        if (path.isAbsolute()) {
                            parentFolder = new File(parentFolderPath);
                        } else {
                            parentFolder = new File(configFile.getParent(), parentFolderPath);
                        }
                    }
                } else {
                    parentFolder = configFile.getParentFile();
                }

                return DependencyConfig.fromMap(configMap, parentFolder);
            } else {
                return null;
            }
        } catch (IOException e) {
            LOGGER.error(String.format("Error parsing config file (%s)", configFile.getAbsolutePath()));
            return null;
        }
    }

    public DependencyConfig parseConfig(String configContent) {
        try {
            CharacterStreamHandler hdl = new CharacterStreamHandler(configContent);

            String actualConfigContent = readConfigContent(hdl);

            hdl.close();

            if (actualConfigContent != null) {
                JsonParser jsonParser = JsonParser.getInstance();

                Map<String, Object> configMap = jsonParser.parseMap(actualConfigContent);

                File parentFolder, currentFolder = new File(".");

                if (configMap.containsKey("parentFolder")) {
                    String parentFolderPath = DataManipulator.getStringValue(configMap, "parentFolder", "");


                    if (StringUtil.isEmptyString(parentFolderPath)) {
                        parentFolder = currentFolder;
                    } else {
                        Path path = Paths.get(parentFolderPath);
                        if (path.isAbsolute()) {
                            parentFolder = new File(parentFolderPath);
                        } else {
                            parentFolder = new File(currentFolder, parentFolderPath);
                        }
                    }
                } else {
                    parentFolder = currentFolder;
                }

                return DependencyConfig.fromMap(configMap, parentFolder);
            } else {
                return null;
            }
        } catch (IOException e) {
            LOGGER.error(String.format("Error parsing config content:\n (%s)", configContent));
            return null;
        }
    }

    private String readConfigContent(CharacterStreamHandler hdl) throws IOException {
        String content = hdl.readUntil(CONFIG_START_TAG);
        if (hdl.hasNext()) {
            hdl.skip(CONFIG_START_TAG.length());
            return hdl.readUntil(CONFIG_END_TAG);
        } else {
            // No config tag found, return all the content read before.
            return content;
        }
    }
}
