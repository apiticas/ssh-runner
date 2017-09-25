package ro.b1nnar.projects.sshrunner.ssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import static com.google.common.base.Preconditions.checkNotNull;

public class Shell {

    private Channel channel;

    public Shell() {
        channel = null;
    }

    public boolean isConnected() {
        return channel != null && channel.isConnected();
    }

    public void connect(String hostname, String username, String password, JTextArea textArea) {
        try {
            Session session = new JSch().getSession(username, hostname);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setUserInfo(new MyUserInfo());

            session.connect(30000);

            channel = session.openChannel("shell");
            ((ChannelShell) channel).setPtyType("dumb");
            channel.setInputStream(null);
            channel.setOutputStream(appendNewLine(new JTextAreaOutputStream(textArea)));

            channel.connect(30000);

        } catch (Exception e) {
            PrintWriter printWriter = new PrintWriter(appendNewLine(new JTextAreaOutputStream(textArea)), true);
            e.printStackTrace(printWriter);
        }
    }

    public void disconnect() {
        if (channel != null) {
            channel.disconnect();
        }
    }

    private static OutputStream appendNewLine(OutputStream outputStream) {
        checkNotNull(outputStream, "Output stream is null");

        try {
            outputStream.write(System.getProperty("line.separator").getBytes());
        } catch (IOException e) {
        }

        return outputStream;
    }

    private class MyUserInfo implements UserInfo, UIKeyboardInteractive {

        @Override
        public String getPassword() {
            return null;
        }

        @Override
        public boolean promptYesNo(String message) {
            Object[] options = {"yes", "no"};
            int foo = JOptionPane.showOptionDialog(null, message, "Warning", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            return foo == 0;
        }

        @Override
        public String getPassphrase() {
            return null;
        }

        @Override
        public boolean promptPassphrase(String message) {
            return false;
        }

        @Override
        public boolean promptPassword(String message) {
            return false;
        }

        @Override
        public void showMessage(String message) {
            JOptionPane.showMessageDialog(null, message);
        }

        @Override
        public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt,
                                                  boolean[] echo) {
            return null;
        }
    }
}

/*
            JSch jsch = new JSch();
            Session session;

            String user = "sysadmin";
            String host = "demat.doc-process.com";
            String password = ".,Acce55";

            session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
//            session.setUserInfo(new MyUserInfo());
            session.connect(30000);

            String command = "/var/lib/demat-platform/transfer-starters/ge-invoices-transfer-starter-today";

            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            ((ChannelExec) channel).setPty(true);

            InputStream in = channel.getInputStream();
            OutputStream out = channel.getOutputStream();
            ((ChannelExec) channel).setErrStream(System.err);
            channel.connect(30000);

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
            channel.disconnect();
            session.disconnect();

        } catch (Exception e) {
            System.out.println(e);
        }
*/