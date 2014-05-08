package ro.apiticas.runner.gui.utils;

import ro.apiticas.runner.gui.data.ServerData;

import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class GuiUtils {

    public static String[] getServersNames(List<ServerData> servers) {
        checkNotNull(servers, "Servers list is null");

        String[] serversNames = new String[servers.size()];
        for (int i = 0; i < servers.size(); i++) {
            serversNames[i] = servers.get(i).getName();
        }

        return serversNames;
    }

    public static ServerData getServerByName(List<ServerData> servers, String serverName) {
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

    public static boolean serverNameExists(List<ServerData> servers, String serverName) {
        checkNotNull(servers, "Servers list is null");
        checkNotNull(serverName, "Server name is null");

        return getServerByName(servers, serverName) != null;
    }

    public static List<ServerData> addNewServer(List<ServerData> servers, String serverName) {
        checkNotNull(servers, "Servers list is null");
        checkNotNull(serverName, "Server name is null");

        servers.add(new ServerData(serverName));
        return servers;
    }

    public static List<ServerData> removeServer(List<ServerData> servers, String serverName) {
        checkNotNull(servers, "Servers list is null");
        checkNotNull(serverName, "Server name is null");

        servers.remove(getServerByName(servers, serverName));
        return servers;
    }

    public static List<ServerData> renameServer(List<ServerData> servers, String oldServerName, String newServerName) {
        checkNotNull(servers, "Servers list is null");
        checkNotNull(oldServerName, "Old server name is null");
        checkNotNull(newServerName, "New server name is null");

        servers.get(servers.indexOf(getServerByName(servers, oldServerName))).setName(newServerName);
        return servers;
    }

    public static List<ServerData> updateServer(List<ServerData> servers, ServerData serverData) {
        checkNotNull(servers, "Servers list is null");
        checkNotNull(serverData, "Server data is null");

        servers.set(servers.indexOf(getServerByName(servers, serverData.getName())), serverData);
        return servers;
    }

    public static String shellLinesToText(List<String> lines) {
        checkNotNull(lines, "Shell lines list is null");

        StringBuilder stringBuilder = new StringBuilder();
        for (String line : lines) {
            stringBuilder.append(line + "\n");
        }

        return stringBuilder.toString();
    }

    public static List<String> textToShellLines(String text) {
        checkNotNull(text, "Shell text is null");

        String[] lines = text.split("\n");
        return Arrays.asList(lines);
    }
}