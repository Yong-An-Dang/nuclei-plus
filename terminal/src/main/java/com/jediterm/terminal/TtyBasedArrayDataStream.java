package com.jediterm.terminal;

import com.jediterm.terminal.util.CharUtils;
import com.jediterm.typeahead.Ascii;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Takes data from and sends it back to TTY input and output streams via {@link TtyConnector}
 */
public class TtyBasedArrayDataStream extends ArrayTerminalDataStream {
    private final TtyConnector myTtyConnector;
    private final @Nullable Runnable myOnBeforeBlockingWait;

    public TtyBasedArrayDataStream(final TtyConnector ttyConnector, final @Nullable Runnable onBeforeBlockingWait) {
        super(new char[1024], 0, 0);
        myTtyConnector = ttyConnector;
        myOnBeforeBlockingWait = onBeforeBlockingWait;
    }

    public TtyBasedArrayDataStream(final TtyConnector ttyConnector) {
        super(new char[1024], 0, 0);
        myTtyConnector = ttyConnector;
        myOnBeforeBlockingWait = null;
    }

    private void fillBuf() throws IOException {
        myOffset = 0;

        if (!myTtyConnector.ready() && myOnBeforeBlockingWait != null) {
            myOnBeforeBlockingWait.run();
        }

        myLength = myTtyConnector.read(myBuf, myOffset, myBuf.length);
        // TODO 输入的字符不进行替换
        replace63();

        if (myLength <= 0) {
            myLength = 0;
            throw new EOF();
        }
    }

    private void replace63() {
        /**
         * ?[1;92m
         *
         * dn.com,?[
         * 96m*.baiducontent.com,*.hao222.com,*.
         */
        String pattern = "(\\?)(\\[\\d+m|\\[\\d+;\\d+m|\\[.{6}+m)";
        String content = new String(myBuf);
        Pattern p = Pattern.compile(pattern, Pattern.MULTILINE | Pattern.DOTALL);
        Matcher m = p.matcher(content);
        String newStr = m.replaceAll((char) Ascii.ESC + "$2");
        myBuf = newStr.toCharArray();
    }

    public char getChar() throws IOException {
        if (myLength == 0) {
            fillBuf();
        }
        return super.getChar();
    }

    public String readNonControlCharacters(int maxChars) throws IOException {
        if (myLength == 0) {
            fillBuf();
        }

        return super.readNonControlCharacters(maxChars);
    }

    @Override
    public String toString() {
        return CharUtils.toHumanReadableText(new String(myBuf, myOffset, myLength));
    }
}
