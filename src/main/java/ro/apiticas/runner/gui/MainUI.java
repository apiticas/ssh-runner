package ro.apiticas.runner.gui;

import com.jcraft.jsch.Channel;
import ro.apiticas.runner.gui.data.ServerData;
import ro.apiticas.runner.ssh.JTextAreaOutputStream;
import ro.apiticas.runner.ssh.Shell;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
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

    private List<ServerData> servers;

    private Channel channel = null;

    public MainUI() {
        createUIComponents();
    }

    private void createUIComponents() {

        servers = new ArrayList<ServerData>();

        // Test data
        ServerData firstServer = new ServerData("First");
        firstServer.setHostname("localhost");
        firstServer.setUsername("alexandru");
        firstServer.setPassword("password");
        servers.add(firstServer);
        servers.add(new ServerData("Second"));

        tabbedPane1.setVisible(false);

        serversList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        serversList.setListData(getServersNames(servers));

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newServerName = (String) JOptionPane.showInputDialog(mainPanel, "Set the name of the new server", "New server",
                        JOptionPane.QUESTION_MESSAGE, null, null, "New server " + servers.size());

                if (newServerName == null || newServerName.trim().isEmpty()) {
                    return;
                }

                if (serverNameExists(servers, newServerName)) {
                    JOptionPane.showMessageDialog(mainPanel, "A server with the same name already exists",
                            "Server name exists", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                serversList.setListData(getServersNames(addNewServer(servers, newServerName)));
                serversList.setSelectedValue(newServerName, true);
            }
        });

        removeButton.addActionListener(new ActionListener() {
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

                serversList.setListData(getServersNames(removeServer(servers, serverToDelete)));
                serversList.setSelectedValue(servers.get(servers.size() - 1).getName(), true);
            }
        });

        editButton.addActionListener(new ActionListener() {
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

                if (serverNameExists(servers, updatedServerName)) {
                    JOptionPane.showMessageDialog(mainPanel, "A server with the same name already exists",
                            "Server name exists", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                serversList.setListData(getServersNames(renameServer(servers, serverToEdit, updatedServerName)));
            }
        });

        serversList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                String serverName = (String) serversList.getSelectedValue();
                if (serverName == null) {
                    return;
                }

                tabbedPane1.setVisible(true);
                ServerData server = getServerByName(servers, serverName);

                hostnameField.setText(server.getHostname());
                usernameField.setText(server.getUsername());
                passwordField.setText(server.getPassword());
                shellArea.setText(shellLinesToText(server.getShellLines()));
                connectButton.setEnabled(!server.connectionStatus());
                disconnectButton.setEnabled(server.connectionStatus());
                connectionStatusLabel.setText(server.connectionStatus() ? "Connected" : "Disconnected");
            }
        });

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ServerData selectedServer = getServerByName(servers, (String) serversList.getSelectedValue());

                channel = new Shell().connect(selectedServer.getHostname(), selectedServer.getUsername(),
                        selectedServer.getPassword(), shellArea);

                selectedServer.setShellLines(textToShellLines(shellArea.getText()));

                if (channel != null && channel.isConnected()) {
                    connectionStatusLabel.setText("Connected");
                    connectButton.setEnabled(false);
                    disconnectButton.setEnabled(true);
                    selectedServer.setConnectionStatus(true);
                } else {
                    connectionStatusLabel.setText("Disconnected");
                    selectedServer.setConnectionStatus(false);
                }

                updateServer(servers, selectedServer);
            }
        });

        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ServerData selectedServer = getServerByName(servers, (String) serversList.getSelectedValue());

                if (channel == null) {
                    return;
                }

                try {
                    channel.disconnect();
                    channel.getSession().disconnect();
                } catch (Exception e) {
                    e.printStackTrace(new PrintWriter(new JTextAreaOutputStream(shellArea), true));
                }

                selectedServer.setShellLines(textToShellLines(shellArea.getText()));

                connectionStatusLabel.setText("Disconnected");
                connectButton.setEnabled(true);
                disconnectButton.setEnabled(false);
                selectedServer.setConnectionStatus(true);

                updateServer(servers, selectedServer);
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
            ServerData selectedServer = getServerByName(servers, (String) serversList.getSelectedValue());
            String newValue = textField.getText();

            if (textField.equals(hostnameField)) {
                selectedServer.setHostname(newValue);
            } else if (textField.equals(usernameField)) {
                selectedServer.setUsername(newValue);
            } else if (textField.equals(passwordField)) {
                selectedServer.setPassword(newValue);
            }
            updateServer(servers, selectedServer);
        }
    }

    private String[] getServersNames(List<ServerData> servers) {
        checkNotNull(servers, "Servers list is null");

        String[] serversNames = new String[servers.size()];
        for (int i = 0; i < servers.size(); i++) {
            serversNames[i] = servers.get(i).getName();
        }

        return serversNames;
    }

    private ServerData getServerByName(List<ServerData> servers, String serverName) {
        checkNotNull(servers, "Servers list is null");
        checkNotNull(serverName, "Server name is null");

        ServerData serverData = null;

        for (ServerData server : servers) {
            if (server.getName().equals(serverName)) {
                serverData = server;
                break;
            }
        }

        return serverData;
    }

    private boolean serverNameExists(List<ServerData> servers, String serverName) {
        checkNotNull(servers, "Servers list is null");
        checkNotNull(serverName, "Server name is null");

        return getServerByName(servers, serverName) != null;
    }

    private List<ServerData> addNewServer(List<ServerData> servers, String serverName) {
        checkNotNull(servers, "Servers list is null");
        checkNotNull(serverName, "Server name is null");

        servers.add(new ServerData(serverName));
        return servers;
    }

    private List<ServerData> removeServer(List<ServerData> servers, String serverName) {
        checkNotNull(servers, "Servers list is null");
        checkNotNull(serverName, "Server name is null");

        servers.remove(getServerByName(servers, serverName));
        return servers;
    }

    private List<ServerData> renameServer(List<ServerData> servers, String oldServerName, String newServerName) {
        checkNotNull(servers, "Servers list is null");
        checkNotNull(oldServerName, "Old server name is null");
        checkNotNull(newServerName, "New server name is null");

        servers.get(servers.indexOf(getServerByName(servers, oldServerName))).setName(newServerName);
        return servers;
    }

    private List<ServerData> updateServer(List<ServerData> servers, ServerData serverData) {
        checkNotNull(servers, "Servers list is null");
        checkNotNull(serverData, "Server data is null");

        servers.set(servers.indexOf(getServerByName(servers, serverData.getName())), serverData);
        return servers;
    }

    private String shellLinesToText(List<String> lines) {
        checkNotNull(lines, "Shell lines list is null");

        StringBuilder stringBuilder = new StringBuilder();
        for (String line : lines) {
            stringBuilder.append(line + "\n");
        }

        return stringBuilder.toString();
    }

    private List<String> textToShellLines(String text) {
        checkNotNull(text, "Shell text is null");

        String[] lines = text.split("\n");
        return Arrays.asList(lines);
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
