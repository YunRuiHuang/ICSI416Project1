import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;



public class server_tcp {
    public static void main(String[] args) throws IOException{
        int port = 11111;
        ServerSocket server = new ServerSocket(port);

        while(true){
            System.out.println("server start waiting for input");
            Socket socket = server.accept();

            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            System.out.println("massage: " + line);

            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);
            writer.println(line);

            outputStream.close();
            inputStream.close();

            if(line.equalsIgnoreCase("close")){
                socket.close();
                server.close();
                break;
            }
        }

    }

}
