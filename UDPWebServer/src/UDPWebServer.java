import java.io.*;
import java.net.* ;
import java.util.* ;

public final class UDPWebServer {
    public static void main(String argv[]) throws Exception {
        //Mission 1. Fill in #1 Create DatagramSocket.
        DatagramSocket socket = new DatagramSocket(6789);// Changed ServerSocket to DatagramSocket
        
        // Process HTTP service requests in an infinite loop.
        while (true) {
			//Mission  1, Fill in #2 Init receiveData 
			// Fill in #2 Construct an object to process the HTTP request message.(Changed to DatagramPacket)
            byte[] receiveData = new byte[1024];
            // client로부터 받은 request를 저장할 datagram
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            
            // Fill in #3 Listen for a UDP packet.
            // Fill in #3 Construct an object to process the HTTP request message(From changed constructor)
            socket.receive(receivePacket); // UDP 패킷 수신
            // HTTP 요청 처리를 위한 UDPHttpRequest 객체 생성
			UDPHttpRequest request = new UDPHttpRequest(socket, receivePacket);  // Changed constructor
            // Create a new thread to process the request.
            Thread thread = new Thread(request);
            
            // Start the thread.
            thread.start();
        }
    }
}


