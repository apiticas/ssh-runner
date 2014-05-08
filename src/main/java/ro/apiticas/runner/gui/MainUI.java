package ro.apiticas.runner.gui;

import ro.apiticas.runner.gui.data.ServerData;
import ro.apiticas.runner.gui.listeners.ListenersWrapper;
import ro.apiticas.runner.gui.utils.GuiUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class MainUI {

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
        listenersWrapper.setServers(servers);

        addButton.addActionListener(listenersWrapper.new AddServerButtonActionListener());
        removeButton.addActionListener(listenersWrapper.new RemoveServerButtonActionListener());
        editButton.addActionListener(listenersWrapper.new EditServerButtonActionListener());

        serversList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                String serverName = (String) serversList.getSelectedValue();
                if (serverName == null) {
                    return;
                }

                tabbedPane1.setVisible(true);
                ServerData server = GuiUtils.getServerByName(servers, serverName);

                hostnameField.setText(server.getHostname());
                usernameField.setText(server.getUsername());
                passwordField.setText(server.getPassword());
                shellArea.setText(GuiUtils.shellLinesToText(server.getShellLines()));
                connectButton.setEnabled(!server.getShell().isConnected());
                disconnectButton.setEnabled(server.getShell().isConnected());
                connectionStatusLabel.setText(server.getShell().isConnected() ? "Connected" : "Disconnected");
            }
        });

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ServerData selectedServer = GuiUtils.getServerByName(servers, (String) serversList.getSelectedValue());

                selectedServer.getShell().connect(
                        selectedServer.getHostname(), selectedServer.getUsername(), selectedServer.getPassword(),
                        shellArea
                );

                selectedServer.setShellLines(GuiUtils.textToShellLines(shellArea.getText()));

                connectButton.setEnabled(!selectedServer.getShell().isConnected());
                disconnectButton.setEnabled(selectedServer.getShell().isConnected());
                connectionStatusLabel.setText(selectedServer.getShell().isConnected() ? "Connected" : "Disconnected");

                GuiUtils.updateServer(servers, selectedServer);
            }
        });

        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ServerData selectedServer = GuiUtils.getServerByName(servers, (String) serversList.getSelectedValue());

                selectedServer.getShell().disconnect();

                connectButton.setEnabled(!selectedServer.getShell().isConnected());
                disconnectButton.setEnabled(selectedServer.getShell().isConnected());
                connectionStatusLabel.setText(selectedServer.getShell().isConnected() ? "Connected" : "Disconnected");

                GuiUtils.updateServer(servers, selectedServer);
            }
        });

        DocumentListener documentListener = new InputFieldsDocumentListener();

        hostnameField.getDocument().putProperty("source", hostnameField);
        usernameField.getDocument().putProperty("source", usernameField);
        passwordField.getDocument().putProperty("source", passwordField);

        hostnameField.getDocument().addDocumentListener(documentListener);
        usernameField.getDocument().addDocumentListener(documentListener);
        passwordField.getDocument().addDocumentListener(documentListener);

        shellArea.setEditable(false);
        disconnectButton.setEnabled(false);
    }

    class InputFieldsDocumentListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent documentEvent) {
            updateModel(documentEvent);
        }

        @Override
        public void removeUpdate(DocumentEvent documentEvent) {
            updateModel(documentEvent);
        }

        @Override
        public void changedUpdate(DocumentEvent documentEvent) {
        }

        private void updateModel(DocumentEvent documentEvent) {
            checkNotNull(documentEvent, "Document event is null");

            JTextField textField = (JTextField) documentEvent.getDocument().getProperty("source");
            ServerData selectedServer = GuiUtils.getServerByName(servers, (String) serversList.getSelectedValue());
            String newValue = textField.getText();

            if (textField.equals(hostnameField)) {
                selectedServer.setHostname(newValue);
            } else if (textField.equals(usernameField)) {
                selectedServer.setUsername(newValue);
            } else if (textField.equals(passwordField)) {
                selectedServer.setPassword(newValue);
            }
            GuiUtils.updateServer(servers, selectedServer);
        }
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
