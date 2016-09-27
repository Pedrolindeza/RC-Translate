package rc.translate.g25;


import java.io.*;
import java.net.*;
import java.util.ArrayList;

class TCS{
    
    private static ArrayList<TRSNode> nodes = new ArrayList<TRSNode>();
    
    public static void main(String args[]) throws Exception{
    	
        int TCSport = 58025;
        
        if(args.length==2 && args[0].equals("-p")){
            TCSport = Integer.parseInt(args[1]);
        }
	    
        	 
        DatagramsSocket serverSocket = new DatagramSocket(TCSport);

        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];

        while(true){
        	java.util.Arrays.fill(receiveData, (byte) 0);
        	
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            
            String sentence = new String(receivePacket.getData());
            sentence = sentence.substring(0,sentence.indexOf(0));
            
            System.out.println("RECEIVED: " + sentence);

            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();

            String capitalizedSentence = sentence.toUpperCase();
            sendData = capitalizedSentence.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, capitalizedSentence.length(), IPAddress, port);
            serverSocket.send(sendPacket);
        }
    }
}
