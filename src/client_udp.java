import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * author Yunrui Huang
 * ICSI416
 * 2022/03/31
 */

public class client_udp {

    /**
     * The main method to run the UDP client
     * @param args
     * input accept IP address and port, which should be $java client_dup [IP address] [port]
     * @throws IOException
     * throw the IOException
     * @throws InterruptedException
     * throw the InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        InetAddress address = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);
        DatagramSocket socket = new DatagramSocket();

        while(true){
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter command: ");
            String input = scanner.nextLine();
            String[] msg = input.split(" ");

            //switch
            if(msg[0].equalsIgnoreCase("get")){
                get(socket,input,address,port);
            }else if(msg[0].equalsIgnoreCase("put")){
                put(socket, input, address, port);

            }else if(msg[0].equalsIgnoreCase("remap")){
                remap(socket,msg,address,port);

            }else{
                massage(socket, input, address, port);
            }


            if(input.equalsIgnoreCase("close") || input.equalsIgnoreCase("quit")){
                socket.close();
                System.out.println("Exiting!");
                break;
            }

        }

    }


    /**
     * put method used to put the file from local to server
     * @param socket
     * the socket of udp client
     * @param input
     * the input command which should look like: $put [filename]
     * @param address
     * the IP address of server
     * @param port
     * the open port of server
     * @throws IOException
     * throw the IOException
     * @throws InterruptedException
     * throw the InterruptedException
     */
    private static void put(DatagramSocket socket, String input, InetAddress address, int port) throws IOException, InterruptedException {
        String[] msg = input.split(" ");
        input = "put " + msg[1] + " ";
        byte[] sendPacket = input.getBytes();
        DatagramPacket reportPacket = new DatagramPacket(sendPacket, sendPacket.length, address, port);
        socket.send(reportPacket);

        File file = new File(msg[1]);
        InputStream inputStream = new FileInputStream(file);

        sendPacket = (file.length() + "").getBytes();
        reportPacket = new DatagramPacket(sendPacket, sendPacket.length, address, port);
        socket.send(reportPacket);

        sendPacket = new byte[1024];
        socket.setSoTimeout(5000);
        try{
            while ((inputStream.read(sendPacket)) != -1){
                reportPacket =  new DatagramPacket(sendPacket, sendPacket.length, address, port);
                socket.send(reportPacket);
//                System.out.println(sendPacket.length);
//            TimeUnit.MILLISECONDS.sleep(1);
                 //wait for ACK packet here
                socket.receive(reportPacket);
//            System.out.println("file is: " + new String(sendPacket,0,reportPacket.getLength()));
            }
        }catch(IOException e){
            System.out.println("Did not receive ACK. Terminating.");
        }

        TimeUnit.MILLISECONDS.sleep(500);
        System.out.println("massage: File upload, rename as : 2" + msg[1]);
    }


    /**
     * get method use to download the file from server to local
     * @param socket
     * the socket of udp client
     * @param input
     * the input command which should look like: $get [filename]
     * @param address
     * the IP address of server
     * @param port
     * the open port of server
     * @throws IOException
     * throw the IOException
     */
    private static void get(DatagramSocket socket, String input, InetAddress address, int port) throws IOException {
        input = input + " ";
        byte[] sendPacket = input.getBytes();
        DatagramPacket reportPacket = new DatagramPacket(sendPacket, sendPacket.length, address, port);

        socket.send(reportPacket);
        String fileName = input.split(" ")[1];

        File file = new File("1" + fileName);
        FileOutputStream output = new FileOutputStream(file);
        socket.setSoTimeout(500);
        byte[] packSize = new byte[1024];
        DatagramPacket packet = new DatagramPacket(packSize, packSize.length, address, port);

        socket.receive(packet);
        int LEN = Integer.parseInt(new String(packSize, 0, packet.getLength()));
        System.out.println("file size is: " + LEN);

        try{

            while(LEN > 0){
                packSize = new byte[1024];
                packet = new DatagramPacket(packSize, packSize.length, address, port);
                socket.receive(packet);
                output.write(packSize, 0,packet.getLength());
                LEN = LEN - packSize.length;
//                System.out.println(LEN);
                output.flush();

                //send the SCK packet
                packSize = "ACK".getBytes();
                packet = new DatagramPacket(packSize, packSize.length, address, port);
                socket.send(packet);

                output.flush();
            }

//            socket.close();

        }catch(IOException e){
            socket.close();
            System.out.println("Did not receive data. Terminating.");
        }
        System.out.println("massage: " + fileName + " downloaded, rename as : 1" + fileName);
    }

    /**
     * remap method use to remap the data in the file on server
     * @param socket
     * the socket of udp client
     * @param msg
     * the input of command which spilt by " "
     * @param address
     * the IP address of the server
     * @param port
     * the open port of the server
     * @throws IOException
     * throw the IOException
     * @throws InterruptedException
     * throw the InterruptedException
     */
    private static void remap(DatagramSocket socket, String[] msg, InetAddress address, int port) throws IOException, InterruptedException {
        String input = "put " + msg[2];
//        put(socket,input,address,port);

        TimeUnit.MILLISECONDS.sleep(500);

        input = msg[0] + " " + msg[1] + " " + msg[2] + " ";
        byte[] sendPacket = input.getBytes();
        DatagramPacket reportPacket = new DatagramPacket(sendPacket, sendPacket.length, address, port);
        socket.send(reportPacket);

//        System.out.println("here");

        byte[] packSize = new byte[1024];
        DatagramPacket packet = new DatagramPacket(packSize, packSize.length);
        socket.receive(packet);
        String line = new String(packSize, 0, packSize.length);



        msg = line.split(" ");
        line = "";
        for(int i = 0; i < (msg.length-1); i++){
            line = line + msg[i] + " ";
        }
        System.out.println("massage : " + line);
    }

    /**
     * the massage method is use to send the massage to the server and receive that back
     * @param socket
     * the socket of the udp client
     * @param input
     * the massage ready to send to the server
     * @param address
     * the IP address of server
     * @param port
     * the open port of server
     * @throws IOException
     * throw the IOException
     */
    private static void massage(DatagramSocket socket, String input, InetAddress address, int port) throws IOException {
        input = input + " ";
        byte[] sendPacket = input.getBytes();
        DatagramPacket reportPacket = new DatagramPacket(sendPacket, sendPacket.length, address, port);

        socket.send(reportPacket);

        byte[] packSize = new byte[1024];
        DatagramPacket packet = new DatagramPacket(packSize, packSize.length);
        socket.receive(packet);
        String line = new String(packSize, 0, packSize.length);

        String[] msg = line.split(" ");
        line = "";
        for(int i = 0; i < (msg.length-1); i++){
            line = line + msg[i] + " ";
        }
        System.out.println("massage : " + line);
    }

}
