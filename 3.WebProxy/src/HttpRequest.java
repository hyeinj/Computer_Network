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
    final static int HTTP_PORT = 8888;
    /** Store the request parameters */
    String method;
    String URI;
    String version;
    String headers = "";
    /** Server and port */
    private String host;
    private int port;

    /** Create HttpRequest by reading it from the client socket */
    public HttpRequest(BufferedReader from) {
    	String firstLine = "";
    	try {
    		firstLine = from.readLine();
    	} catch (IOException e) {
    		System.out.println("Error reading request line: " + e);
    	}

		String[] tmp = firstLine.split(" ");
		method = tmp[0];
		URI = tmp[1];
		version = tmp[2];
		
		if (URI.contains(":")) {
	        // Extract host and port if present
	        int colonIndex = URI.indexOf(":");
	        int slashIndex = URI.indexOf("/", colonIndex);

	        if (slashIndex == -1) {
	            slashIndex = URI.length();
	        }

	        host = URI.substring(0, colonIndex); // Extract host
	        port = Integer.parseInt(URI.substring(colonIndex + 1, slashIndex)); // Extract port
	        URI = URI.substring(slashIndex); // Extract path
	    } else {
	        // Default to HTTP port if no port is specified
	        int slashIndex = URI.indexOf("/");
	        if (slashIndex == -1) {
	            slashIndex = URI.length();
	        }

	        host = URI.substring(0, slashIndex); // Extract host
	        port = HTTP_PORT; // Default port
	        URI = URI.substring(slashIndex); // Extract path
	    }
	
		System.out.println("URI is: " + URI);
	
		if (!method.equals("GET")) {
		    System.out.println("Error: Method not GET");
		}
		try {
		    String line = from.readLine();
		    while (line.length() != 0) {
				/* We need to find host header to know which server to
				 * contact in case the request URI is not complete. */
				/*if (line.startsWith("Host:")) {
				    tmp = line.split(" ");
				    String hostHeader = tmp[1];
		            if (hostHeader.contains(":")) {
		                String[] hostParts = hostHeader.split(":");
		                host = hostParts[0];
		                port = Integer.parseInt(hostParts[1]);
		            } else {
		                host = hostHeader;
		                port = HTTP_PORT;
		            }
				}*/
				line = from.readLine();
		    }
		} catch (IOException e) {
		    System.out.println("Error reading from socket: " + e);
		    return;
		}
		System.out.println("Host to contact is: " + host + " at port " + port);
    }

    /** Return host for which this request is intended */
    public String getHost() {
    	System.out.println("host:" + host);
    	return host;
    }

    /** Return port for server */
    public int getPort() {
    	System.out.println("port:" + port);
    	return port;
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
