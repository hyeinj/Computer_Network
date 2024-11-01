/**
 * HttpRequest - HTTP request container and parser
 *
 * $Id: HttpRequest.java,v 1.2 2003/11/26 18:11:53 kangasha Exp $
 *
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class HttpRequest {
    /** Help variables */
    final static String CRLF = "\r\n";
    final static int HTTP_PORT = 80;
    /** Store the request parameters */
    String method;
    String URI;
    String version;
    String headers = "";
    /** Server and port */
    private String host;
    private int port;
    private String body = ""; // POST 요청 본문을 저장할 변

    /** Create HttpRequest by reading it from the client socket */
    public HttpRequest(BufferedReader from) {
	String firstLine = "";
	try {
	    firstLine = from.readLine();
	    // firstLine이 null인지 확인
        if (firstLine == null) {
            throw new IOException("Empty request line from client.");
        }
        
        String[] tmp = firstLine.split(" ");
    	if (tmp.length < 3) { // 요청 줄이 올바른 형식인지 확인
            System.out.println("Error: Invalid request line");
            return;
        }
    	method = tmp[0];
    	URI = tmp[1];
    	version = tmp[2]; // HTTP version

    	System.out.println("URI is: " + URI);
    	
    	try {
    	    String line = from.readLine();
    	    while (line != null && line.length() != 0) {
    		headers += line + CRLF;
    		/* We need to find host header to know which server to
    		 * contact in case the request URI is not complete. */
    		if (line.startsWith("Host:")) {
    		    tmp = line.split(" ");
    		    if (tmp[1].indexOf(':') > 0) {
    			String[] tmp2 = tmp[1].split(":");
    			host = tmp2[0];
    			port = Integer.parseInt(tmp2[1]);
    		    } else {
    			host = tmp[1];
    			port = HTTP_PORT;
    		    }
    		}
    			line = from.readLine();
    	    }
    	    // POST 요청인 경우 본문 읽기
            if (method.equals("POST")) {
                StringBuilder bodyBuilder = new StringBuilder();
                while (from.ready()) { // 본문 읽기
                    bodyBuilder.append((char) from.read());
                }
                body = bodyBuilder.toString();
            }
    	    
    	} catch (IOException e) {
    	    System.out.println("Error reading from socket: " + e);
    	    return;
    	}

	} catch (IOException e) {
	    System.out.println("Error reading request line: " + e);
	}
//	if (!method.equals("GET")) {
//	    System.out.println("Error: Method not GET");
//	}
	
	System.out.println("Host to contact is: " + host + " at port " + port);
    }

    /** Return host for which this request is intended */
    public String getHost() {
	return host;
    }

    /** Return port for server */
    public int getPort() {
	return port;
    }
    
    public String getUrl() {
    	return URI;
    }
    
    public String getMethod() {
    	return method;
    }
    
    // POST 요청 본문 반
    public String getBody() {
    	return body;
    }

    /**
     * Convert request into a string for easy re-sending.
     */
    public String toString() {
	String req = "";

	req = method + " " + URI + " " + version + CRLF;
	req += headers;
	/* This proxy does not support persistent connections */
	req += "Connection: close" + CRLF;
	req += CRLF;
	
	return req;
    }
}
