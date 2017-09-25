package ro.b1nnar.projects.sshrunner.gui;

import ro.b1nnar.projects.sshrunner.gui.data.ServerData;
import ro.b1nnar.projects.sshrunner.gui.listeners.ListenersWrapper;
import ro.b1nnar.projects.sshrunner.gui.utils.GuiUtils;

import javax.swing.JButton;
import javax.swing.JComponent;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunnerUI {

    protected JPanel mainPanel;

    private JPanel leftPanel;
    private JScrollPane serversListScrollPane;
    private JList serversList;
    private JButton addButton;
    private JButton removeButton;
    private JButton editButton;

    private JPanel rightPanel;
    private JTabbedPane tabbedPanel;
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

    private List<ServerData> servers;

    public RunnerUI() {
        createUIComponents();
    }

    private void createUIComponents() {

        servers = new ArrayList<ServerData>();

        Map<UIComponent, JComponent> uiComponents = new HashMap<>();
        uiComponents.put(UIComponent.MAIN_PANEL, mainPanel);
        uiComponents.put(UIComponent.SERVERS_LIST, serversList);
        uiComponents.put(UIComponent.TABBED_PANEL, tabbedPanel);
        uiComponents.put(UIComponent.HOSTNAME_FIELD, hostnameField);
        uiComponents.put(UIComponent.USERNAME_FIELD, usernameField);
        uiComponents.put(UIComponent.PASSWORD_FIELD, passwordField);
        uiComponents.put(UIComponent.CONNECT_BUTTON, connectButton);
        uiComponents.put(UIComponent.DISCONNECT_BUTTON, disconnectButton);
        uiComponents.put(UIComponent.CONNECTION_STATUS_LABEL, connectionStatusLabel);
        uiComponents.put(UIComponent.SHELL_AREA, shellArea);

        ListenersWrapper listenersWrapper = new ListenersWrapper(uiComponents, servers);

        addButton.addActionListener(listenersWrapper.new AddServerButtonActionListener());
        removeButton.addActionListener(listenersWrapper.new RemoveServerButtonActionListener());
        editButton.addActionListener(listenersWrapper.new EditServerButtonActionListener());
        serversList.addListSelectionListener(listenersWrapper.new ServersListSelectionListener());
        connectButton.addActionListener(listenersWrapper.new ConnectButtonActionListener());
        disconnectButton.addActionListener(listenersWrapper.new DisconnectButtonActionListener());
        DocumentListener documentListener = listenersWrapper.new InputFieldsDocumentListener();

        hostnameField.getDocument().putProperty(UIComponent.DOCUMENT_SOURCE, hostnameField);
        hostnameField.getDocument().addDocumentListener(documentListener);
        usernameField.getDocument().putProperty(UIComponent.DOCUMENT_SOURCE, usernameField);
        usernameField.getDocument().addDocumentListener(documentListener);
        passwordField.getDocument().putProperty(UIComponent.DOCUMENT_SOURCE, passwordField);
        passwordField.getDocument().addDocumentListener(documentListener);

        serversList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        serversList.setListData(GuiUtils.getServersNames(servers));

        tabbedPanel.setVisible(false);
        shellArea.setEditable(false);
        disconnectButton.setEnabled(false);
    }

}
