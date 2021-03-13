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
        // create a list of received request ids, 
        // which will be required in case At-most-once invocation method is used
        Map<Integer, Response> requestResponseMap = new HashMap<>();
        // select invocation method to be used
        InvocationMethod invocation = InvocationMethod.AT_MOST_ONCE;
        // create receive and reply packets
        DatagramPacket receivePacket = null;
        DatagramPacket replyPacket = null;

        // create a socket to listen at port 2222
        DatagramSocket socket = new DatagramSocket(Constants.SERVER_PORT);

        while (true) {
            // create a byte buffer for receiving and sending data
            byte[] buffer = new byte[512];
            // create a DatgramPacket to receive the data
            receivePacket = new DatagramPacket(buffer, buffer.length);
            // recieve the data in byte buffer and return a response
            socket.receive(receivePacket);

            // get a general request from client
            Request request = (Request) Unmarshaller.unmarshal(receivePacket.getData());
            // create a general response
            Response response = null;

            // if At-most-once invocation method is not used or there is no duplicate request
            if (!invocation.filterDuplicates || !requestResponseMap.keySet().contains(request.id)) {
                // check request method to get the specific request,
                // and construct the respective response accordingly
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

                // store the response if at-most-once invocation method is used
                if (invocation.filterDuplicates) {
                    requestResponseMap.put(request.id, response);
                }
            } 
            // if duplicate request is found, retransmit stored reply message (response)
            else {
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
