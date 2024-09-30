import java.io.*; 
import java.net.*; 

class TCPClient {
	public static void main(String argv[]) throws Exception {
		String sentence;
		String modifiedSentence;

		// Create BufferedReader to read input from user
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		// Create socket to connect to server ("hostname" and port number 6789)
		Socket clientSocket = new Socket("localhost", 6789);
		// 여기서 ip주소는 server인 나의 주소이다 -> terminal에서 내 ip주소를 확인해도 되고, "127.0.0.1" 또는 "localhost"라고 적어도 

		
		// Create output stream to send data to server
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

		// Create input stream to receive data from server
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		// Read a line of input from user
		sentence = inFromUser.readLine();
		outToServer.writeBytes(sentence + '\n');
		
		
		// Read reply from clientSocket
		modifiedSentence = inFromServer.readLine();
		
		System.out.println("FROM SERVER: " + modifiedSentence);
		// Close the socket to end the connection
		clientSocket.close();
	}
}