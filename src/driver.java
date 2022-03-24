import java.io.IOException;
import java.util.Scanner;

public class driver {
    public static void main(String[] args) throws IOException {
//        new testing();
        client_tcp client = new client_tcp();
        client.startConnection("127.0.0.1", 6666);
        String response = client.sendMessage("hello server");
        client.sendMessage("stop");

        Scanner scanner = new Scanner(System.in);
        client.sendMessage(scanner.nextLine());
//        assertEquals("hello client", response);

    }

}
