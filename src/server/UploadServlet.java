package server;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;

public class UploadServlet implements Servlet {
    @Override
    public void init() throws Exception {
        System.out.println("UploadServlet is inited");
    }

    @Override
    public void service(byte[] requestBuffer, OutputStream out) throws Exception {
        String request = new String(requestBuffer);
        String headerOfRequest = request.substring(request.indexOf("\r\n") + 2, request.indexOf("\r\n\r\n"));
        BufferedReader br = new BufferedReader(new StringReader(headerOfRequest));
        String data = null;
        String boundary = null;
        while ((data = br.readLine()) != null) {
            if (data.contains("Content-Type")) {
                boundary = data.substring(data.indexOf("boundary=") + 9, data.length()) + "\r\n";
                break;
            }
        }
        if (boundary == null) {
            out.write("HTTP/1.1 200 OK\r\n".getBytes());
            //发送HTTP响应头
            out.write("Content-Type:text/html\r\n\r\n".getBytes());
            out.write("Upload is faild".getBytes());
            return;
        }
        //第一个boundary出现的位置
        int index1OfBoundary = request.indexOf(boundary);
        //第二个boundary出现的位置
        int index2OfBoundary = request.indexOf(boundary,
                index1OfBoundary + boundary.length());
        //第三个boundary出现的位置
        int index3OfBoundary = request.indexOf(boundary,
                index2OfBoundary + boundary.length());
        //文件部分的正文部分的开始前的位置
        int beforeofFilePart =
                request.indexOf("\r\n\r\n", index2OfBoundary) + 4;
        String sq = request.substring(request.indexOf("\r\n\r\n", index2OfBoundary),beforeofFilePart);
        int z = sq.length();
        String s = request.substring(index2OfBoundary+boundary.length(),beforeofFilePart);
        //文件部分的正文部分的结束后的位置
        int afterofFilePart = index3OfBoundary - 4;
        String ss = request.substring(index3OfBoundary - 4,index3OfBoundary);
        int q = ss.length();
        //文件部分的头的第一行结束后的位置
        int afterofFilePartLine1 = request.indexOf("\r\n",
                index2OfBoundary + boundary.length());
        //文件部分的头的第二行
        String header2ofFilePart = request.substring(
                index2OfBoundary + boundary.length(),
                afterofFilePartLine1);
        //上传文件的额名字
        String fileName = header2ofFilePart.substring(header2ofFilePart.lastIndexOf("=") + 2, header2ofFilePart.length() - 1);
        //文件部分的正文部分之前的字符串的字节长度
        int len1 = request.substring(0, beforeofFilePart + 1).getBytes().length;
        //文件部分正文部分之后的字符串的字节长度
        int len2 = request.substring(afterofFilePart, request.length()).getBytes().length;
        //文件部分的正文部分的字节长度
        int fileLen = requestBuffer.length - len1 - len2;
        /*把文件部分的正文部分保存到本地文件系统中*/
        FileOutputStream f = new FileOutputStream(fileName);
        f.write(requestBuffer, len1, fileLen);
        f.close();

        /*创建并发送HTTP响应*/
        //发送HTTP响应第一行
        out.write("HTTP/1/1 200 OK\r\r".getBytes());
        //发送HTTP响应头
        out.write("Content-Type:text/html\r\n\r\n".getBytes());
        //发送HTTP响应正文
        String content = "<html><head><title>Hello world</title></head><body><h1>Uploading is finished</h1></body></html>";
        out.write(content.getBytes());
    }
}
