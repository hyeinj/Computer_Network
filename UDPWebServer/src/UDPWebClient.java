import java.io.*;
import java.net.*;

/** WebClient class implements a simple web client.
* Its primary responsibilities include:
* 1. Initializing the udp connection to web server
* 2. send HTTP request and receive HTTP response
*/
public class UDPWebClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 6789;
        String resource = "/HelloWorld.html";

        try {
            // Mission 1. Fill in #11 define InetAddress with host ip and initial DatagramSocket
            InetAddress address = InetAddress.getByName(host); // server address
            DatagramSocket socket = new DatagramSocket(); //create UDP socket 

/**
* Improve your HTTP Client to provide other request Methods(POST, DELETE, …)
* and also improve to handle headers(Content-Type, User-Agent, …)
*/
            // Mission 2. Fill in #10 Create Request MSG
            //Fill in #10 Transfter to Byte and put in datagramPacket, and set it.
            String requestMessage = "GET " + resource + " HTTP/1.0\r\n" +
                    "Host: " + host + "\r\n" +
                    "User-Agent: UDPWebClient\r\n" +
                    "Accept: */*\r\n" +
                    "\r\n"; // HTTP GET 요청 메시지 생성

            byte[] requestData = requestMessage.getBytes();
            DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, address, port);

            // Send the request
            socket.send(requestPacket);

            // Mission 3. Fill in #11 Initialize byte and datagrampacket to receive data
            byte[] responseData = new byte[4096]; // 응답 데이터를 수신할 버퍼
            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length);
            
            // Receive the response
            socket.receive(responsePacket);

            // Fill in #12 Get string from datagrampacket to display
            String responseMessage = new String(responsePacket.getData(), 0, responsePacket.getLength());
            System.out.println("Response from server:");
            System.out.println(responseMessage);

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
