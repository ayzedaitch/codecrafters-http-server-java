import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class RequestHandler implements Runnable{

    private final Socket clientSocket;

    public RequestHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String[] requestParts = reader.readLine().split(" ");
            String response = "HTTP/1.1 404 Not Found\r\n\r\n";
            if(requestParts[1].equals("/")) {
                response = "HTTP/1.1 200 OK\r\n\r\n";
            } else if (requestParts[1].startsWith("/echo")) {
                String str = requestParts[1].substring(6);
                response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + str.length() + "\r\n\r\n" + str;
            } else if (requestParts[1].equals("/user-agent")) {
                StringBuilder headers = new StringBuilder();
                String line = reader.readLine();
                String userAgent = "";
                while (line != null && !line.isEmpty()) {
                    if (line.startsWith("User-Agent")) {
                        userAgent = line.substring(line.indexOf(":") + 2);
                    }
                    line = reader.readLine();
                }
                response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + userAgent.length() + "\r\n\r\n" + userAgent;
            }
            clientSocket.getOutputStream().write(response.getBytes());
            System.out.println("accepted new connection");
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e){
                System.out.println("Failed to close socket: " + e.getMessage());
            }
        }
    }
}
