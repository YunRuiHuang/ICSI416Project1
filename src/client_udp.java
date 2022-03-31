import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class client_udp {

    public static void main(String[] args) throws IOException {
        InetAddress address = InetAddress.getByName("127.0.0.1");
        int port = 11111;
        byte[] sendPacket = "hello ".getBytes();
        DatagramPacket reportPacket = new DatagramPacket(sendPacket, sendPacket.length, address, port);
        DatagramSocket socket = new DatagramSocket();
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
        socket.close();
    }

}
