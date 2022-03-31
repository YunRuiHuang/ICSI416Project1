import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class client_udp {

    public static void main(String[] args) throws IOException, InterruptedException {
        InetAddress address = InetAddress.getByName("127.0.0.1");
        int port = 11111;
        DatagramSocket socket = new DatagramSocket();

        while(true){
            Scanner scanner = new Scanner(System.in);
            System.out.println("write something: ");
            String input = scanner.nextLine();
            String[] msg = input.split(" ");

            //switch
            if(msg[0].equalsIgnoreCase("get")){

            }else if(msg[0].equalsIgnoreCase("put")){
                put(socket, input, address, port);

            }else if(msg[0].equalsIgnoreCase("remap")){

            }else{
                massage(socket, input, address, port);
            }


            if(input.equalsIgnoreCase("close")){
                socket.close();
                break;
            }

        }

    }

    private static void put(DatagramSocket socket, String input, InetAddress address, int port) throws IOException, InterruptedException {
        String[] msg = input.split(" ");
        input = "put " + msg[1] + " ";
        byte[] sendPacket = input.getBytes();
        DatagramPacket reportPacket = new DatagramPacket(sendPacket, sendPacket.length, address, port);
        socket.send(reportPacket);

        File file = new File(msg[1]);
        InputStream inputStream = new FileInputStream(file);
        sendPacket = new byte[1024];
        while ((inputStream.read(sendPacket)) != -1){
            reportPacket =  new DatagramPacket(sendPacket, sendPacket.length, address, port);
            socket.send(reportPacket);
            TimeUnit.MICROSECONDS.sleep(1);
        }

    }



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
