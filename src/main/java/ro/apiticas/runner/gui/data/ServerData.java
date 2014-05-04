package ro.apiticas.runner.gui.data;

import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class ServerData {

    private String name;

    private String hostname;
    private String username;
    private String password;

    private List<String> shellLines;
    private List<String> commands;

    private boolean connectionStatus;

    public ServerData(String name) {
        this.name = checkNotNull(name, "Server name is null");

        hostname = "";
        username = "";
        password = "";

        shellLines = new ArrayList<String>();
        commands = new ArrayList<String>();

        connectionStatus = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getShellLines() {
        return shellLines;
    }

    public void setShellLines(List<String> shellLines) {
        this.shellLines = shellLines;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public boolean connectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(boolean connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerData that = (ServerData) o;

        if (!name.equals(that.name)) return false;
        if (hostname != null ? !hostname.equals(that.hostname) : that.hostname != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (hostname != null ? hostname.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("serverName", name)
                .add("hostname", hostname)
                .add("username", username)
                .toString();
    }
}
