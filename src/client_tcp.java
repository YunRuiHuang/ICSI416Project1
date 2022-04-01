import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * author Yunrui Huang
 * ICSI416
 * 2022/03/31
 */

public class client_tcp {
    /**
     * The main method to run the TCP client
     * @param args
     * input accept IP address and port, which should be $java client_tcp [IP address] [port]
     * @throws IOException
     * throws the IOException
     */
    public static void main(String[] args) throws IOException{
        String host= args[0];
        int port = Integer.parseInt(args[1]);

        while (true){


            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter Command:");
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
                System.out.println("Exiting!");
                break;
            }
        }

    }

    /**
     * put method used to put the file from local to server
     * @param host
     * The IP address of host, which should be a String
     * @param port
     * The open port of host, which should be an int
     * @param fileName
     * The name of file for upload to the host
     * @return
     * the massage from the host after upload the file
     * @throws IOException
     * throws the IOException
     */

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

    /**
     * get method use to download the file from server to local
     * @param host
     * The IP address of host, which should be a String
     * @param port
     * The open port of host, which should be an int
     * @param fileName
     * The name of file for download from the host
     * @return
     * the massage after download the file
     * @throws IOException
     * throws the IOException
     */
    private static String getMethod(String host, int port, String fileName) throws IOException {
        Socket socket = new Socket(host, port);
        OutputStream outputStream = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true);

        writer.println("get " + fileName);
        //writer.println("This is a message sent to the server");

        socket.shutdownOutput();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //
        PrintWriter out = new PrintWriter(new FileWriter("1" + fileName),true);
        String line;
        while((line = bufferedReader.readLine()) != null){
            out.println(line);
        }

        bufferedReader.close();
        out.close();
        System.out.println("massage: " + fileName + " Downloaded, rename as : 1" + fileName);
        return "finish";
    }

    /**
     * remap method use to remap the data in the file on server
     * @param host
     * The IP address of host, which should be a String
     * @param port
     * The open port of host, which should be an int
     * @param fileName
     * The name of file for remap
     * @return
     * the massage after download the file
     * @throws IOException
     * throws the IOException
     */
    private static String remap(String host, int port, String fileName, int N) throws IOException {

        //putMethod(host, port, fileName);
        Socket socket = new Socket(host, port);
        OutputStream outputStream = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true);

        writer.println("remap " + fileName + " " +  N);
        socket.shutdownOutput();

        InputStream inputStream = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        System.out.println("massage: " + line);
        reader.close();
        outputStream.close();
        socket.close();
        return "null";
    }


    /**
     * the massage method is use to send the massage to the server and receive that back
     * @param host
     * The IP address of host, which should be a String
     * @param port
     * The open port of host, which should be an int
     * @param msg
     * the message ready to send to the server
     * @return
     * the massage from the server, which will be the same as the massage send to the server
     * @throws IOException
     * throw the IOException
     */
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
