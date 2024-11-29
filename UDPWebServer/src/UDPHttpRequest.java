import java.io.* ;
import java.net.* ;
import java.util.* ;

final class UDPHttpRequest implements Runnable {
    final static String CRLF = "\r\n";
    // Mission 1. Fill in #4 Changed to DatagramSocket
    private DatagramSocket socket;
    //  Fill in #4 Added to store the received packet
    private DatagramPacket receivePacket;

    // Fill in #5 Constructor should be changed. Socket and packet information should be transferred
    public UDPHttpRequest(DatagramSocket socket, DatagramPacket receivePacket) throws Exception {
    	// UDPWebServer에서 이 객체를 생성하면 여기서 this에 저장하고 request요청 처리 & response 반환까지 진행
        this.socket = socket;
        this.receivePacket = receivePacket;   
    }
    
    // Implement the run() method of the Runnable interface.
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception {
        // Mission 2. #Fill in #6 Get the request data from the packet
        // Received DatagramPacket  Byte  byteArrayInputStream  InputStream  BufferedReader
    	byte[] requestData = receivePacket.getData();
        ByteArrayInputStream byteStream = new ByteArrayInputStream(requestData);
        InputStreamReader inputStream = new InputStreamReader(byteStream);
        BufferedReader br = new BufferedReader(inputStream);

        // Get the request line of the HTTP request message.
        String requestLine = br.readLine();
        System.out.println("Request Line: " + requestLine);

        // Extract the filename from the request line.
        StringTokenizer tokens = new StringTokenizer(requestLine);
        String method = tokens.nextToken();
        String fileName = tokens.nextToken();
        String version = tokens.nextToken();
        // Remove leading slash if present
        if (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }
        // Get the base directory for the "src" folder
        String baseDir = System.getProperty("user.dir") + File.separator + "src";

        // Construct the full file path by appending the requested file to the base directory
        String filePath = baseDir + File.separator + fileName;

        // Debugging: Print the full file path
        System.out.println("Looking for file(filePath): " + filePath);
        // Prepend a "." so that file request is within the current directory.
//        fileName = "." + fileName;
        System.out.println("Looking for file(fileName): " + fileName);
        
        // Open the requested file.
        FileInputStream fis = null;
        boolean fileExists = true;
        try {
            fis = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            fileExists = false;
        }

        System.out.println("File exists: " + fileExists);
        

        // Construct the response message.
        String statusLine = null;
        String contentTypeLine = null;
        String contentLengthLine = null;
        String entityBody = null;

        if(!method.equals("GET")) {  
        	// Check for unsupported HTTP method (501 Method Not Implemented)
        	statusLine = "HTTP/1.0 501 Method Not Implemented" + CRLF;
        	contentTypeLine = "Content-Type: text/html" + CRLF;
            entityBody = "<HTML><HEAD><TITLE>501 Method Not Implemented</TITLE></HEAD><BODY>Method Not Implemented</BODY></HTML>";
        } else if(!version.equals("HTTP/1.0")) { 
        	// Check for unsupported HTTP version (505 Version Not Supported)
            statusLine = "HTTP/1.0 505 Version Not Supported" + CRLF;
            contentTypeLine = "Content-Type: text/html" + CRLF;
            entityBody = "<HTML><HEAD><TITLE>505 Version Not Supported</TITLE></HEAD><BODY>HTTP Version Not Supported</BODY></HTML>";
        } else if (fileExists) {
            statusLine = "HTTP/1.0 200 OK" + CRLF;
            contentTypeLine = "Content-Type: " + contentType(fileName) + CRLF;
            contentLengthLine = "Content-Length: " + getFileSizeBytes(fileName) + CRLF;
        } 
        else {
            statusLine = "HTTP/1.0 404 Not Found" + CRLF;
            contentTypeLine = "Content-Type: text/html" + CRLF;
            entityBody = "<HTML><HEAD><TITLE>Not Found</TITLE></HEAD><BODY>Not Found</BODY></HTML>";
        }

        // Mission 3. Fill in #7 Create the header line as bytes with get Bytes
        // Fill in #7 create and write to ByteArrayOutputStream to send header line
        // server가 client에 보낼 response 생성하는 과
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(statusLine.getBytes());
        baos.write(contentTypeLine.getBytes());
        if (contentLengthLine != null) {
            baos.write(contentLengthLine.getBytes());
        }
        baos.write(CRLF.getBytes()); // Blank line to separate headers from body


        // transfer entity to ByteArrayOutputStream
        if (fileExists) { // 요청된 파일이 존재하는 경우, 해당 file contents를 response에 포함해야한
            sendBytes(fis, baos); // sendBytes():ByteArrayOutputStream(baos)에 파일 데이터를 
            fis.close();
        } else {
            baos.write(entityBody.getBytes());
        } 

       // Mission 3. Fill in #8 Create the byte to send stream(header and entity body)
       // Fill in #8 send Datagram with UDP Socket.
       // (ByteArrayOutputStream  ByteArray   DatagramPacket)
        byte[] responseData = baos.toByteArray();
        InetAddress clientAddress = receivePacket.getAddress();
        int clientPort = receivePacket.getPort();
        DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);

        socket.send(responsePacket);
        
        // Close streams and socket.
        br.close();
    }

/**
 * Method which sends the context
 * @param fis FileInputStream to transfer
 * @param os outputstream to client
 */
    private static void sendBytes(FileInputStream fis, 
				  OutputStream os) throws Exception {
	// Construct a 1K buffer to hold bytes on their way to the socket.
	byte[] buffer = new byte[1024];
	int bytes = 0;
	
	// Copy requested file into the socket's output stream.
	while ((bytes = fis.read(buffer)) != -1) {
	    os.write(buffer, 0, bytes);
	}
    }

/**
 * Method to return appropriate
 * @param fileName 
 */
private static String contentType(String fileName) {
	if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
	    return "text/html";
	}
/**
 * create an HTTP response message consisting of the requested file preceded by header lines
 * Now, you are just handling text/html, is there any more context-types? Find and make codes for it.
 */
	if(fileName.endsWith(".ram") || fileName.endsWith(".ra")) {
	    return "audio/x-pn-realaudio";
	} 
	if(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
	    return "image/jpeg";
	}
	if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
        return "image/jpeg";
    }
    if (fileName.endsWith(".png")) {
        return "image/png";
    }
    if (fileName.endsWith(".gif")) {
        return "image/gif";
    }
    if (fileName.endsWith(".mp3")) {
        return "audio/mpeg";
    }
    if (fileName.endsWith(".pdf")) {
        return "application/pdf";
    }
	return "application/octet-stream" ;
    }

/**
 * Get the File name, and through the file name, get the size of the file.
 *.@param fileName
 */
private static long getFileSizeBytes(String fileName) throws IOException {
        File file = new File(fileName);
        return file.length();
    }
  // This method returns the size of the specified file in bytes.
}
