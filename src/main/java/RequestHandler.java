import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RequestHandler implements Runnable{

    private final Socket clientSocket;
    private final String[] args;

    public RequestHandler(Socket clientSocket, String[] args) {
        this.clientSocket = clientSocket;
        this.args = args;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String[] requestParts = reader.readLine().split(" "); // Read the first line of the request
            String response = "HTTP/1.1 404 Not Found\r\n\r\n";
            if(requestParts[1].equals("/")) {
                response = "HTTP/1.1 200 OK\r\n\r\n";
            } else if (requestParts[1].startsWith("/echo/")) {
                String str = requestParts[1].substring(6); // /echo/{str}
                response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + str.getBytes().length + "\r\n\r\n" + str;
            } else if (requestParts[1].equals("/user-agent")) {
                String line = reader.readLine();
                String userAgent = "";
                while (line != null && !line.isEmpty()) {
                    if (line.startsWith("User-Agent")) {
                        userAgent = line.substring(line.indexOf(":") + 2); // User-Agent: {value}
                        break;
                    }
                    line = reader.readLine();
                }
                response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + userAgent.getBytes().length + "\r\n\r\n" + userAgent;
            } else if (requestParts[1].startsWith("/files/")) {
                String path = null;
                for (int i = 0; i < args.length; i++) {
                    if (args[i].equals("--directory")) {
                        path = args[++i] + requestParts[1].substring(7);;
                        break;
                    }
                }
                try{
                    String content = Files.readString(Paths.get(path));
                    response = "HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: " + content.getBytes().length + "\r\n\r\n" + content;
                } catch (Exception e) {
                    response = "HTTP/1.1 404 Not Found\r\n\r\n";
                }

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
