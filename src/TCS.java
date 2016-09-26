import java.io.*;
import java.net.*;
import java.util.ArrayList;

class TCS{
    public static int GN = 25;
    public static ArrayList<TRSNode> nodes = new ArrayList<TRSNode>();

    public static void main(String args[]) throws Exception{
        int TCSport = 58000 + GN;
        if(args.length >= 2){
            for(int i = 0; i < args.length-1; i++){
                if(args[i].equals("-p")){
                    TCSport = Integer.parseInt(args[i+1]);
                }
            }
        }
        	 
        DatagramSocket serverSocket = new DatagramSocket(TCSport);

        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];

        while(true){
        	java.util.Arrays.fill(receiveData, (byte) 0);
        	java.util.Arrays.fill(sendData, (byte) 0);
        	
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String( receivePacket.getData());
            System.out.println("RECEIVED: " + sentence);

            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();

            String capitalizedSentence = sentence.toUpperCase();
            sendData = capitalizedSentence.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);
        }
    }
}
