package rc.translate.g25;

import java.io.*;
import java.net.*;

public class TRS {
	public static void main(String args[]) throws Exception
	   {
	      BufferedReader inFromUser =
	         new BufferedReader(new InputStreamReader(System.in));
	      
	      DatagramSocket clientSocket = new DatagramSocket();
	      InetAddress IPAddress = InetAddress.getByName("localhost");
	      byte[] sendData = new byte[1024];
	      byte[] receiveData = new byte[1024];
	      
	      String sentence = inFromUser.readLine();
	      TRSNode node = new TRSNode("?",IPAddress,"nome da maquina",59000);
	      	for(String s: sentence.split(" ")){
	      		System.out.println(s);
	      		if(s.equals("-p")){
	      			String porto = new String();
	      			node.setPort(Integer.parseInt(sentence.split(" ")));
	      		}
	      		
	      	}
	      sendData = sentence.getBytes();
	      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
	      clientSocket.send(sendPacket);
	      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	      clientSocket.receive(receivePacket);
	      String modifiedSentence = new String(receivePacket.getData());
	      System.out.println("FROM SERVER:" + modifiedSentence);
	      clientSocket.close();
	   }
}
