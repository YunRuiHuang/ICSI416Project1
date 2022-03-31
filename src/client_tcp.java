import java.io.*;
import java.net.Socket;
import java.util.Scanner;



public class client_tcp {
    public static void main(String[] args) throws IOException{
        String host= args[0];
        int port = Integer.parseInt(args[1]);

        while (true){


            Scanner scanner = new Scanner(System.in);
            System.out.println("write something:");
            String command = scanner.nextLine();
            String[] temp = command.split(" ");
            String line;
            if(temp[0] != null){
                if(temp[0].equalsIgnoreCase("put")){
                    //put method
                    line = putMethod(host, port, temp[1]);
                }else if(temp[0].equalsIgnoreCase("get")){
                    //get method
                    line = getMethod(host, port, temp[1]);
                }else if(temp[0].equalsIgnoreCase("remap")){
                    //remap method
                    line = remap(host, port, temp[2], Integer.parseInt(temp[1]));
                }else{
                    line = massage(host, port, command);
                }

            }else{
                line = "please enter a command";
            }





            if(line.equalsIgnoreCase("close") || line.equalsIgnoreCase("quit")){
                break;
            }
        }

    }

    private static String putMethod(String host, int port, String fileName) throws IOException {
        Socket socket = new Socket(host, port);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        printWriter.println("put " + fileName);
        String line;
        while ((line = bufferedReader.readLine()) != null){
            printWriter.println(line);
        }
        socket.shutdownOutput();

        InputStream inputStream = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        line = reader.readLine();
        System.out.println("massage: " + line);


        inputStream.close();
        socket.close();
        return line;

    }

    private static String getMethod(String host, int port, String fileName) throws IOException {
        Socket socket = new Socket(host, port);
        OutputStream outputStream = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true);

        writer.println("get " + fileName);
        //writer.println("This is a message sent to the server");

        socket.shutdownOutput();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //
        PrintWriter out = new PrintWriter(new FileWriter("2" + fileName),true);
        String line;
        while((line = bufferedReader.readLine()) != null){
            out.println(line);
        }

        bufferedReader.close();
        out.close();
        return "finish";
    }

    private static String remap(String host, int port, String fileName, int N) throws IOException {

        putMethod(host, port, fileName);
        Socket socket = new Socket(host, port);
        OutputStream outputStream = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true);

        writer.println("remap " + fileName + " " +  N);
        socket.shutdownOutput();

        InputStream inputStream = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        System.out.println("massage: " + line);
        outputStream.close();
        socket.close();
        return "null";
    }


    private static String massage(String host, int port, String msg) throws IOException {

        Socket socket = new Socket(host, port);
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
