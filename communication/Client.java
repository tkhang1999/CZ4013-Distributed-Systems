package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import backend.WeekDay;
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

        // display welcome message and list of services
        System.out.println("\n***************************************************");
        System.out.println("******WELCOME TO OUR FACILITY BOOKING SERVICE******");
        System.out.println("***************************************************\n");

        boolean end = false;

        while (!end) {
            String services = "----------------------------------------------------------------\n" +
                "Please choose a service by typing [0-6]:\n" +
                "1: Query the availability of the facility\n" +
                "2: Book a time slot at the facility\n" +
                "3: Postpone or advance the booking time\n" +
                "4: Register to receive availability updates of the facility\n" +
                "5: Cancel your booking\n" +
                "6: Extend your booking\n" +
                "0: Stop the client\n";
            System.out.println(services);
    
            // read user's input for service type
            String input = sc.nextLine().trim();
            int serviceType = Integer.parseInt(input);

            // buffer for request and response
            byte[] requestBuffer;
            byte[] responseBuffer;

            switch (serviceType) {
                case Constants.SAMPLE_SERVICE:
                	try {
                		requestBuffer = constructTestRequest(sc);
                	} catch (Exception e) {
                		System.out.println("Cannot construct request due to invalid input");
                		break;
                	}
                    responseBuffer = client.sendAndReceive(requestBuffer);
                    handleTestResponse(responseBuffer);
                    break;
                case Constants.AVAILABILITY_SERVICE:
                	try {
	                    requestBuffer = constructAvailabilityRequest(sc);
		            } catch (Exception e) {
		        		System.out.println("Cannot construct request due to invalid input");
		        		break;
		        	}
                    responseBuffer = client.sendAndReceive(requestBuffer);
                    handleAvailabilityResponse(responseBuffer);
                    break;
                case Constants.BOOK_SERVICE:
                	try {
                		requestBuffer = constructBookRequest(sc);
                	} catch (Exception e) {
                		System.out.println("Cannot construct request due to invalid input");
                		break;
                	}
                    responseBuffer = client.sendAndReceive(requestBuffer);
                    handleBookResponse(responseBuffer);
                    break;
                case Constants.SHIFT_SERVICE:
                	try {
                		requestBuffer = constructShiftRequest(sc);
                	} catch (Exception e) {
                		System.out.println("Cannot construct request due to invalid input");
                		break;
                	}
                    responseBuffer = client.sendAndReceive(requestBuffer);
                    handleShiftResponse(responseBuffer);
                    break;
                case Constants.REGISTER_SERVICE:
                	try {
                		requestBuffer = constructRegisterRequest(sc);
                	} catch (Exception e) {
                		System.out.println("Cannot construct request due to invalid input");
                		break;
                	}
                    responseBuffer = client.sendAndReceive(requestBuffer);
                    handleRegisterResponse(responseBuffer, client);
                    break;
                case Constants.CANCEL_SERVICE:
                	try {
                		requestBuffer = constructCancelRequest(sc);
                	} catch (Exception e) {
                		System.out.println("Cannot construct request due to invalid input");
                		break;
                	}
                	responseBuffer = client.sendAndReceive(requestBuffer);
                	handleCancelResponse(responseBuffer);
                    break;
                case Constants.EXTEND_SERVICE:
                	try {
                		requestBuffer = constructExtendRequest(sc);
                	} catch (Exception e) {
                		System.out.println("Cannot construct request due to invalid input");
                		break;
                	}
                	responseBuffer = client.sendAndReceive(requestBuffer);
                	handleExtendResponse(responseBuffer);
                    break;
                case Constants.END_SERVICE:
                    end = true;
                    System.out.println("Ending client!");
                    break;
                default: 
                    System.out.println("Invalid service! Please select a service again!");
                    break;
            }
        }

        sc.close();
        client.end();
    }

    private static byte[] constructTestRequest(Scanner sc) {
        // String input = sc.nextLine().trim();
        float[] content = {15, 24, 6};
        // SAMPLE request
        TestRequest request = new TestRequest(IdGenerator.getNewId(), content);
        // For DUPLICATE requests
        // TestRequest request = new TestRequest(152406, content);
        byte[] buffer = Marshaller.marshal(request);

        return buffer;
    }

    private static byte[] constructAvailabilityRequest(Scanner sc) {
        System.out.println("Enter facility name: ");
        String facility = sc.nextLine().trim();
    
        String days = "----------------------------------------------------------------\n" +
        "Please choose one or multiple days:\n" +
            WeekDay.MONDAY.getIntValue() + ": " + WeekDay.MONDAY.toString() + "\n" +
            WeekDay.TUESDAY.getIntValue() + ": " + WeekDay.TUESDAY.toString() + "\n" +
            WeekDay.WEDNESDAY.getIntValue() + ": " + WeekDay.WEDNESDAY.toString() + "\n" +
            WeekDay.THURSDAY.getIntValue() + ": " + WeekDay.THURSDAY.toString() + "\n" +
            WeekDay.FRIDAY.getIntValue() + ": " + WeekDay.FRIDAY.toString() + "\n" +
            WeekDay.SATURDAY.getIntValue() + ": " + WeekDay.SATURDAY.toString() + "\n" +
            WeekDay.SUNDAY.getIntValue() + ": " + WeekDay.SUNDAY.toString() + "\n";
            System.out.println(days);
        System.out.println("Enter a list of chosen days (seperated by spaces): ");
        int[] selectedDays = Arrays.asList(sc.nextLine().trim().split("[ ]+")).stream()
            .mapToInt(Integer::parseInt).toArray();

        AvailabilityRequest request = new AvailabilityRequest(IdGenerator.getNewId(), facility, selectedDays);
        byte[] buffer = Marshaller.marshal(request);

        return buffer;
    }

    private static byte[] constructShiftRequest(Scanner sc) {
        System.out.println("Enter booking id: ");
        String bookingId = sc.nextLine().trim();
        System.out.println("Postpone(0) or Advance(1): ");
        int postpone = Integer.parseInt(sc.nextLine().trim());
        System.out.println("Shift period (minutes): ");
        int period = Integer.parseInt(sc.nextLine().trim());
        
        ShiftRequest request = new ShiftRequest(IdGenerator.getNewId(), bookingId, postpone, period);
        byte[] buffer = Marshaller.marshal(request);

        return buffer;
    }

    private static byte[] constructBookRequest(Scanner sc) {
        System.out.println("Enter facility name: ");
        String facility = sc.nextLine().trim();
        String days = "\n----------------------------------------------------------------\n" +
            "Please choose one day:\n" +
            WeekDay.MONDAY.getIntValue() + ": " + WeekDay.MONDAY.toString() + "\n" +
            WeekDay.TUESDAY.getIntValue() + ": " + WeekDay.TUESDAY.toString() + "\n" +
            WeekDay.WEDNESDAY.getIntValue() + ": " + WeekDay.WEDNESDAY.toString() + "\n" +
            WeekDay.THURSDAY.getIntValue() + ": " + WeekDay.THURSDAY.toString() + "\n" +
            WeekDay.FRIDAY.getIntValue() + ": " + WeekDay.FRIDAY.toString() + "\n" +
            WeekDay.SATURDAY.getIntValue() + ": " + WeekDay.SATURDAY.toString() + "\n" +
            WeekDay.SUNDAY.getIntValue() + ": " + WeekDay.SUNDAY.toString() + "\n";
        System.out.println(days);
        System.out.println("Enter day: ");
        int day = Integer.parseInt(sc.nextLine().trim());
        System.out.println("Enter start and end time (between 0:00 and 23:59): ");
        String time = sc.nextLine().trim();

        BookRequest request = new BookRequest(IdGenerator.getNewId(), facility, day, time);
        byte[] buffer = Marshaller.marshal(request);

        return buffer;
    }

    private static byte[] constructRegisterRequest(Scanner sc) {
        System.out.println("Enter facility name: ");
        String facility = sc.nextLine().trim();
        System.out.println("Enter monitor interval (in minutes): ");
        int interval = Integer.parseInt(sc.nextLine().trim());

        RegisterRequest request = new RegisterRequest(IdGenerator.getNewId(), facility, interval);
        byte[] buffer = Marshaller.marshal(request);

        return buffer;
    }
    
    private static byte[] constructCancelRequest(Scanner sc) {
    	System.out.println("Enter booking id: ");
    	String bookingId = sc.nextLine().trim();
    	CancelRequest request = new CancelRequest(IdGenerator.getNewId(), bookingId);
        byte[] buffer = Marshaller.marshal(request);
        return buffer;
    }
    
    private static byte[] constructExtendRequest(Scanner sc) {
    	System.out.println("Enter booking id: ");
        String bookingId = sc.nextLine().trim();
        System.out.println("sooner(0) or later(1): ");
        int sooner = Integer.parseInt(sc.nextLine().trim());
        System.out.println("Extend period (minutes): ");
        int period = Integer.parseInt(sc.nextLine().trim());
        
        ExtendRequest request = new ExtendRequest(IdGenerator.getNewId(), bookingId, sooner, period);
        byte[] buffer = Marshaller.marshal(request);

        return buffer;
    }

    private static void handleTestResponse(byte[] responseBuffer) {
        TestResponse response = (TestResponse) Unmarshaller.unmarshal(responseBuffer);
        System.out.println("response id: " + response.id + ", response status: " + response.status
            + ", response content: " + response.content);
    }

    private static void handleAvailabilityResponse(byte[] responseBuffer) {
        AvailabilityResponse response = (AvailabilityResponse) Unmarshaller.unmarshal(responseBuffer);
        if (! response.status.equals(Status.OK.label)) {
        	System.out.println(response.status);
        	System.out.println(response.content);
        	return;
        }
        System.out.println("\nFacility availability:");
        System.out.println(response.content);
    }

    private static void handleBookResponse(byte[] responseBuffer) {
        BookResponse response = (BookResponse) Unmarshaller.unmarshal(responseBuffer);
        System.out.println("\nBooking status:");
        System.out.println(response.status);
        System.out.println(response.content);
    }

    private static void handleShiftResponse(byte[] responseBuffer) {
        ShiftResponse response = (ShiftResponse) Unmarshaller.unmarshal(responseBuffer);
        System.out.println("\nShift status:");
        System.out.println(response.status);
        System.out.println(response.content);
    }

    private static void handleRegisterResponse(byte[] responseBuffer, Client client) throws IOException, SocketException {
        RegisterResponse response = (RegisterResponse) Unmarshaller.unmarshal(responseBuffer);
        if (! response.status.equals(Status.OK.label)) {
        	System.out.println(response.status);
        	System.out.println(response.content);
        	return;
        }
        System.out.println("\nRegister status:");
        System.out.println(response.status);
        System.out.println(response.interval);
        System.out.println(response.content);

        long current = System.currentTimeMillis();
        long end = current + response.interval*60000;
        while(current < end) {
            try {
                System.out.println("\nWaiting for notification messages...!");
                client.socket.setSoTimeout(Long.valueOf(end - current).intValue());
                byte[] buffer = client.receive();
                response = (RegisterResponse) Unmarshaller.unmarshal(buffer);
                System.out.println("\nRegistered notification:");
                System.out.println(response.status);
                System.out.println(response.interval);
                System.out.println(response.content);
            } catch (SocketTimeoutException ste) {
            	System.out.println("Monitor interval passed");
            }
            current = System.currentTimeMillis();
        }

        client.socket.setSoTimeout(Constants.TIME_OUT);
    }
    
    private static void handleCancelResponse(byte[] responseBuffer) {
    	CancelResponse response = (CancelResponse) Unmarshaller.unmarshal(responseBuffer);
    	System.out.println("\nCancel status:");
    	System.out.println(response.status);
    	System.out.println(response.content);
    }
    
    private static void handleExtendResponse(byte[] responseBuffer) {
    	ExtendResponse response = (ExtendResponse) Unmarshaller.unmarshal(responseBuffer);
    	System.out.println("\nExtend status:");
    	System.out.println(response.status);
    	System.out.println(response.content);
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