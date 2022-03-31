import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class server_udp {

    public static void main(String[] args) throws IOException {

        DatagramSocket socket = new DatagramSocket(11111);
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

        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        byte[] report = line.getBytes();
        DatagramPacket reportPacket = new DatagramPacket(report, report.length, address, port);
        socket.send(reportPacket);
        socket.close();



    }
}
