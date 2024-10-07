import java.io.*;
import java.net.*;

/**
* WebClient class implements a simple web client.

* Its primary responsibilities include:
* 1. Initializing the tcp connection to web server
* 2. send HTTP request and receive HTTP response
*/
public class WebClient {
    public static void main(String[] args) {
        // Set the host, port and resource to send HTTP Request
    	String host = "localhost";
        int port = 8888;
        String resource = "/index.html";
          // Mission 1: Establish a socket connection to Server
        try (
        		//Fill #1, Set TCP socket to HTTP Web Server
        		Socket socket = new Socket(host, port);
        		//Fill #2, create PrintWirter instance with socket’s OutputStream
        		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        		//Fill #3, Get input stream from server, and insert it to BufferedReader instance
        		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        		)
        // try-with-resources 구
        {

/**
* Improve your HTTP Client to provide other request Methods(POST, DELETE, …)
* and also improve to handle headers(Content-Type, User-Agent, …)
*/
            // Mission 2: Send HTTP GET Request and Read and display the response
        	// Fill#4, Send HTTP GET request
            out.println("GET " + resource + " HTTP/1.1");
            out.println("Host: " + host);
            out.println("User-Agent: Firefox/3.6.10"); // User-Agent 헤
            out.println("Accept: text/html,application/xhtml+xml");  // Accept 헤더
            out.println("Accept-Language: en-us,en;q=0.5");  // Accept-Language 헤더
            out.println("Accept-Encoding: gzip,deflate");  // Accept-Encoding 헤더
            out.println("Accept-Charset: ISO-8859-1,utf-8;q=0.7");  // Accept-Charset 헤더
            out.println("Connection: keep-alive");  // Connection 헤더
            out.println();  // 빈 줄로 요청 헤더 끝을 표시

            // Mission 3: Read and display the response
            //Fill#5,  Read and display the response
            String responseLine;
            while ((responseLine = in.readLine()) != null) {
                System.out.println(responseLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
