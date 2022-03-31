import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;



public class server_tcp {
    public static void main(String[] args) throws IOException{
        int port = Integer.parseInt(args[0]);
        ServerSocket server = new ServerSocket(port);

        while(true){
            System.out.println("server start waiting for input at port " + port);
            Socket socket = server.accept();

            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            System.out.println("massage: " + line);

            String[] temp = line.split(" ");
            if(temp[0].equalsIgnoreCase("put")){
                //put method
//                inputStream.close();
//                System.out.println("here");
                putMethod(socket, temp[1]);
            }else if(temp[0].equalsIgnoreCase("get")){
                //get method
                getMethod(socket, temp[1]);
            }else if(temp[0].equalsIgnoreCase("remap")){
                //remap method
                remap(socket, temp[1], temp[2]);
            }else{
                massage(socket, line);
            }


            inputStream.close();
            if(line.equalsIgnoreCase("close")){
                socket.close();
                server.close();
                break;
            }else if(line.equalsIgnoreCase("quit")){
                socket.close();
            }
        }

    }

    private static void putMethod(Socket socket, String fileName) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //
        PrintWriter out = new PrintWriter(new FileWriter("1" + fileName),true);
        String line;
        while((line = bufferedReader.readLine()) != null){
            out.println(line);
        }
        socket.shutdownInput();
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
        printWriter.println("file upload");
        //

        bufferedReader.close();
        out.close();
        printWriter.close();




    }

    private static void getMethod(Socket socket, String fileName) throws IOException {

        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
//        printWriter.println("put " + fileName);
        String line;
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            while ((line = bufferedReader.readLine()) != null){
                printWriter.println(line);
            }
        }catch (Exception e){
            printWriter.println("File not exist");
        }

//        socket.shutdownOutput();
//
//        InputStream inputStream = socket.getInputStream();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//        line = reader.readLine();
//        System.out.println("massage: " + line);
//
//
//        inputStream.close();
        socket.close();

    }

    private static void remap(Socket socket, String fileName, String N) throws IOException {
        int moveN = Integer.parseInt(N);
        String[] remapFileName = fileName.split("\\.");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        PrintWriter out = new PrintWriter(new FileWriter(remapFileName[0] + "_remap." + remapFileName[1]),true);

        char ch;
        while(bufferedReader.ready()){
            ch = (char)bufferedReader.read();
            if((int)ch > 96 && (int)ch < 123){
                ch = (char)((int)ch - moveN);
                if((int)ch < 97){
                    ch = (char)((int)ch + 26);
                }else if((int)ch > 122){
                    ch = (char)((int)ch - 26);
                }
            }

            out.print(ch);
        }

        OutputStream outputStream = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true);
        writer.println("new file is: " + remapFileName[0] + "_remap." + remapFileName[1]);

        outputStream.close();

    }


    private static void massage(Socket socket, String line) throws IOException {

        OutputStream outputStream = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true);
        writer.println(line);

        outputStream.close();
    }

}
