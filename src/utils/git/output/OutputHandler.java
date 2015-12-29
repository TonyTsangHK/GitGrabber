package utils.git.output;

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2015-04-11
 * Time: 09:56
 */
public interface OutputHandler {
    void debug(String msg);
    void info(String msg);
    void error(String msg, Throwable e);
}
