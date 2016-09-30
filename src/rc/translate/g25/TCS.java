package rc.translate.g25;


import java.io.*;
import java.net.*;
import java.util.ArrayList;

class TCS{
	
	private static Integer TCSPort = new Integer(58025);
    
    /*private ArrayList<TRSNode> nodes = new ArrayList<TRSNode>();*/
    
    public static void main(String args[]) throws Exception{
    	
        if(args.length==2 && args[0].equals("-p")){
            setTCSPort(Integer.parseInt(args[1]));
        }
	    
        System.out.println("porta ->  " + getTCSPort());
        	 
        DatagramSocket serverSocket = new DatagramSocket(getTCSPort());

        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];

        while(true){
        	
        	java.util.Arrays.fill(receiveData, (byte) 0); //Enche tudo com zeros
        	
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); //Inicializa	
            serverSocket.receive(receivePacket); // BLoqueante ate receber
            
            String sentence = new String(receivePacket.getData());
            sentence = sentence.substring(0,sentence.indexOf(0)); //criar string ate primeiro zero
            
            System.out.println("\n" +"RECEIVED: " + sentence);

            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();

            String capitalizedSentence = sentence.toUpperCase();
            sendData = capitalizedSentence.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, capitalizedSentence.length(), IPAddress, port);
            serverSocket.send(sendPacket);
        }
    }
    
    public static void setTCSPort(int a){
    	TCSPort = a;
    	return;
    }
    
    public static Integer getTCSPort(){
    	return TCSPort;
    }
}
