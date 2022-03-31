import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class server_udp {

    public static void main(String[] args) throws IOException {

        DatagramSocket socket = new DatagramSocket(11111);


        while (true){
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

            }else if(msg[0].equalsIgnoreCase("put")){

            }else if(msg[0].equalsIgnoreCase("remap")){

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


    private static void massage(DatagramSocket socket, DatagramPacket packet, String input) throws IOException {
        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        byte[] report = input.getBytes();
        DatagramPacket reportPacket = new DatagramPacket(report, report.length, address, port);
        socket.send(reportPacket);
    }

}
