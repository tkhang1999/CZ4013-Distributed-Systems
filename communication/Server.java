package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import marshalling.Marshaller;
import marshalling.Unmarshaller;

public class Server {
    public static void main(String[] args) throws IOException {
        // Step 1 : Create a socket to listen at port 1234
        DatagramSocket socket = new DatagramSocket(1234);

        // these 2 packets can be the same, no need to be 2 separate objects
        DatagramPacket receivePacket = null;
        DatagramPacket replyPacket = null;

        while (true) {
            byte[] buffer = new byte[512];

            // Step 2 : create a DatgramPacket to receive the data.
            receivePacket = new DatagramPacket(buffer, buffer.length);

            // Step 3 : revieve the data in byte buffer.
            socket.receive(receivePacket);

            // String msg = new String(receivePacket.getData(), 0, receivePacket.getLength());
            Request request = (Request) Unmarshaller.unmarshal(receivePacket.getData(), new Request());
            System.out.println("request id: " + request.id + ", request method: " + request.method
                + ", request content: " + Arrays.toString(request.content));

            // exit the server if the client sends "bye"
            if (request.method.equals("end"))
            {
                System.out.println("Client sent 'end' request. Ending!");
                break;
            }

            // reply to client
            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();

            Response response = new Response(request.id, Status.OK.toString(), "received");
            buffer = Marshaller.marshal(response);

            replyPacket = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
            socket.send(replyPacket);            
        }

        socket.close();
    }
}
