package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import marshalling.Marshaller;
import marshalling.Unmarshaller;

/**
 * The {@code Server} class for the server of the application
 */
public class Server {

    private DatagramSocket socket;
    private InvocationMethod invocation;
    // create a list of received request ids, 
    // which will be required in case At-most-once invocation method is used
    private Map<Integer, Response> requestResponseMap;
    private double failProb;

    private Server(int port, InvocationMethod invocation) throws SocketException {
        this.socket = new DatagramSocket(port);
        this.invocation = invocation;
        this.requestResponseMap = new HashMap<>();
        this.failProb = Constants.FAIL_PROBABILITY;
    }

    public static void main(String[] args) throws Exception {
        boolean end = false;
        Server server = new Server(Constants.SERVER_PORT, Constants.INVOCATION_METHOD);
        // request packet from client
        DatagramPacket requestPacket = null;

        while (!end) {
            try {
                requestPacket = server.receive();
                boolean handled = false;

                // get general request and details from client
                Request request = (Request) Unmarshaller.unmarshal(requestPacket.getData());
                InetAddress clientAddress = requestPacket.getAddress();
                int clientPort = requestPacket.getPort();

                if (server.invocation == InvocationMethod.AT_MOST_ONCE) {
                    handled = server.filterDuplicateRequest(request, clientAddress, clientPort);
                }
    
                if (!handled) {
                    // get specific request based on request method
                    System.out.println(request.type);
                    switch(RequestType.fromString(request.type)) {
                        case TEST:
                            TestRequest t = (TestRequest) request;
                            System.out.println("request id: " + t.id + ", request method: " + t.type
                                + ", request content: " + Arrays.toString(t.content));
                            TestResponse response = new TestResponse(request.id, Status.OK.label, "received");
                            server.requestResponseMap.put(request.id, response);
                            byte[] buffer = Marshaller.marshal((TestResponse) response);
                            server.send(buffer, clientAddress, clientPort);
                            break;
                        default: break;
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                end = true;
            }
        }

        server.end();
    }

    private boolean filterDuplicateRequest(Request request, InetAddress address, int port) throws IOException {
        boolean handled = false;

        if (this.requestResponseMap.containsKey(request.id)) {
            Response response = requestResponseMap.get(request.id);
            System.out.println("Detect duplicate request! Restransmit stored response!");

            switch(RequestType.fromString(request.type)) {
                case TEST:
                    // TestRequest t = (TestRequest) request;
                    // System.out.println("request id: " + t.id + ", request method: " + t.method
                    //     + ", request content: " + Arrays.toString(t.content));
                    byte[] buffer = Marshaller.marshal((TestResponse) response);
                    this.send(buffer, address, port);
                    break;
                default: break;
            }

            handled = true;
        }

        return handled;
    }

    private void send(byte[] response, InetAddress clientAddress, int clientPort) throws IOException {
        // send response to client
        if (Math.random() < this.failProb) {
            System.out.println("Failed response! Server drop packet!");
        } else {
            DatagramPacket packet = new DatagramPacket(response, response.length, clientAddress, clientPort);
            this.socket.send(packet);
        }
    }

    private DatagramPacket receive() throws IOException {
        // create a byte buffer for receiving request
        byte[] buffer = new byte[Constants.BUFFER_LENGTH];
        // create a DatgramPacket to receive the data
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        // recieve the data in byte buffer and return a response
        this.socket.receive(packet);

        return packet;
    }

    private void end() {
        this.socket.close();
    }
}
