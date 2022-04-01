import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.LinkPermission;
import java.util.concurrent.TimeUnit;

/**
 * author Yunrui Huang
 * ICSI416
 * 2022/03/31
 */

public class server_udp {

    /**
     * the main method use to run the UDP server
     * @param args
     * input accept port, which should be $java server_udp [port]
     * @throws IOException
     * throw the IOException
     * @throws InterruptedException
     * throw the InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        int port = Integer.parseInt(args[0]);
        while (true){
            DatagramSocket socket = new DatagramSocket(port);
            byte[] packSize = new byte[1024];
            DatagramPacket packet = new DatagramPacket(packSize, packSize.length);
            System.out.println("wait for connect");
            socket.receive(packet);
            String line = new String(packSize, 0, packSize.length);
            String[] msg = line.split(" ");
            line = "";
            for(int i = 0; i < (msg.length-1); i++){
                line = line + msg[i] + " ";
            }
            System.out.println("massage : " + line);

            //switch
            if(msg[0].equalsIgnoreCase("get")){
                get(socket,packet,msg[1]);
            }else if(msg[0].equalsIgnoreCase("put")){
                put(socket, packet, packSize, msg[1]);
//                socket = new DatagramSocket(11111);
            }else if(msg[0].equalsIgnoreCase("remap")){
                remap(socket,packet, line);
            }else{
                massage(socket, packet, line);
            }



            if(msg[0].equalsIgnoreCase("close")){
                socket.close();
                break;
            }
            //socket.close();
        }

    }

    /**
     * put method used to put the file from client to server
     * @param socket
     * the socket of this server
     * @param packet
     * the packet use to send by the socket
     * @param packSize
     * the packet size of each packet
     * @param fileName
     * the file name ready to upload to server
     * @throws IOException
     * throw the IOException
     */

    private static void put(DatagramSocket socket, DatagramPacket packet,byte[] packSize, String fileName) throws IOException {
        File file = new File("2" + fileName);
        FileOutputStream output = new FileOutputStream(file);
        socket.setSoTimeout(500);

        socket.receive(packet);
        System.out.println("file size is: " + new String(packSize,0,packet.getLength()));
        int LEN = Integer.parseInt(new String(packSize,0,packet.getLength()));


        try{

            while(LEN > 0){
                packSize = new byte[1024];
                packet = new DatagramPacket(packSize, packSize.length, packet.getAddress(), packet.getPort());
                socket.receive(packet);
                output.write(packSize, 0, packet.getLength());
//                System.out.println(packet.getLength());
                LEN = LEN - packet.getLength();
//                System.out.println("now is : " + LEN);
                output.flush();

                //send the ACK packet
                packSize = "ACK".getBytes();
                packet = new DatagramPacket(packSize, packSize.length, packet.getAddress(), packet.getPort());
                socket.send(packet);

            }
            socket.close();

        }catch(IOException e){
            socket.close();
            System.out.println("Did not receive data. Terminating.");
        }

    }

    /**
     * get method use to download the file from server to client
     * @param socket
     * the socket of the server
     * @param packet
     * the packet use to send by the socket
     * @param fileName
     * the file name ready to download to client
     * @throws IOException
     * throw the IOException
     * @throws InterruptedException
     * throw the InterruptedException
     */
    private static void get(DatagramSocket socket, DatagramPacket packet, String fileName) throws IOException, InterruptedException {
        byte[] sendPacket = new byte[1024];
        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        try{
            File file = new File(fileName);
            InputStream inputStream = new FileInputStream(file);

            sendPacket = (file.length() + "").getBytes();
            packet =  new DatagramPacket(sendPacket, sendPacket.length, address, port);
            socket.send(packet);

            socket.setSoTimeout(5000);
            sendPacket = new byte[1024];

            try{
                while ((inputStream.read(sendPacket)) != -1){
                    packet =  new DatagramPacket(sendPacket, sendPacket.length, address, port);
                    socket.send(packet);
//                    System.out.println(sendPacket.length);
//                TimeUnit.MILLISECONDS.sleep(10);

                    socket.receive(packet); //Wait for ACK packet

//                    System.out.println("file is: " + new String(sendPacket,0,packet.getLength()));


                }
            }catch(IOException e){
                System.out.println("Did not receive ACK. Terminating.");
            }

            TimeUnit.MILLISECONDS.sleep(500);
        }catch(IOException e){
            byte[] report = "File not exist".getBytes();
            packet = new DatagramPacket(sendPacket, sendPacket.length, address, port);
            socket.send(packet);
        }
        socket.close();

    }

    /**
     * remap the file data in a new file on server
     * @param socket
     * the socket of the server
     * @param packet
     * the packet use to send by the socket
     * @param input
     * the command from the client which should look like $remap [number] [filename]
     * @throws IOException
     * throw the IOException
     */
    private static void remap(DatagramSocket socket, DatagramPacket packet, String input) throws IOException {

        String[] msg = input.split(" ");
        int moveN = Integer.parseInt(msg[1]);
        String[] fileName = msg[2].split("\\.");

        BufferedReader bufferedReader = new BufferedReader(new FileReader(msg[2]));
        PrintWriter out = new PrintWriter(new FileWriter(fileName[0] + "_remap." + fileName[1]),true);

        char ch;
        while(bufferedReader.ready()){
            ch = (char)bufferedReader.read();
            if((int)ch > 96 && (int)ch < 123){
                ch = (char)((int)ch + moveN);
                if((int)ch < 97){
                    ch = (char)((int)ch + 26);
                }else if((int)ch > 122){
                    ch = (char)((int)ch - 26);
                }
            }

            out.print(ch);
        }

        InetAddress address = packet.getAddress();
        int port = packet.getPort();
//        System.out.println("new file is: " + fileName[0] + "_remap." + fileName[1]);
        String output = "Output file is: " + fileName[0] + "_remap." + fileName[1] + " ";
        byte[] report = output.getBytes();
        DatagramPacket reportPacket = new DatagramPacket(report, report.length, address, port);
        socket.send(reportPacket);
        socket.close();
    }


    /**
     * the message use to receive the massage from client
     * @param socket
     * the socket of the server
     * @param packet
     * the packet use to send by the socket
     * @param input
     * the massage from the client
     * @throws IOException
     * theow the IOException
     */
    private static void massage(DatagramSocket socket, DatagramPacket packet, String input) throws IOException {
        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        byte[] report = input.getBytes();
        DatagramPacket reportPacket = new DatagramPacket(report, report.length, address, port);
        socket.send(reportPacket);
        socket.close();
    }

}
