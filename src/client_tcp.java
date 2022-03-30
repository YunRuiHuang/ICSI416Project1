import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class client_tcp {
    public static void main(String[] args) throws IOException{
        String host = "127.0.0.1";
        int port = 11111;



        while (true){
            Socket socket = new Socket(host, port);
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);
            Scanner scanner = new Scanner(System.in);
            System.out.println("write something:");
            writer.println(scanner.nextLine());
            //writer.println("This is a message sent to the server");

            socket.shutdownOutput();

            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            System.out.println("massage: " + line);


            inputStream.close();
            outputStream.close();
            socket.close();
            if(line.equalsIgnoreCase("close")){
                break;
            }
        }

    }
}
