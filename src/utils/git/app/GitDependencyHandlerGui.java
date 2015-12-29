package utils.git.app;

import info.clearthought.layout.TableLayout;
import utils.git.data.DependencyConfig;
import utils.git.output.OutputHandler;
import utils.git.parser.ConfigParser;
import utils.string.StringUtil;
import utils.ui.GuiUtils;
import utils.ui.TableLayoutUtil;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2015-01-26
 * Time: 09:43
 */

public class GitDependencyHandlerGui extends GitDependencyHandlerBase {
    public static final int INPUT_TABLE_ROW_HEIGHT = 35;
    private static GitDependencyHandlerGui instance;

    public static GitDependencyHandlerGui getInstance() {
        if (instance == null) {
            instance = new GitDependencyHandlerGui();
        }

        return instance;
    }

    public static void process() {
        getInstance().showUI();
    }

    private JFrame appFrame;
    private JLabel dependencyLocalLabel, remoteLabel, targetLabel, branchLabel, dependencyRemoteLabel;
    private JTextField dependencyLocalField, remoteField, targetPathField, branchField, dependencyRemoteField;

    private JTextArea resultArea;
    private JScrollPane resultScrollPane;
    private JButton dependencyLocalBrowseButton, targetBrowseButton,
        processLocalButton, resetLocalInputButton,
        processRemoteButton, resetRemoteInputButton,
            clearLogButton;
    private JFileChooser fIleChooser, folderChooser;
    private JPanel inputPanel, localInputPanel, localButtonPanel, remoteInputPanel, remoteButtonPanel;

    private TableLayout localTableLayout, remoteTableLayout;

    private JTextAreaOutputHandler textAreaOutputHandler = new JTextAreaOutputHandler();

    private GitDependencyHandlerGui() {
        super(new JTextAreaOutputHandler());
        initializeUI();
        enableAction();
    }

    private void initializeUI() {
        GuiUtils.changeLookAndFeel("Nimbus");

        this.textAreaOutputHandler = (JTextAreaOutputHandler) outputHandler;

        this.appFrame = new JFrame("Git dependency Grabber");
        this.appFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        fIleChooser = new JFileChooser(".");
        folderChooser = new JFileChooser(".");
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        this.dependencyLocalLabel = new JLabel("Dependency file");

        this.dependencyLocalField = new JTextField();
        this.dependencyLocalField.setEditable(false);
        this.dependencyLocalBrowseButton = new JButton("Browse");
        this.processLocalButton = new JButton("Start process");
        this.resetLocalInputButton = new JButton("Reset");

        this.targetLabel = new JLabel("Target path");
        this.remoteLabel = new JLabel("Remote URL");
        this.branchLabel = new JLabel("Default branch");
        this.dependencyRemoteLabel = new JLabel("Dependency file");

        this.targetPathField = new JTextField();
        this.targetPathField.setEditable(false);

        this.remoteField = new JTextField();
        this.branchField = new JTextField();
        this.dependencyRemoteField = new JTextField();

        this.targetBrowseButton = new JButton("Browse");
        this.processRemoteButton = new JButton("Start process");
        this.resetRemoteInputButton = new JButton("Reset");

        this.clearLogButton = new JButton("Clear log");

        this.localTableLayout = new TableLayout();
        this.remoteTableLayout = new TableLayout();
        double[] columns = {100, 600, 100};

        this.remoteTableLayout.setColumn(columns);
        this.localTableLayout.setColumn(columns);

        this.inputPanel = new JPanel(new FlowLayout());
        this.localInputPanel = new JPanel(localTableLayout);
        this.remoteInputPanel = new JPanel(remoteTableLayout);
        this.localButtonPanel = new JPanel(new FlowLayout());
        this.remoteButtonPanel = new JPanel(new FlowLayout());

        this.localInputPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Local dependency file"));
        this.remoteInputPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Remote dependency file"));

        GuiUtils.addComponents(localButtonPanel, processLocalButton, resetLocalInputButton);
        GuiUtils.addComponents(remoteButtonPanel, processRemoteButton, resetRemoteInputButton);

        localTableLayout.insertRow(0, INPUT_TABLE_ROW_HEIGHT);
        addTableLayoutComponent(localInputPanel, dependencyLocalLabel, 0, 1, 0, 1, TableLayout.CENTER, TableLayout.CENTER);
        addTableLayoutComponent(localInputPanel, dependencyLocalField, 0, 1, 1, 1, TableLayout.FULL, TableLayout.CENTER);
        addTableLayoutComponent(localInputPanel, dependencyLocalBrowseButton, 0, 1, 2, 1, TableLayout.CENTER, TableLayout.CENTER);
        localTableLayout.insertRow(1, INPUT_TABLE_ROW_HEIGHT);
        addTableLayoutComponent(localInputPanel, localButtonPanel, 1, 1, 0, 3, TableLayout.CENTER, TableLayout.CENTER);

        remoteTableLayout.insertRow(0, INPUT_TABLE_ROW_HEIGHT);
        addTableLayoutComponent(remoteInputPanel, remoteLabel, 0, 1, 0, 1, TableLayout.CENTER, TableLayout.CENTER);
        addTableLayoutComponent(remoteInputPanel, remoteField, 0, 1, 1, 1, TableLayout.FULL, TableLayout.CENTER);

        remoteTableLayout.insertRow(1, INPUT_TABLE_ROW_HEIGHT);
        addTableLayoutComponent(remoteInputPanel, targetLabel, 1, 1, 0, 1, TableLayout.CENTER, TableLayout.CENTER);
        addTableLayoutComponent(remoteInputPanel, targetPathField, 1, 1, 1, 1, TableLayout.FULL, TableLayout.CENTER);
        addTableLayoutComponent(remoteInputPanel, targetBrowseButton, 1, 1, 2, 1, TableLayout.CENTER, TableLayout.CENTER);

        remoteTableLayout.insertRow(2, INPUT_TABLE_ROW_HEIGHT);
        addTableLayoutComponent(remoteInputPanel, branchLabel, 2, 1, 0, 1, TableLayout.CENTER, TableLayout.CENTER);
        addTableLayoutComponent(remoteInputPanel, branchField, 2, 1, 1, 1, TableLayout.FULL, TableLayout.CENTER);

        remoteTableLayout.insertRow(3, INPUT_TABLE_ROW_HEIGHT);
        addTableLayoutComponent(remoteInputPanel, dependencyRemoteLabel, 3, 1, 0, 1, TableLayout.CENTER, TableLayout.CENTER);
        addTableLayoutComponent(remoteInputPanel, dependencyRemoteField, 3, 1, 1, 1, TableLayout.FULL, TableLayout.CENTER);

        remoteTableLayout.insertRow(4, INPUT_TABLE_ROW_HEIGHT);
        addTableLayoutComponent(remoteInputPanel, remoteButtonPanel, 4, 1, 0, 3, TableLayout.CENTER, TableLayout.CENTER);

        this.resultArea = textAreaOutputHandler.getTextArea();
        this.resultScrollPane = new JScrollPane(this.resultArea);

        this.inputPanel.setPreferredSize(new Dimension(825, 320));

        GuiUtils.addComponents(inputPanel, localInputPanel, remoteInputPanel);

        Container container = this.appFrame.getContentPane();
        container.setLayout(new BorderLayout());
        container.add(inputPanel, BorderLayout.NORTH);
        container.add(resultScrollPane, BorderLayout.CENTER);
        container.add(clearLogButton, BorderLayout.SOUTH);
    }

    private void addTableLayoutComponent(
        Container c, Component comp, int row, int rSpan, int col, int cSpan, int hAlign, int vAlign
    ) {
        TableLayoutUtil.addComponent(c, comp, row, rSpan, col, cSpan, hAlign, vAlign);
    }

    private void showUI() {
        resetAll();

        GuiUtils.showFrameAtCenter(appFrame, true, 850, 650);
    }

    private void resetLocalInputs() {
        this.dependencyLocalField.setText("");
    }

    private void resetRemoteInputs() {
        this.remoteField.setText("");
        this.targetPathField.setText("");
        this.branchField.setText("");
        this.dependencyRemoteField.setText("");
    }

    private void resetAll() {
        resetLocalInputs();
        resetRemoteInputs();

        this.resultArea.setText("");
    }

    private void appendResult(String fmt, Object ... params) {
        resultArea.append(String.format(fmt, params));
        resultArea.append("\n");
    }

    private void enableAction() {
        this.dependencyLocalBrowseButton.addActionListener(
            e -> {
                int opt = fIleChooser.showOpenDialog(appFrame);

                if (opt == JFileChooser.APPROVE_OPTION) {
                    File configFile = fIleChooser.getSelectedFile();

                    dependencyLocalField.setText(configFile.getAbsolutePath());
                } else {
                    // DO NOTHING
                }
            }
        );

        this.processLocalButton.addActionListener(
            e -> {
                new Thread() {
                    @Override
                    public void run() {
                        File configFile = new File(dependencyLocalField.getText());

                        if (configFile.exists()) {
                            processLocalButton.setEnabled(false);
                            resetLocalInputButton.setEnabled(false);

                            doMain(configFile);

                            processLocalButton.setEnabled(true);
                            resetLocalInputButton.setEnabled(true);
                        } else {
                            appendResult("Config file " + configFile.getName() + " does not exists!");
                        }
                    }
                }.start();
            }
        );

        resetLocalInputButton.addActionListener(
            e -> {
                resetLocalInputs();
            }
        );

        this.targetBrowseButton.addActionListener(
            e -> {
                int opt = folderChooser.showOpenDialog(appFrame);

                if (opt == JFileChooser.APPROVE_OPTION) {
                    File targetPath = folderChooser.getSelectedFile();

                    targetPathField.setText(targetPath.getAbsolutePath());
                }
            }
        );

        this.processRemoteButton.addActionListener(
            e -> {
                String remoteUrl = remoteField.getText(), targetPath = targetPathField.getText(),
                        branch = branchField.getText(), dependencyFile = dependencyRemoteField.getText();

                if (StringUtil.isEmptyString(remoteUrl) || StringUtil.isEmptyString(targetPath) || StringUtil.isEmptyString(branch) || StringUtil.isEmptyString(dependencyFile)) {
                    appendResult("Missing required parameters, process terminated!");
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            processRemoteButton.setEnabled(false);
                            resetRemoteInputButton.setEnabled(false);
                            doMain(targetPath, remoteUrl, branch, dependencyFile);
                            processRemoteButton.setEnabled(true);
                            resetRemoteInputButton.setEnabled(true);
                        }
                    }.start();
                }
            }
        );

        this.resetRemoteInputButton.addActionListener(
            e -> {
                resetRemoteInputs();
            }
        );

        this.clearLogButton.addActionListener(
            e -> {
                textAreaOutputHandler.getTextArea().setText("");
            }
        );
    }

    public static class JTextAreaOutputHandler implements OutputHandler {
        private JTextArea textArea;

        public JTextAreaOutputHandler() {
            textArea = new JTextArea();
            ((DefaultCaret)textArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
            textArea.setEditable(false);
        }

        public JTextArea getTextArea() {
            return textArea;
        }

        private void appendResult(String msg) {
            textArea.append(msg);
            textArea.append("\n");
        }

        @Override
        public void debug(String msg) {
            appendResult(msg);
        }

        @Override
        public void info(String msg) {
            appendResult(msg);
        }

        @Override
        public void error(String msg, Throwable e) {
            appendResult(msg);
            StackTraceElement[] traceElements = e.getStackTrace();

            for (StackTraceElement traceElement : traceElements) {
                appendResult(traceElement.toString());
            }
        }
    }
}
