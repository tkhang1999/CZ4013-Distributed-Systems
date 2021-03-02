package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);

        // Step 1:Create the socket object for
        // carrying the data.
        DatagramSocket socket = new DatagramSocket();

        InetAddress address = InetAddress.getByName("localhost");    //IP address of server
        byte[] buffer = null;

        // loop while user not enters "bye"
        while (true)
        {
            String input = sc.nextLine();

            // convert the String input into the byte array.
            buffer = input.getBytes();

            // Step 2 : Create the datagramPacket for sending
            // the data.
            DatagramPacket packet =
                    new DatagramPacket(buffer, buffer.length, address, 1234);

            // Step 3 : invoke the send call to actually send
            // the data.
            socket.send(packet);

            // break the loop if user enters "bye"
            if (input.equals("end"))
                break;

            // receive reply from server
            buffer = new byte[512];
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String replyMsg = new String(packet.getData(), 0, packet.getLength());

            System.out.println("Server: " + replyMsg);
        }
    }
}