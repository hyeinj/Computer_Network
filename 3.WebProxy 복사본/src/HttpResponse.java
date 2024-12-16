import java.io.*;
import java.util.*;

public class HttpResponse {
    final static String CRLF = "\r\n";
    final static int BUF_SIZE = 8192;
    final static int MAX_OBJECT_SIZE = 100000;

    String version;
    int status;
    String statusLine = "";
    String headers = "";
    byte[] body = new byte[MAX_OBJECT_SIZE];

    public HttpResponse(DataInputStream fromServer) {
        int length = -1;
        boolean gotStatusLine = false;

        try {
            // 응답 상태 라인과 헤더 읽기
            String line = fromServer.readLine();
            while (line != null && line.length() != 0) {
                if (!gotStatusLine) {
                    statusLine = line;
                    gotStatusLine = true;
                } else {
                    headers += line + CRLF;
                }

                // Content-Length 처리
                if (line.startsWith("Content-Length") || line.startsWith("Content-length")) {
                    String[] tmp = line.split(" ");
                    length = Integer.parseInt(tmp[1]);
                }
                line = fromServer.readLine();
            }
        } catch (IOException e) {
            System.out.println("Error reading headers from server: " + e);
            return;
        }

        try {
            int bytesRead = 0;
            int res;
            byte[] buf = new byte[BUF_SIZE];
            boolean loop = (length == -1);

            // 바디 읽기
            while (bytesRead < length || loop) {
                res = fromServer.read(buf, 0, BUF_SIZE);
                if (res == -1) break; // EOF

                // 바디를 body 배열에 복사
                System.arraycopy(buf, 0, body, bytesRead, res);
                bytesRead += res;

                if (length != -1 && bytesRead >= length) break;
            }

            System.out.println("Body received: " + bytesRead + " bytes");
        } catch (IOException e) {
            System.out.println("Error reading response body: " + e);
        }
    }

    public String toString() {
        return statusLine + CRLF + headers + CRLF;
    }
}
