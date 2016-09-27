package rc.translate.g25;

import java.io.*;
import java.net.*;

public class TRS {
	public static void main(String args[]) throws Exception
	   {
	      BufferedReader inFromUser =
	         new BufferedReader(new InputStreamReader(System.in));
	      
	      DatagramSocket clientSocket = new DatagramSocket();
	      
	      byte[] sendData = new byte[1024];
	      byte[] receiveData = new byte[1024];
	      
	      String sentence = inFromUser.readLine();
	      String[]splited= sentence.split(" ");
	      int TRSport = 59000;
	      InetAddress IPAddress = InetAddress.getByName("localhost");
	      int TCSport = 58025;
	      String language=splited[0];
	      	for(int i=1; i<splited.length -1;i++){
	      		System.out.println(splited[i]);
	      		if(splited[i].equals("-p")){
	      			TRSport=Integer.parseInt(splited[++i]);
	      		}
	      		if(splited[i].equals("-n")){
	      			IPAddress=InetAddress.getByName(splited[++i]);
	      		}
	      		if(splited[i].equals("-e")){
	      			TCSport=Integer.parseInt(splited[++i]);
	      		}
	      		
	      	}
	      	
	      String tosend= new String();
	      tosend="SRG "+ language +" "+IPAddress+" "+TCSport;
	      System.out.println(tosend);
	      sendData = tosend.getBytes();
	      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, TCSport);
	      clientSocket.send(sendPacket);
	      
	      
	      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	      clientSocket.receive(receivePacket);
	      String modifiedSentence = new String(receivePacket.getData());
	      String[] msg = modifiedSentence.split(" ");
	      if (msg[1]=="OK"){
	    	  //fork e derivados
	      }
	      else{//vai tudo abaixo
	    	  
	      }
	      System.out.println("FROM SERVER:" + modifiedSentence);
	      clientSocket.close();
	   }
}
