package ru.ylab.tasks;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public final class MockServletInputStream extends ServletInputStream {
    private final ByteArrayInputStream bais;

    MockServletInputStream(String body) {
        this.bais = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
    }

    @Override public boolean isFinished() { return bais.available() == 0; }
    @Override public boolean isReady() { return true; }
    @Override public void setReadListener(ReadListener readListener) {}
    @Override public int read() { return bais.read(); }
}
