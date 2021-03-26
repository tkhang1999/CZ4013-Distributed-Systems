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
        
        Server server = new Server(Constants.SERVER_PORT, Constants.INVOCATION_METHOD);
        // request packet from client
        DatagramPacket requestPacket = null;

        // initialize FacilityManager
        FacilityManager manager = FacilityManager.getInstance();
        manager.initializeDummyData();
        String invalid_input = "Invalid user input";
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

                System.out.println("request id: " + request.id + ", request type: " + request.type);

                if (server.invocation == InvocationMethod.AT_MOST_ONCE) {
                    handled = server.handleDuplicateRequest(request, clientAddress, clientPort);
                }
    
                if (!handled) {
                    // get specific request based on request method
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
                            try {
                                List<WeekDay> days = Arrays.stream(a.days)
                                    .mapToObj(i -> WeekDay.fromInt(i)).collect(Collectors.toList());
                                String content = manager.getAvailabilityInString(a.facility, days);
                                response = new AvailabilityResponse(a.id, Status.OK.label, content);
                            } catch (Exception e) {
                                response = new AvailabilityResponse(a.id, Status.INVALID.label, invalid_input);
                            }
                            server.requestResponseMap.put(request.id, response);
                            buffer = Marshaller.marshal((AvailabilityResponse) response);
                            server.send(buffer, clientAddress, clientPort);
                            break;
                        case BOOK:
                            BookRequest b = (BookRequest) request;
                            try {
                                Message bMess = manager.bookFacility(clientAddress.toString(), b.facility, WeekDay.fromInt(b.day), b.time);
                                response = new BookResponse(b.id, Status.OK.label, bMess.getMessage());
                            } catch (Exception e) {
                                response = new BookResponse(b.id, Status.INVALID.label, invalid_input);
                            }
                            server.requestResponseMap.put(request.id, response);
                            buffer = Marshaller.marshal((BookResponse) response);
                            server.send(buffer, clientAddress, clientPort);
                            break;
                        case SHIFT:
                            ShiftRequest s = (ShiftRequest) request;
                            try {
                                Message sMess = manager.shiftBooking(clientAddress.toString(), s.bookingId, s.postpone == 0, s.period);
                                response = new ShiftResponse(s.id, Status.OK.label, sMess.getMessage());
                            } catch (Exception e) {
                                response = new ShiftResponse(s.id, Status.INVALID.label, invalid_input);
                            }
                            server.requestResponseMap.put(request.id, response);
                            buffer = Marshaller.marshal((ShiftResponse) response);
                            server.send(buffer, clientAddress, clientPort);
                            break;
                        case REGISTER:
                            RegisterRequest r = (RegisterRequest) request;
                            try {
                                Message rMess = manager.registerUser(clientAddress.getHostAddress(), clientPort, r.facility, r.interval*60);
                                response = new RegisterResponse(r.id, Status.OK.label, rMess.getMessage(), r.interval);
                            } catch (Exception e) {
                                response = new RegisterResponse(r.id, Status.INVALID.label, invalid_input, 0);
                            }
                            server.requestResponseMap.put(request.id, response);
                            buffer = Marshaller.marshal((RegisterResponse) response);
                            server.send(buffer, clientAddress, clientPort);
                            break;
                        case CANCEL:
                            CancelRequest c = (CancelRequest) request;
                            try {
                                Message cMess = manager.cancelBooking(clientAddress.toString(), c.bookingId);
                                response = new CancelResponse(c.id, Status.OK.label, cMess.getMessage());
                            } catch (Exception e) {
                                response = new CancelResponse(c.id, Status.INVALID.label, invalid_input);
                            }
                            server.requestResponseMap.put(request.id, response);
                            buffer = Marshaller.marshal((CancelResponse) response);
                            server.send(buffer, clientAddress, clientPort);
                            break;
                        case EXTEND:
                            ExtendRequest e = (ExtendRequest) request;
                            try {
                                Message eMess = manager.extendBookingTime(clientAddress.toString(), e.bookingId, e.sooner == 0, e.period);
                                response = new ExtendResponse(e.id, Status.OK.label, eMess.getMessage());
                            } catch (Exception ex) {
                                response = new ExtendResponse(e.id, Status.INVALID.label, invalid_input);
                            }
                            server.requestResponseMap.put(request.id, response);
                            buffer = Marshaller.marshal((ExtendResponse) response);
                            server.send(buffer, clientAddress, clientPort);
                            break;
                        default: break;
                    }
                }
                
                String updatedFacility = manager.getUpdatedFacility();
                if (updatedFacility != null) {
                    Hashtable<String, Set<RegisteredClientInfo>> mapFacilityUser = manager.getMapFacilityUser();
                    if (mapFacilityUser.containsKey(updatedFacility)) {
                        for (RegisteredClientInfo info : mapFacilityUser.get(updatedFacility)) {
                        	try {
	                            clientAddress = InetAddress.getByName(info.getClientIP());
	                            clientPort = info.getPort();
	                            String mess = manager.getNotifiedMessage(updatedFacility);
	                            response = new RegisterResponse(IdGenerator.getUnsavedId(), Status.OK.label, mess, info.getInterval());
	                            buffer = Marshaller.marshal((RegisterResponse) response);
	                            
	                            int tries = 0;
	                            boolean sent = false;
	                            // re-try sending registered notification messages
	                            while (tries < Constants.MAX_TRIES && sent == false) {
	                                tries++;
	                                System.out.println("Try sending a notification message " + tries + "...!");
	                                sent = server.send(buffer, clientAddress, clientPort);
	                            }
	                            if (!sent) {
	                                System.out.println("Fail to send a notification message!\n");
	                            }
                        	} catch (Exception e) {
//                        		e.printStackTrace();
                        	}
                        }
                    }
                    manager.setUpdatedFacility(null);
                }

            } catch (IOException ioe) {
                ioe.printStackTrace();
                end = true;
            } catch (Exception e) {}
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
                case CANCEL:
                    buffer = Marshaller.marshal((CancelResponse) response);
                    this.send(buffer, address, port);
                    break;
                case EXTEND:
                    buffer = Marshaller.marshal((ExtendResponse) response);
                    this.send(buffer, address, port);
                    break;
                default: return false;
            }

            handled = true;
        }

        return handled;
    }

    private boolean send(byte[] response, InetAddress clientAddress, int clientPort) throws IOException {
        // send response to client
        if (Math.random() < this.failProb) {
            System.out.println("Fail to response! Server drop packet!\n");
            return false;
        } else {
            DatagramPacket packet = new DatagramPacket(response, response.length, clientAddress, clientPort);
            this.socket.send(packet);
            System.out.println("Succeed in sending a response!\n");
            return true;
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
