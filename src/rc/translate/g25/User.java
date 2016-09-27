package rc.translate.g25;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class User {
	private static String TCSName = "127.0.0.1";
	private static int TCSport = 58025;
	private static ArrayList<String> languagesCache;
	
	private static ArrayList<String> getLanguages() throws IOException{
		ArrayList<String> languages = new ArrayList<String>();
		
		String response = sendUDPMessage("ULQ" + "\n");
		
		String[] split = response.split(" ");
		if(split[0].equals("ULR")){
			for(int i = 2; i < split.length; i++)
				languages.add(split[i]);
		}
		
		languagesCache = languages;
		
		return languages;
	}
	
	private static TRSNode getTRSNode(String language) throws IOException{
		String response = sendUDPMessage("UNQ " + language + "\n");
		TRSNode node = null;
		
		String[] split = response.split(" ");
		if(split[0].equals("UNQ") && split.length == 4){
			node = new TRSNode(split[1], InetAddress.getByName(split[2]), Integer.parseInt(split[3]));
		}
		
		return node;
	}
	
	private static String sendUDPMessage(String message) throws IOException{
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
	
	private static String sendTCPMessage(TRSNode node, String message) throws IOException{
		InetAddress address = node.getAddress();
		
		DatagramSocket clientSocket = new DatagramSocket();
		byte[] sendData = new byte[message.length()];
		byte[] receiveData = new byte[1024];
		
    	java.util.Arrays.fill(receiveData, (byte) 0);
		
		sendData = message.getBytes();
		
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, node.getPort());
		clientSocket.send(sendPacket);
		
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	    clientSocket.receive(receivePacket);
	    
	    String response = new String(receivePacket.getData());
	    response = response.substring(0, response.indexOf(0));
	    
	    clientSocket.close();
	    
	    return response;
	}
	
	public static void main(String args[]) throws IOException{		
        for(int i = 0; i < args.length-1; i++){
            if(args[i].equals("-p")){
                TCSport = Integer.parseInt(args[i+1]);
            }
        }
        
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        
        while(true){
        	String input = stdin.readLine();
        	String[] split = input.split(" ");
        	
        	if(split[0].equals("list")){
        		ArrayList<String> languages = getLanguages();
        		for(int i = 0; i < languages.size(); i++){
        			System.out.println(i+1 + "- " + languages.get(i));
        		}
        	}
        	else if(split[0].equals("request")){
        		if(split.length < 4){
        			System.out.println("REQUEST: ERROR");
        		}
        		else{
        			String language = languagesCache.get(Integer.getInteger(split[1])-1);
        			TRSNode node = getTRSNode(language);
        			
        			System.out.println(node.getAddress().getHostAddress() + node.getPort());
        		}
        	}
        	else{
        		System.out.println("Invalid command");
        	}
        }
	}
}
