package ro.b1nnar.projects.sshrunner.ssh;

import javax.swing.JTextArea;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class extends from OutputStream to redirect output to a JTextArrea
 */
public class JTextAreaOutputStream extends OutputStream {

    private JTextArea textArea;

    public JTextAreaOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        // redirects data to the text area
        textArea.append(String.valueOf((char) b));
        // scrolls the text area to the end of data
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}