package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import marshalling.Marshaller;
import marshalling.Unmarshaller;

/**
 * The {@code Server} class for the server of the application
 */
public class Server {
    public static void main(String[] args) throws IOException {
        // List of received request ids, which will be required in case At-most-once invocation method is used
        Map<Integer, Response> requestResponseMap = new HashMap<>();
        // Select invocation method to be used
        InvocationMethod invocation = InvocationMethod.AT_MOST_ONCE;

        // Step 1: create a socket to listen at port 1234
        DatagramSocket socket = new DatagramSocket(1234);

        // these 2 packets can be the same, no need to be 2 separate objects
        DatagramPacket receivePacket = null;
        DatagramPacket replyPacket = null;

        while (true) {
            byte[] buffer = new byte[512];

            // Step 2: create a DatgramPacket to receive the data
            receivePacket = new DatagramPacket(buffer, buffer.length);

            // Step 3: recieve the data in byte buffer
            socket.receive(receivePacket);

            Request request = (Request) Unmarshaller.unmarshal(receivePacket.getData());
            Response response = null;

            // if At-most-once invocation method is used, check for duplicate request
            if (!invocation.filterDuplicates || !requestResponseMap.keySet().contains(request.id)) {
                switch (request.method) {
                    case "input":
                        TestRequest t = (TestRequest) request;
                        System.out.println("request id: " + t.id + ", request method: " + t.method
                            + ", request content: " + Arrays.toString(t.content));
                        response = new TestResponse(request.id, Status.OK.toString(), "received");
                        buffer = Marshaller.marshal((TestResponse) response);
                        break;
                    case "end":
                        System.out.println("Client sent 'end' request!");
                        continue;
                    default: 
                        response = new Response(request.id, Status.OK.toString());
                        buffer = Marshaller.marshal(response);
                }

                if (invocation.filterDuplicates) {
                    requestResponseMap.put(request.id, response);
                }
            } else {
                response = requestResponseMap.get(request.id);
                buffer = Marshaller.marshal(response);
            }

            // reply to client
            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();

            replyPacket = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
            socket.send(replyPacket);            
        }

        // socket.close();
    }
}
