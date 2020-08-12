package server;

import java.io.OutputStream;

public interface Servlet {
    public void inint() throws Exception;
    public void service(byte[] requestBuffer, OutputStream out) throws Exception;
}
