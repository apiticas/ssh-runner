package ro.apiticas.runner.ssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import javax.swing.JTextArea;
import java.io.PrintWriter;

public class Shell {

    public Channel connect(String hostname, String username, String password, JTextArea textArea) {

        Channel channel = null;

        try {
            JSch jsch = new JSch();

            Session session = null;
            session = jsch.getSession(username, hostname, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setUserInfo(new MyUserInfo());

            session.connect(30000);

            channel = session.openChannel("shell");
            ((ChannelShell) channel).setPtyType("dumb");

//            channel.setInputStream(System.in);
            channel.setInputStream(null);
            channel.setOutputStream(new JTextAreaOutputStream(textArea));
            channel.connect(30000);

        } catch (Exception e) {
            e.printStackTrace(new PrintWriter(new JTextAreaOutputStream(textArea), true));
        }

        return channel;
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