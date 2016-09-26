package rc.translate.g25;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class User {
	private static String TCSName = "127.0.0.1";
	private static int TCSport = 58025;
	
	private static ArrayList<String> getLanguages(){
		ArrayList<String> languages = new ArrayList<String>();
		
		
	}
	
	private String sendUDPResponse(String message) throws IOException{
		InetAddress address = InetAddress.getByName(TCSName);
		
		DatagramSocket clientSocket = new DatagramSocket();
		byte[] sendData = new byte[message.length()];
		byte[] receiveData = new byte[1024];
		
    	java.util.Arrays.fill(receiveData, (byte) 0);
		
		sendData = message.getBytes();
		
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, TCSport);
		clientSocket.send(sendPacket);
		
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	    clientSocket.receive(receivePacket);
	    
	    String response = new String(receivePacket.getData());
	    response = response.substring(0, response.indexOf(0));
	    
	    clientSocket.close();
	    
	    return response;
	}
	
	public static void main(String args[]){		
        for(int i = 0; i < args.length-1; i++){
            if(args[i].equals("-p")){
                TCSport = Integer.parseInt(args[i+1]);
            }
        }
	}
}
