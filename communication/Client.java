package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import marshalling.Marshaller;
import marshalling.Unmarshaller;

/**
 * The {@code Client} class for the UDP client of the application
 */
public class Client {

    private DatagramSocket socket;
    private InetAddress address;
    private int serverPort;
    private int maxTries;
    private double failProb;

    public Client(String address, int serverPort) throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket();
        this.address = InetAddress.getByName(address);
        this.serverPort = serverPort;
        this.maxTries = Constants.MAX_TRIES;
        this.failProb = Constants.FAIL_PROBABILITY;
        socket.setSoTimeout(Constants.TIME_OUT);
    }

    public static void main(String[] args) throws Exception {

        String address = Constants.SERVER_ADDRESS;
        int serverPort = Constants.SERVER_PORT;

        // initiate an UDP client instance
        Client client = new Client(address, serverPort);
        // initiate a scanner for reading user's input
        Scanner sc = new Scanner(System.in);

        boolean end = false;

        while (!end) {
            // read user's input
            String input = sc.nextLine();
            int serviceType = Integer.parseInt(input);

            // buffer for request and response
            byte[] requestBuffer;
            byte[] responseBuffer;

            switch (serviceType) {
                case Constants.SAMPLE_SERVICE:
                    requestBuffer = constructSampleServiceRequest(sc);
                    responseBuffer = client.sendAndReceive(requestBuffer);
                    handleSampleSericeResponse(responseBuffer);
                    break;
                case Constants.END_SERVICE:
                    end = true;
                    System.out.println("Ending client!");
                    break;
                default: 
                    System.out.println("Invalid service! Please select a service again!");
                    break;
            }
            // // end client session if user inputs "end"
            // if (input.equals("end"))
            //     break;

            // // get a general response from the server
            // Response response = (Response) Unmarshaller.unmarshal(packet.getData());
            // // get the specific response based on the request method
            // switch (request.method) {
            //     case "input":
            //         TestResponse tr = (TestResponse) response;

            //         break;
            //     default:
            //         System.out.println("response id: " + response.id + ", response status: " + response.status);
            // }
        }

        sc.close();
        client.end();
    }

    private static byte[] constructSampleServiceRequest(Scanner sc) {
        // String input = sc.nextLine();
        float[] content = {15, 24, 6};
        // SAMPLE request
        // TestRequest request = new TestRequest(IdGenerator.getNewId(), Constants.SAMPLE_SERVICE_METHOD, content);
        // For DUPLICATE requests
        TestRequest request = new TestRequest(152406, Constants.SAMPLE_SERVICE_METHOD, content);
        byte[] buffer = Marshaller.marshal(request);

        return buffer;
    }

    private static void handleSampleSericeResponse(byte[] responseBuffer) {
        TestResponse response = (TestResponse) Unmarshaller.unmarshal(responseBuffer);
        System.out.println("response id: " + response.id + ", response status: " + response.status
            + ", response content: " + response.content);
    }

    private void send(byte[] request) throws IOException {
        // send request to server
        if (Math.random() < this.failProb) {
            System.out.println("Failed request! Client drop packet!");
        } else {
            DatagramPacket packet = new DatagramPacket(request, request.length, this.address, this.serverPort);
            this.socket.send(packet);
        }
    }

    private byte[] receive() throws IOException{
        // receive response from server
        byte[] buffer = new byte[Constants.BUFFER_LENGTH];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        this.socket.receive(packet);

        return packet.getData();
    }

    private byte[] sendAndReceive(byte[] request) throws IOException, TimeoutException {
        byte[] resposne = null;
        int tries = 0;

        while (tries < this.maxTries) {
            try {
                this.send(request);
                resposne = this.receive();
                break;
            } catch (SocketTimeoutException se) {
                tries++;
                if (tries == this.maxTries) {
                    throw new TimeoutException("Timeout after max " + tries + " tries!");
                }
                System.out.println("Retrying " + tries + "...!");
            }
        }

        return resposne;
    }

    private void end() {
        this.socket.close();
    }
}