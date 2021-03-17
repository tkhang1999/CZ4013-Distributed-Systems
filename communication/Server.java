package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import backend.FacilityManager;
import backend.Message;
import backend.RegisteredClientInfo;
import backend.WeekDay;
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
        boolean updated = false;
        String updatedFacility = null;
        Server server = new Server(Constants.SERVER_PORT, Constants.INVOCATION_METHOD);
        // request packet from client
        DatagramPacket requestPacket = null;

        // initialize FacilityManager
        FacilityManager manager = FacilityManager.getInstance();
        manager.initializeDummyData();

        while (!end) {
            try {
                requestPacket = server.receive();
                boolean handled = false;
                Response response = null;
                byte[] buffer = null;

                // get general request and details from client
                Request request = (Request) Unmarshaller.unmarshal(requestPacket.getData());
                InetAddress clientAddress = requestPacket.getAddress();
                int clientPort = requestPacket.getPort();

                if (server.invocation == InvocationMethod.AT_MOST_ONCE) {
                    handled = server.handleDuplicateRequest(request, clientAddress, clientPort);
                }
    
                if (!handled) {
                    // get specific request based on request method
                    System.out.println(request.type);
                    switch(RequestType.fromString(request.type)) {
                        case TEST:
                            TestRequest t = (TestRequest) request;
                            System.out.println("request id: " + t.id + ", request method: " + t.type
                                + ", request content: " + Arrays.toString(t.content));
                            response = new TestResponse(request.id, Status.OK.label, "received");
                            server.requestResponseMap.put(request.id, response);
                            buffer = Marshaller.marshal((TestResponse) response);
                            server.send(buffer, clientAddress, clientPort);
                            break;
                        case AVAILABILITY:
                            AvailabilityRequest a = (AvailabilityRequest) request;
                            List<WeekDay> days = Arrays.stream(a.days)
                                .mapToObj(i -> WeekDay.fromInt(i)).collect(Collectors.toList());
                            String content = manager.getAvailabilityInString(a.facility, days);
                            response = new AvailabilityResponse(a.id, Status.OK.label, content);
                            buffer = Marshaller.marshal((AvailabilityResponse) response);
                            server.send(buffer, clientAddress, clientPort);
                            break;
                        case BOOK:
                            BookRequest b = (BookRequest) request;
                            Message bMess = manager.bookFacility(clientAddress.toString(), b.facility, WeekDay.fromInt(b.day), b.time);
                            response = new BookResponse(b.id, Status.OK.label, bMess.getMessage());
                            buffer = Marshaller.marshal((BookResponse) response);
                            server.send(buffer, clientAddress, clientPort);
                            if (bMess.getStatus()) {
                                updated = true;
                                updatedFacility = b.facility;
                            }
                            break;
                        case SHIFT:
                            ShiftRequest s = (ShiftRequest) request;
                            Message sMess = manager.shiftBooking(clientAddress.toString(), s.bookingId, s.postpone == 0, s.period);
                            response = new ShiftResponse(s.id, Status.OK.label, sMess.getMessage());
                            buffer = Marshaller.marshal((ShiftResponse) response);
                            server.send(buffer, clientAddress, clientPort);
                            if (sMess.getStatus()) {
                                updated = true;
                                updatedFacility = "PDR 1";
                            }
                            break;
                        case REGISTER:
                            RegisterRequest r = (RegisterRequest) request;
                            Message rMess = manager.registerUser(clientAddress.getHostAddress(), clientPort, r.facility, r.interval*60);
                            response = new RegisterResponse(r.id, Status.OK.label, rMess.getMessage(), r.interval);
                            buffer = Marshaller.marshal((RegisterResponse) response);
                            server.send(buffer, clientAddress, clientPort);
                            break;
                        default: break;
                    }
                }

                if (updated) {
                    Hashtable<String, Set<RegisteredClientInfo>> mapFacilityUser = manager.getMapFacilityUser();
                    for (RegisteredClientInfo info : mapFacilityUser.get(updatedFacility)) {
                        clientAddress = InetAddress.getByName(info.getClientIP());
                        clientPort = info.getPort();
                        String mess = manager.callBack(updatedFacility);
                        response = new RegisterResponse(IdGenerator.getNewId(), Status.OK.label, mess, info.getInterval());
                        buffer = Marshaller.marshal((RegisterResponse) response);
                        server.send(buffer, clientAddress, clientPort);
                    }
                }

            } catch (IOException ioe) {
                ioe.printStackTrace();
                end = true;
            }
        }

        server.end();
    }

    private boolean handleDuplicateRequest(Request request, InetAddress address, int port) throws IOException {
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
                case AVAILABILITY:
                    buffer = Marshaller.marshal((AvailabilityResponse) response);
                    this.send(buffer, address, port);
                    break;
                case BOOK:
                    buffer = Marshaller.marshal((BookResponse) response);
                    this.send(buffer, address, port);
                    break;
                case SHIFT:
                    buffer = Marshaller.marshal((ShiftResponse) response);
                    this.send(buffer, address, port);
                    break;
                case REGISTER:
                    buffer = Marshaller.marshal((RegisterResponse) response);
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
