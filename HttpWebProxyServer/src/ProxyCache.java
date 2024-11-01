/**
 * ProxyCache.java - Simple caching proxy
 *
 * $Id: ProxyCache.java,v 1.3 2004/02/16 15:22:00 kangasha Exp $
 *
 */

import java.net.*;
import java.io.*;
import java.util.*;

public class ProxyCache {
    /** Port for the proxy */
    private static int port;
    /** Socket for client connections */
    private static ServerSocket socket;
    // cache
    private static Map<String, String> cache = new HashMap<>();

    /** Create the ProxyCache object and the socket */
    public static void init(int p) {
	port = p;
	try {
	    socket = new ServerSocket(port);
	 // Ensure the cache directory exists
        File cacheDir = new File("cache");
        if (!cacheDir.exists()) {
            cacheDir.mkdir();  // Create the cache directory if it does not exist
        }
	} catch (IOException e) {
	    System.out.println("Error creating socket: " + e);
	    System.exit(-1);
	}
    }

    public static void handle(Socket client) {
    	Socket server = null;
    	HttpRequest request = null;
    	HttpResponse response = null;
    	String requestUrl = null;

	/* Process request. If there are any exceptions, then simply
	 * return and end this request. This unfortunately means the
	 * client will hang for a while, until it timeouts. */

	/* Read request */
	try {
	    // Create a BufferedReader to read the request from the client
		BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
	    // parse the client request
		request = new HttpRequest(fromClient);
		// proxy가 Client 요청 제대로 읽어들였는지 확인
		System.out.println("Proxy Server reads client's request : " + request);
		requestUrl = request.getUrl();
	} catch (IOException e) {
	    System.out.println("Error reading request from client: " + e);
	    return;
	}
	
	// Caching : 캐시에 해당 URL이 있는지 확인
    if (cache.containsKey(requestUrl)) {
        // 캐시된 파일 경로에서 응답 읽기
        String cachedFilePath = cache.get(requestUrl);
        try {
            File cachedFile = new File(cachedFilePath);
            FileInputStream fis = new FileInputStream(cachedFile);
            byte[] fileContent = fis.readAllBytes();
            fis.close();
            
            DataOutputStream toClient = new DataOutputStream(client.getOutputStream());
            toClient.write(fileContent);
    		
    		// proxy가 client에게 response를 제대로 전달했는지 확인
            System.out.println("Cache hit. Served from cache: " + cachedFilePath);
    		System.out.println("Proxy Server sends response to client : " + fileContent);
            
            client.close();
            return;
        } catch (IOException e) {
            System.out.println("Error reading from cache: " + e);
        }
    }
	
	
	/* Send request to server */
	try {
	    /* Open socket and write request to socket */
	    server = new Socket(request.getHost(), request.getPort());
	    // Create a DataOutputStream to send the request to the server
	    DataOutputStream toServer = new DataOutputStream(server.getOutputStream());
	    // Send the parsed request to the server
	    toServer.writeBytes(request.toString());
	    
	    // POST 요청인 경우 요청 본문도 서버에 전송
	    if (request.getMethod().equals("POST") && request.getBody() != null) {
	        toServer.write(request.getBody().getBytes());
	    }
	    
	    // proxy가 server한테 client의 요청을 제대로 전달했는지 확인
	    String reqeustString = request.toString();
	    System.out.println("Proxy Server sends request to server : " + reqeustString);
	} catch (UnknownHostException e) {
	    System.out.println("Unknown host: " + request.getHost());
	    System.out.println(e);
	    return;
	} catch (IOException e) {
	    System.out.println("Error writing request to server: " + e);
	    return;
	}
	
	/* Read response and forward it to client */
	try {
	    // Create a DataInputStream to read the response from the server
		DataInputStream fromServer = new DataInputStream(server.getInputStream());
	    // Parse the server's response
		response = new HttpResponse(fromServer);
		// proxy가 server로부터 response를 제대로 받았는지 확인
		System.out.println("Proxy Server reads response from server : " + response);
		
		// Caching
		// 서버 응답을 캐시에 저장
        File cacheFile = new File("cache/" + requestUrl.hashCode());
        FileOutputStream fos = new FileOutputStream(cacheFile);
        fos.write(response.toString().getBytes());
        fos.write(response.getBody());
        fos.close();
        
        // 캐시에 경로 저장
        cache.put(requestUrl, cacheFile.getAbsolutePath());
		
	    // Create a DataOutputStream to write the response to the client
		DataOutputStream toClient = new DataOutputStream(client.getOutputStream());
		
		// 응답 상태 코드가 404인지 확인
	    if (response.getStatusCode() != 404) {
	        // 응답 본문을 읽어옴
	    	 /* Write response to client. First headers, then body */
	        toClient.writeBytes(response.toString());
	        toClient.write(response.getBody());
	    } else {
	        // 404 에러이므로 본문을 보내지 않음
	        toClient.writeBytes(response.toString());
	    }
		
		// proxy가 client에게 server의 response를 제대로 전달했는지 확인
		System.out.println("Proxy Server sends server's response to client : ");
		System.out.println("Header : " + response.toString());
		System.out.println("Body : " + response.getBody());
		
		
		
	    client.close();
	    server.close();
	    /* Insert object into the cache */
	    /* Fill in (optional exercise only) */
	} catch (IOException e) {
	    System.out.println("Error writing response to client: " + e);
	}
    }


    /** Read command line arguments and start proxy */
    public static void main(String args[]) {
		int myPort = 0;
		
		try {
		    myPort = Integer.parseInt(args[0]);
		} catch (ArrayIndexOutOfBoundsException e) {
		    System.out.println("Need port number as argument");
		    System.exit(-1);
		} catch (NumberFormatException e) {
		    System.out.println("Please give port number as integer.");
		    System.exit(-1);
		}
		
		init(myPort);
	
		/** Main loop. Listen for incoming connections and spawn a new
		 * thread for handling them */
		Socket client = null;
		
		while (true) {
		    try {
		    	// Accept an incoming client connection
		    	client = socket.accept();
		    	handle(client);
		    } catch (IOException e) {
				System.out.println("Error reading request from client: " + e);
				/* Definitely cannot continue processing this request,
				 * so skip to next iteration of while loop. */
				continue;
		    }
		}
    }
}
