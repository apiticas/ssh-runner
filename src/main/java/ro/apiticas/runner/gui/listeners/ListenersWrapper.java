package ro.apiticas.runner.gui.listeners;

import ro.apiticas.runner.gui.data.ServerData;
import ro.apiticas.runner.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ListenersWrapper {

    private JPanel mainPanel;
    private JList serversList;
    private List<ServerData> servers;

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
            serversList.setSelectedValue(servers.get(servers.size() - 1).getName(), true);
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

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void setMainPanel(JPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public JList getServersList() {
        return serversList;
    }

    public void setServersList(JList serversList) {
        this.serversList = serversList;
    }

    public List<ServerData> getServers() {
        return servers;
    }

    public void setServers(List<ServerData> servers) {
        this.servers = servers;
    }
}