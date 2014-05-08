package ro.apiticas.runner.gui;

import ro.apiticas.runner.gui.data.ServerData;
import ro.apiticas.runner.gui.listeners.ListenersWrapper;
import ro.apiticas.runner.gui.utils.GuiUtils;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentListener;
import java.util.ArrayList;
import java.util.List;

public class MainUI {

    public static final String DOCUMENT_SOURCE = "source";

    private JPanel mainPanel;

    private JPanel leftPanel;
    private JScrollPane serversListScrollPane;
    private JList serversList;
    private JButton addButton;
    private JButton removeButton;
    private JButton editButton;

    private JPanel rightPanel;
    private JTabbedPane tabbedPane1;
    private JPanel connectionPanel;
    private JPanel credentialsPanel;
    private JTextField hostnameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton connectButton;
    private JButton disconnectButton;
    private JLabel connectionStatusLabel;
    private JPanel shellPanel;
    private JTextArea shellArea;
    private JPanel commandsPanel;
    private JPanel outputPanel;
    private JScrollPane shellAreaScrollPane;

    private ListenersWrapper listenersWrapper;
    private List<ServerData> servers;

    public MainUI() {
        createUIComponents();
    }

    private void createUIComponents() {

        servers = new ArrayList<ServerData>();

        serversList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        serversList.setListData(GuiUtils.getServersNames(servers));

        tabbedPane1.setVisible(false);

        listenersWrapper = new ListenersWrapper();
        listenersWrapper.setMainPanel(mainPanel);
        listenersWrapper.setServersList(serversList);
        listenersWrapper.setTabbedPane1(tabbedPane1);
        listenersWrapper.setHostnameField(hostnameField);
        listenersWrapper.setUsernameField(usernameField);
        listenersWrapper.setPasswordField(passwordField);
        listenersWrapper.setConnectButton(connectButton);
        listenersWrapper.setDisconnectButton(disconnectButton);
        listenersWrapper.setConnectionStatusLabel(connectionStatusLabel);
        listenersWrapper.setShellArea(shellArea);
        listenersWrapper.setServers(servers);

        addButton.addActionListener(listenersWrapper.new AddServerButtonActionListener());
        removeButton.addActionListener(listenersWrapper.new RemoveServerButtonActionListener());
        editButton.addActionListener(listenersWrapper.new EditServerButtonActionListener());
        serversList.addListSelectionListener(listenersWrapper.new ServersListSelectionListener());
        connectButton.addActionListener(listenersWrapper.new ConnectButtonActionListener());
        disconnectButton.addActionListener(listenersWrapper.new DisconnectButtonActionListener());

        DocumentListener documentListener = listenersWrapper.new InputFieldsDocumentListener();

        hostnameField.getDocument().putProperty(DOCUMENT_SOURCE, hostnameField);
        usernameField.getDocument().putProperty(DOCUMENT_SOURCE, usernameField);
        passwordField.getDocument().putProperty(DOCUMENT_SOURCE, passwordField);

        hostnameField.getDocument().addDocumentListener(documentListener);
        usernameField.getDocument().addDocumentListener(documentListener);
        passwordField.getDocument().addDocumentListener(documentListener);

        shellArea.setEditable(false);
        disconnectButton.setEnabled(false);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainUI");
        frame.setContentPane(new MainUI().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }
}
