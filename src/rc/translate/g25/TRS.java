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
	      tosend="SRG "+ language +" "+IPAddress+" "+TRSport+"\n";
	      System.out.println(tosend);
	      sendData = tosend.getBytes();
	      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, TCSport);
	      clientSocket.send(sendPacket);
	      
	      
	      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	      clientSocket.receive(receivePacket);
	      String modifiedSentence = new String(receivePacket.getData());
	      String[] msg = modifiedSentence.split(" ");
	      if (msg[1]=="OK"){
	    	  ServerSocket TCPsocket = new ServerSocket(TCSport);
	    	  while(true){
	    		  Socket connectTCP = TCPsocket.accept();
	    		  BufferedReader message = new BufferedReader(new InputStreamReader(connectTCP.getInputStream()));
	    		  String tobesplit = message.readLine();
	    		  String[] splitted= tobesplit.split(" ");
	    		  
	    		  if (splitted[0].equals("exit")){ TCPsocket.close(); break; }
	    		  if(splitted[0].equals("TRQ")){
	    			  if (splitted[1].equals("t")){/*TODO translation */ }
	    			  else if (splitted[1].equals("t")){/*TODO translation */ }
	    			  else{ System.out.println("ERROR Invalid Command after TRQ");}
	    			  
	    		  }
	    		  else{ System.out.println("ERROR: Invalid Command");}
	    	  }
      	  }
	      else{	
	    	  System.out.println("TCS n�o deu o OK");	  
	      }
	      System.out.println("FROM SERVER:" + modifiedSentence);
	      clientSocket.close();
	   }
}
