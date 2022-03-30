import java.io.*;
import java.net.Socket;
import java.util.Scanner;



public class client_tcp {
    public static void main(String[] args) throws IOException{
        String host = "127.0.0.1";
        int port = 11111;



        while (true){
            Socket socket = new Socket(host, port);

            Scanner scanner = new Scanner(System.in);
            System.out.println("write something:");
            String command = scanner.nextLine();
            String[] temp = command.split(" ");
            String line;
            if(temp[0] != null){
                if(temp[0].equalsIgnoreCase("put")){
                    //put method
                    line = putMethod(socket, temp[1]);
                }else if(temp[0].equalsIgnoreCase("get")){
                    //get method
                    line = getMethod(socket, temp[1]);
                }else if(temp[0].equalsIgnoreCase("remap")){
                    //remap method
                    line = remap(socket, temp[2], Integer.parseInt(temp[1]));
                }else{
                    line = massage(socket, command);
                }

            }else{
                line = "please enter a command";
            }





            if(line.equalsIgnoreCase("close") || line.equalsIgnoreCase("quit")){
                break;
            }
        }

    }

    private static String putMethod(Socket socket, String fileName){
        return null;
    }

    private static String getMethod(Socket socket, String fileName){
        return null;

    }

    private static String remap(Socket socket, String fileName, int N){

        return null;
    }

    private static boolean ifExist(Socket socket, String fileName){

        return true;
    }

    private static String massage(Socket socket, String msg) throws IOException {

        OutputStream outputStream = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true);

        writer.println(msg);
        //writer.println("This is a message sent to the server");

        socket.shutdownOutput();

        InputStream inputStream = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        System.out.println("massage: " + line);


        inputStream.close();
        outputStream.close();
        socket.close();
        return line;
    }


}
