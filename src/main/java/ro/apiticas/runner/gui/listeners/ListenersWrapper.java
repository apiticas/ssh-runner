package ro.apiticas.runner.gui.listeners;

import ro.apiticas.runner.gui.MainUI;
import ro.apiticas.runner.gui.data.ServerData;
import ro.apiticas.runner.gui.utils.GuiUtils;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
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

import static com.google.common.base.Preconditions.checkNotNull;

public class ListenersWrapper {

    private JPanel mainPanel;

    private JList serversList;

    private JTabbedPane tabbedPane1;
    private JTextField hostnameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton connectButton;
    private JButton disconnectButton;
    private JLabel connectionStatusLabel;
    private JTextArea shellArea;

    private List<ServerData> servers;

//    private Map<UIComponent, JComponent> uiComps;

/*    public ListenersWrapper(Map<UIComponent, JComponent> uiComps) {

        this.uiComps = checkNotNull(uiComps, "UI components map is null");

    }*/

    public class AddServerButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
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
    }

    public class ConnectButtonActionListener implements ActionListener {

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
    }

    public class DisconnectButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            ServerData selectedServer = GuiUtils.getServerByName(servers, (String) serversList.getSelectedValue());

            selectedServer.getShell().disconnect();

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

            JTextField textField = (JTextField) documentEvent.getDocument().getProperty(MainUI.DOCUMENT_SOURCE);
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

    public void setMainPanel(JPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public void setServersList(JList serversList) {
        this.serversList = serversList;
    }

    public void setTabbedPane1(JTabbedPane tabbedPane1) {
        this.tabbedPane1 = tabbedPane1;
    }

    public void setHostnameField(JTextField hostnameField) {
        this.hostnameField = hostnameField;
    }

    public void setUsernameField(JTextField usernameField) {
        this.usernameField = usernameField;
    }

    public void setPasswordField(JPasswordField passwordField) {
        this.passwordField = passwordField;
    }

    public void setDisconnectButton(JButton disconnectButton) {
        this.disconnectButton = disconnectButton;
    }

    public void setConnectButton(JButton connectButton) {
        this.connectButton = connectButton;
    }

    public void setConnectionStatusLabel(JLabel connectionStatusLabel) {
        this.connectionStatusLabel = connectionStatusLabel;
    }

    public void setShellArea(JTextArea shellArea) {
        this.shellArea = shellArea;
    }

    public void setServers(List<ServerData> servers) {
        this.servers = servers;
    }
}