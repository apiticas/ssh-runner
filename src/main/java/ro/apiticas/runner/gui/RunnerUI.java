package ro.apiticas.runner.gui;

import ro.apiticas.runner.gui.data.ServerData;
import ro.apiticas.runner.gui.listeners.ListenersWrapper;
import ro.apiticas.runner.gui.utils.GuiUtils;

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

import static ro.apiticas.runner.gui.UIComponent.CONNECTION_STATUS_LABEL;
import static ro.apiticas.runner.gui.UIComponent.CONNECT_BUTTON;
import static ro.apiticas.runner.gui.UIComponent.DISCONNECT_BUTTON;
import static ro.apiticas.runner.gui.UIComponent.DOCUMENT_SOURCE;
import static ro.apiticas.runner.gui.UIComponent.HOSTNAME_FIELD;
import static ro.apiticas.runner.gui.UIComponent.MAIN_PANEL;
import static ro.apiticas.runner.gui.UIComponent.PASSWORD_FIELD;
import static ro.apiticas.runner.gui.UIComponent.SERVERS_LIST;
import static ro.apiticas.runner.gui.UIComponent.SHELL_AREA;
import static ro.apiticas.runner.gui.UIComponent.TABBED_PANEL;
import static ro.apiticas.runner.gui.UIComponent.USERNAME_FIELD;

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
        uiComponents.put(MAIN_PANEL, mainPanel);
        uiComponents.put(SERVERS_LIST, serversList);
        uiComponents.put(TABBED_PANEL, tabbedPanel);
        uiComponents.put(HOSTNAME_FIELD, hostnameField);
        uiComponents.put(USERNAME_FIELD, usernameField);
        uiComponents.put(PASSWORD_FIELD, passwordField);
        uiComponents.put(CONNECT_BUTTON, connectButton);
        uiComponents.put(DISCONNECT_BUTTON, disconnectButton);
        uiComponents.put(CONNECTION_STATUS_LABEL, connectionStatusLabel);
        uiComponents.put(SHELL_AREA, shellArea);

        ListenersWrapper listenersWrapper = new ListenersWrapper(uiComponents, servers);

        addButton.addActionListener(listenersWrapper.new AddServerButtonActionListener());
        removeButton.addActionListener(listenersWrapper.new RemoveServerButtonActionListener());
        editButton.addActionListener(listenersWrapper.new EditServerButtonActionListener());
        serversList.addListSelectionListener(listenersWrapper.new ServersListSelectionListener());
        connectButton.addActionListener(listenersWrapper.new ConnectButtonActionListener());
        disconnectButton.addActionListener(listenersWrapper.new DisconnectButtonActionListener());
        DocumentListener documentListener = listenersWrapper.new InputFieldsDocumentListener();

        hostnameField.getDocument().putProperty(DOCUMENT_SOURCE, hostnameField);
        hostnameField.getDocument().addDocumentListener(documentListener);
        usernameField.getDocument().putProperty(DOCUMENT_SOURCE, usernameField);
        usernameField.getDocument().addDocumentListener(documentListener);
        passwordField.getDocument().putProperty(DOCUMENT_SOURCE, passwordField);
        passwordField.getDocument().addDocumentListener(documentListener);

        serversList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        serversList.setListData(GuiUtils.getServersNames(servers));

        tabbedPanel.setVisible(false);
        shellArea.setEditable(false);
        disconnectButton.setEnabled(false);
    }

}
