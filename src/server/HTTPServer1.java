package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HTTPServer1 {
    //存放Servlet实例的缓存
    private static Map<String, Servlet> servletCache = new HashMap<String, Servlet>();

    public static void main(String args[]) {
        int port;
        ServerSocket serverSocket;
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception e) {
            System.out.println("port = 8080(默认)");
            port = 8080;
        }
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("服务器正在监听端口：" + serverSocket.getLocalPort());
            while (true) {
                final Socket socket = serverSocket.accept();
                System.out.println("建立了与客户的一个新的TCP连接，该客户地址为："+socket.getInetAddress()+":"+socket.getPort());
                service(socket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*响应客户的HTTP请求*/
    public static void service(Socket socket) throws Exception {
        /*读取HTTP请求信息*/
        InputStream socketIn = socket.getInputStream();
        Thread.sleep(500);
        int size = socketIn.available();
        byte[] buffer = new byte[size];
        socketIn.read(buffer);
        String request = new String(buffer);
        System.out.println(request);
        /*解析HTTP请求*/
        //获得HTTP请求的第一行
        int endIndex = request.indexOf("\r\n");
        if (endIndex == -1) {
            endIndex = request.length();
        }
        String firstLineOfRequest = request.substring(0,endIndex);
        //解析HTTP请求的第一行
        String[] parts = firstLineOfRequest.split(" ");
        String uri = "";
        if (parts.length>=2) {
            uri = parts[1];
        }
        /*如果请求访问Servlet，则动态调用Servlet对象的Service方法*/
        if (uri.contains("servlet")) {
            String servletName = null;
            if (uri.contains("?")) {
                servletName = uri.substring(uri.indexOf("servlet/")+8,uri.indexOf("?"));
            } else {
                servletName = uri.substring(uri.indexOf("servlet/")+8,uri.length());
            }
            //从Servlet缓存中获取Servlet对象
            Servlet servlet = servletCache.get(servletName);
            if (servlet == null) {
                servlet = (Servlet) Class.forName("server."+servletName).getDeclaredConstructor().newInstance();
                servlet.init();
                servletCache.put(servletName,servlet);
            }

            //调用Servlet的Service()方法
            servlet.service(buffer,socket.getOutputStream());

            Thread.sleep(1000);
            socket.close();
            return;
        }
        /*决定HTTP响应正文的类型，此处做了简化处理*/
        String contentType;
        if (uri.indexOf("html")!=-1 || uri.indexOf("htm") != -1) {
            contentType = "text/html";
        } else if (uri.indexOf("jpg")!=-1 || uri.indexOf("jpeg") != -1) {
            contentType = "image/jpeg";
        } else if (uri.indexOf("gif")!=-1) {
            contentType = "image/gif";
        } else {
            contentType = "application/octet-stream";//字节流类型
        }

        /*创建HTTP响应结果*/
        //HTTP响应的第一行
        String responseFirstLine = "HTTP/1.1 200 OK\r\n";
        //HTTP响应头
        String responseHeader = "Content-Type:"+contentType+"\r\n\r\n";
        //获得读取响应正文数据的输入流
        //InputStream inTest = new FileInputStream(new File("F:/test/"+uri));
        InputStream in = HTTPServer1.class.getResourceAsStream(uri);
        /*发送HTTP响应结果*/
        OutputStream socketOut = socket.getOutputStream();
        //发送Http响应的第一行
        socketOut.write(responseFirstLine.getBytes());
        //发送HTTP响应的头
        socketOut.write(responseHeader.getBytes());
        //发送HTTP响应的正文
        int len = 0;
        buffer = new byte[128];
        while ((len = in.read(buffer)) != -1) {
            socketOut.write(buffer,0,len);
        }

        Thread.sleep(1000);
        socket.close();
    }
}
