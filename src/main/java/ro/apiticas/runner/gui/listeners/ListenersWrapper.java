package ro.apiticas.runner.gui.listeners;

import ro.apiticas.runner.gui.UIComponent;
import ro.apiticas.runner.gui.data.ServerData;
import ro.apiticas.runner.gui.utils.GuiUtils;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
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

public class ListenersWrapper {

    private Map<UIComponent, JComponent> uiComps;
    private List<ServerData> servers;

    public ListenersWrapper(Map<UIComponent, JComponent> uiComps, List<ServerData> servers) {
        this.uiComps = checkNotNull(uiComps, "UI components map is null");
        this.servers = checkNotNull(servers, "Server data list is null");
    }

    public class AddServerButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JPanel mainPanel = (JPanel) uiComps.get(MAIN_PANEL);
            JList serversList = (JList) uiComps.get(SERVERS_LIST);

            String newServerName = (String) JOptionPane.showInputDialog(mainPanel, "Set the name of the new server",
                    "New server", JOptionPane.QUESTION_MESSAGE, null, null, "New server " + servers.size());

            if (newServerName == null || newServerName.trim().isEmpty()) {
                return;
            }

            if (GuiUtils.serverNameExists(servers, newServerName)) {
                JOptionPane.showMessageDialog(mainPanel, "A server with the same name already exists",
                        "Server name exists", JOptionPane.ERROR_MESSAGE);
                return;
            }

            serversList.setListData(GuiUtils.getServersNames(GuiUtils.addNewServer(servers, newServerName)));
            serversList.setSelectedValue(newServerName, true);
        }
    }

    public class RemoveServerButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JPanel mainPanel = (JPanel) uiComps.get(MAIN_PANEL);
            JList serversList = (JList) uiComps.get(SERVERS_LIST);

            String serverToDelete = (String) serversList.getSelectedValue();

            if (serverToDelete == null) {
                return;
            }

            int reallyDelete = JOptionPane.showConfirmDialog(mainPanel,
                    "Really delete server '" + serverToDelete + "'?", "Confirm delete",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (reallyDelete != 0) {
                return;
            }

            serversList.setListData(GuiUtils.getServersNames(GuiUtils.removeServer(servers, serverToDelete)));
            if (!servers.isEmpty()) {
                serversList.setSelectedValue(servers.get(servers.size() - 1).getName(), true);
            }
        }
    }

    public class EditServerButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JPanel mainPanel = (JPanel) uiComps.get(MAIN_PANEL);
            JList serversList = (JList) uiComps.get(SERVERS_LIST);

            String serverToEdit = (String) serversList.getSelectedValue();

            if (serverToEdit == null) {
                return;
            }

            String updatedServerName = (String) JOptionPane.showInputDialog(mainPanel,
                    "Set the new name of the server", "Rename server",
                    JOptionPane.QUESTION_MESSAGE, null, null, serverToEdit);

            if (updatedServerName == null || updatedServerName.trim().isEmpty()) {
                return;
            }

            if (GuiUtils.serverNameExists(servers, updatedServerName)) {
                JOptionPane.showMessageDialog(mainPanel, "A server with the same name already exists",
                        "Server name exists", JOptionPane.ERROR_MESSAGE);
                return;
            }

            serversList.setListData(GuiUtils.getServersNames(GuiUtils.renameServer(servers, serverToEdit,
                    updatedServerName)));
        }
    }

    public class ServersListSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent event) {
            JList serversList = (JList) uiComps.get(SERVERS_LIST);
            JTabbedPane tabbedPanel = (JTabbedPane) uiComps.get(TABBED_PANEL);
            JTextField hostnameField = (JTextField) uiComps.get(HOSTNAME_FIELD);
            JTextField usernameField = (JTextField) uiComps.get(USERNAME_FIELD);
            JTextField passwordField = (JTextField) uiComps.get(PASSWORD_FIELD);
            JButton connectButton = (JButton) uiComps.get(CONNECT_BUTTON);
            JButton disconnectButton = (JButton) uiComps.get(DISCONNECT_BUTTON);
            JLabel connectionStatusLabel = (JLabel) uiComps.get(CONNECTION_STATUS_LABEL);

            String serverName = (String) serversList.getSelectedValue();
            if (serverName == null) {
                return;
            }

            tabbedPanel.setVisible(true);
            ServerData server = GuiUtils.getServerByName(servers, serverName);

            hostnameField.setText(server.getHostname());
            usernameField.setText(server.getUsername());
            passwordField.setText(server.getPassword());
            ((JTextArea) uiComps.get(SHELL_AREA)).setText(GuiUtils.shellLinesToText(server.getShellLines()));
            connectButton.setEnabled(!server.getShell().isConnected());
            disconnectButton.setEnabled(server.getShell().isConnected());
            connectionStatusLabel.setText(server.getShell().isConnected() ? "Connected" : "Disconnected");
        }
    }

    public class ConnectButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JList serversList = (JList) uiComps.get(SERVERS_LIST);
            JButton connectButton = (JButton) uiComps.get(CONNECT_BUTTON);
            JButton disconnectButton = (JButton) uiComps.get(DISCONNECT_BUTTON);
            JLabel connectionStatusLabel = (JLabel) uiComps.get(CONNECTION_STATUS_LABEL);

            ServerData selectedServer = GuiUtils.getServerByName(servers, (String) serversList.getSelectedValue());

            selectedServer.getShell().connect(
                    selectedServer.getHostname(), selectedServer.getUsername(), selectedServer.getPassword(),
                    (JTextArea) uiComps.get(SHELL_AREA)
            );

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            selectedServer.setShellLines(GuiUtils.textToShellLines(((JTextArea) uiComps.get(SHELL_AREA)).
                    getText()));

            connectButton.setEnabled(!selectedServer.getShell().isConnected());
            disconnectButton.setEnabled(selectedServer.getShell().isConnected());
            connectionStatusLabel.setText(selectedServer.getShell().isConnected() ? "Connected" : "Disconnected");

            GuiUtils.updateServer(servers, selectedServer);
        }
    }

    public class DisconnectButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JList serversList = (JList) uiComps.get(SERVERS_LIST);
            JButton connectButton = (JButton) uiComps.get(CONNECT_BUTTON);
            JButton disconnectButton = (JButton) uiComps.get(DISCONNECT_BUTTON);
            JLabel connectionStatusLabel = (JLabel) uiComps.get(CONNECTION_STATUS_LABEL);
            ServerData selectedServer = GuiUtils.getServerByName(servers, (String) serversList.getSelectedValue());

            selectedServer.getShell().disconnect();

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            connectButton.setEnabled(!selectedServer.getShell().isConnected());
            disconnectButton.setEnabled(selectedServer.getShell().isConnected());
            connectionStatusLabel.setText(selectedServer.getShell().isConnected() ? "Connected" : "Disconnected");

            GuiUtils.updateServer(servers, selectedServer);
        }
    }

    public class InputFieldsDocumentListener implements DocumentListener {

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

            JList serversList = (JList) uiComps.get(SERVERS_LIST);
            JTextField hostnameField = (JTextField) uiComps.get(HOSTNAME_FIELD);
            JTextField usernameField = (JTextField) uiComps.get(USERNAME_FIELD);
            JTextField passwordField = (JTextField) uiComps.get(PASSWORD_FIELD);

            JTextField textField = (JTextField) documentEvent.getDocument().getProperty(DOCUMENT_SOURCE);
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
}