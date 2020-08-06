import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HTTPClient {
    public static void main(String args[]) {
        String uri = "index.htm";
        if (args.length != 0) {
            uri = args[0];
        }
        doGet("localhost",8080,uri);
    }

    public static void doGet(String host, int port,String uri) {
        Socket socket = null;
        try {
            socket = new Socket(host,port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*创建HTTP请求*/
        //HTTP请求第一行
        StringBuffer sb = new StringBuffer("GET "+uri+" HTTP/1.1\r\n");
        //HTTP请求头
        sb.append("Accept:*/*\r\n");
        sb.append("Accept-Language:zh-cn\r\n");
        sb.append("Accept-Encoding:gzip,deflage\r\n");
        sb.append("User-Agent:HTTPClient\r\n");
        sb.append("Host: localhost:8080\r\n");
        sb.append("Connection:Keep-Alive\r\n\r\n");

        /*发送HTTP请求*/
        try {
            OutputStream socketOut = socket.getOutputStream();
            socketOut.write(sb.toString().getBytes());
            Thread.sleep(2000);

            /*接受响应结果*/
            InputStream socketIn = socket.getInputStream();
            int size = socketIn.available();
            byte[] buffer = new byte[size];
            socketIn.read(buffer);
            System.out.println(new String(buffer));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
