package utils.git.data;

import utils.data.DataManipulator;
import utils.string.StringUtil;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2015-01-23
 * Time: 18:02
 */
public class HostEntry {
    private String host, user, protocol;
    private int port;

    private String remoteUrl = null;

    /**
     * Construct HostEntry from map, currently no error checking use with caution
     *
     * @param configMap configuration map
     *
     * @return host entry
     */
    public static HostEntry fromMap(Map<String, Object> configMap) {
        String protocol, host, user;

        int port;

        protocol = DataManipulator.getStringValue(configMap, "protocol", "ssh");
        user = DataManipulator.getStringValue(configMap, "user", "");
        host = DataManipulator.getStringValue(configMap, "host", "");
        port = DataManipulator.getIntValue(
                configMap, "port", 10,
                // default ports, ssh: 22, https: 443, http: 80
                ("ssh".equals(protocol))? 22 : ("https".equals(protocol))? 443 : 80
        );

        if (host.equals("")) {
            // No host return null
            return null;
        } else {
            return new HostEntry(protocol, host, user, port);
        }
    }

    public HostEntry(String protocol, String host, String user, int port) {
        this.protocol = protocol;
        this.host = host;
        this.user = user;
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public int getPort() {
        return port;
    }

    public String buildRemoteUrl(String targetUrl) {
        if (remoteUrl == null) {
            if (protocol.equals("ssh")) {
                remoteUrl = protocol + "://" + user + "@" + host + ":" + port + "/" + targetUrl;
            } else {
                if (StringUtil.isEmptyString(user)) {
                    remoteUrl = protocol + "://" + host + ":" + port + "/" + targetUrl;
                } else {
                    remoteUrl = protocol + "://" + user + "@" + host + ":" + port + "/" + targetUrl;
                }
            }
        }
        return remoteUrl;
    }
}
