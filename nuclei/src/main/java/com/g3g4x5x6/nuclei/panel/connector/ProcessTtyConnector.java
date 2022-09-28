package com.g3g4x5x6.nuclei.panel.connector;

import com.jediterm.terminal.Questioner;
import com.jediterm.terminal.TtyConnector;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

@Slf4j
public abstract class ProcessTtyConnector implements TtyConnector {
    protected final InputStream myInputStream;
    protected final OutputStream myOutputStream;
    protected final InputStreamReader myReader;
    protected final Charset myCharset;
    private Dimension myPendingTermSize;
    private final Process myProcess;

    public ProcessTtyConnector(@NotNull Process process, @NotNull Charset charset) {
        this.myOutputStream = process.getOutputStream();
        this.myCharset = charset;
        this.myInputStream = process.getInputStream();
        this.myReader = new InputStreamReader(this.myInputStream, this.myCharset);
        this.myProcess = process;
    }

    @NotNull
    public Process getProcess() {
        return this.myProcess;
    }

    public void resize(@NotNull Dimension termWinSize) {
        this.setPendingTermSize(termWinSize);
        if (this.isConnected()) {
            this.resizeImmediately();
            this.setPendingTermSize((Dimension) null);
        }

    }

    /**
     * @deprecated
     */
    @Deprecated
    protected void resizeImmediately() {
    }

    public abstract String getName();

    public int read(char[] buf, int offset, int length) throws IOException {
        return this.myReader.read(buf, offset, length);
    }

    public void write(byte[] bytes) throws IOException {
        this.myOutputStream.write(bytes);
        this.myOutputStream.flush();
    }

    public abstract boolean isConnected();

    public void write(String string) throws IOException {
        // TODO History command
        this.write(string.getBytes(this.myCharset));
    }

    /**
     * @deprecated
     */
    @Deprecated
    protected void setPendingTermSize(@Nullable Dimension pendingTermSize) {
        this.myPendingTermSize = pendingTermSize;
    }

    /**
     * @deprecated
     */
    @Deprecated
    @Nullable
    protected Dimension getPendingTermSize() {
        return this.myPendingTermSize;
    }

    /**
     * @deprecated
     */
    @Deprecated
    protected Dimension getPendingPixelSize() {
        return new Dimension(0, 0);
    }

    public boolean init(Questioner q) {
        return this.isConnected();
    }

    public void close() {
        this.myProcess.destroy();

        try {
            this.myOutputStream.close();
        } catch (IOException var3) {
        }

        try {
            this.myInputStream.close();
        } catch (IOException var2) {
        }

    }

    public int waitFor() throws InterruptedException {
        return this.myProcess.waitFor();
    }

    public boolean ready() throws IOException {
        return this.myReader.ready();
    }
}
