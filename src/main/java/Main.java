import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    // Uncomment this block to pass the first stage
    //
     ServerSocket serverSocket = null;
     Socket clientSocket = null;

     try {
       serverSocket = new ServerSocket(4221);
       // Since the tester restarts your program quite often, setting SO_REUSEADDR
       // ensures that we don't run into 'Address already in use' errors
       serverSocket.setReuseAddress(true);
       clientSocket = serverSocket.accept(); // Wait for connection from client.
         BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
         String[] requestParts = reader.readLine().split(" ");
         String response = "HTTP/1.1 404 Not Found\r\n\r\n";
         if(requestParts[1].equals("/")) {
             response = "HTTP/1.1 200 OK\r\n\r\n";
             clientSocket.getOutputStream().write(response.getBytes());
         } else if (requestParts[1].startsWith("/echo")) {
             String str = requestParts[1].substring(6);
             response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + str.length() + "\r\n\r\n" + str;
             clientSocket.getOutputStream().write(response.getBytes());
         } else {
             clientSocket.getOutputStream().write(response.getBytes());
         }
       System.out.println("accepted new connection");
     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }
}
