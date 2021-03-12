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

        // Step 1: create the socket object for carrying the data
        DatagramSocket socket = new DatagramSocket();

        InetAddress address = InetAddress.getByName("localhost");    //IP address of server
        byte[] buffer = null;

        while (true) {
            String input = sc.nextLine();

            // convert the input into the Request object.
            // buffer = input.getBytes();

            float[] test = {15, 24, 6};
            TestRequest request = new TestRequest(IdGenerator.getNewId(), input, test);
            buffer = Marshaller.marshal(request);

            // Step 2: create the datagramPacket for sending the data
            DatagramPacket packet =
                    new DatagramPacket(buffer, buffer.length, address, 1234);

            // Step 3: invoke the send call to actually send the data
            socket.send(packet);

            // break the loop if user enters "end"
            if (input.equals("end"))
                break;

            // receive reply from server
            buffer = new byte[512];
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            Response response = (Response) Unmarshaller.unmarshal(packet.getData());

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