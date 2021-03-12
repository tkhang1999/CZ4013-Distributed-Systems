package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import marshalling.Marshaller;
import marshalling.Unmarshaller;

/**
 * The {@code Client} class for the client of the application
 */
public class Client {

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);

        // create the socket object for carrying the data
        DatagramSocket socket = new DatagramSocket();

        // get the IP address of the server
        InetAddress address = InetAddress.getByName("localhost");
        // create a byte buffer for sending and receiving data
        byte[] buffer = null;

        while (true) {
            // read user's input
            String input = sc.nextLine();

            // SAMPLE request
            float[] test = {15, 24, 6};
            TestRequest request = new TestRequest(IdGenerator.getNewId(), input, test);
            buffer = Marshaller.marshal(request);

            // create the datagramPacket for sending the data
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 1234);
            // invoke the send call to actually send the data
            socket.send(packet);

            // end client session if user inputs "end"
            if (input.equals("end"))
                break;

            // receive reply from server
            buffer = new byte[512];
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            // get a general response from the server
            Response response = (Response) Unmarshaller.unmarshal(packet.getData());
            // get the specific response based on the request method
            switch (request.method) {
                case "input":
                    TestResponse tr = (TestResponse) response;
                    System.out.println("response id: " + tr.id + ", response status: " + tr.status
                        + ", response content: " + tr.content);
                    break;
                default:
                    System.out.println("response id: " + response.id + ", response status: " + response.status);
            }
        }

        sc.close();
        socket.close();
    }
}