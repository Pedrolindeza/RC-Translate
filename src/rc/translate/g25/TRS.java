package rc.translate.g25;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class TRS {
	public static void main(String args[]) throws Exception
	   {
	      BufferedReader inFromUser =
	         new BufferedReader(new InputStreamReader(System.in));
	      
	      DatagramSocket clientSocket = new DatagramSocket();
	      
	      byte[] sendData = new byte[1024];
	      byte[] receiveData = new byte[1024];
	      
	      Scanner scanner = null;
	      Map<String,String> translations = new HashMap<>();
	      try{
		  scanner = new Scanner(new File("translations.txt"));
	      }
	      catch(Exception e){ System.out.println("ERROR: File not found");}
		  while(scanner.hasNext()){
			  String portuguese = scanner.next();
			  String foreignlanguage = scanner.next();
			  translations.put(portuguese, foreignlanguage);
		  }
		  System.out.println(translations);
	      int TRSport = 59000;
	      InetAddress IPAddress = InetAddress.getByName("localhost");
	      int TCSport = 58025;
	      String language=args[0];
	      	for(int i=1; i<args.length -1;i++){
	      		System.out.println(args[i]);
	      		if(args[i].equals("-p")){
	      			TRSport=Integer.parseInt(args[++i]);
	      		}
	      		if(args[i].equals("-n")){
	      			IPAddress=InetAddress.getByName(args[++i]);
	      		}
	      		if(args[i].equals("-e")){
	      			TCSport=Integer.parseInt(args[++i]);
	      		}
	      		
	      	}
	      	
	      String tosend= new String();
	      tosend="SRG "+ language +" "+IPAddress.getHostAddress()+" "+TRSport+"\n";
	      System.out.println(tosend);
	      sendData = tosend.getBytes();
	      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, TCSport);
	      clientSocket.send(sendPacket);
	      
	      java.util.Arrays.fill(receiveData, (byte) 0);
	      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	      clientSocket.receive(receivePacket);
	      String modifiedSentence = new String(receivePacket.getData());
	      modifiedSentence = modifiedSentence.substring(0, modifiedSentence.indexOf(0));
	      String[] msg = modifiedSentence.split(" ");
	      if (msg[1].equals("OK\n")){
	      System.out.println("entrou");
	    	  ServerSocket TCPsocket = new ServerSocket(TRSport);
	    	  while(true){
	    		  Socket connectTCP = TCPsocket.accept();
	    		  BufferedReader message = new BufferedReader(new InputStreamReader(connectTCP.getInputStream()));
	    		  DataOutputStream toclient = new DataOutputStream(connectTCP.getOutputStream());
	    		  String tobesplit = message.readLine();
	    		  String[] splitted= tobesplit.split(" ");
	    		  System.out.println(splitted[0]);
	    		  System.out.println(splitted[1]);
	    		  System.out.println(splitted[2]);
	    		  if (splitted[0].equals("exit")){ TCPsocket.close(); break; }

	    		  if(splitted[0].equals("TRQ")){
	    			  if (splitted[1].equals("t")){
	    				  
	    				  int numwords = Integer.parseInt(splitted[2]);
	    				  String toreturn = "TRR t "+ numwords + " ";
	    				  int count=3;
	    				  while(count<numwords+3){
	    					  String word= splitted[count++];
	    					  toreturn+=translations.get(word) + " ";
	    				  }
	    				  toreturn+="\n";
	    				  toclient.writeBytes(toreturn);
	    				  
	    			  }
	    			  else if (splitted[1].equals("f")){/*TODO image translation */ }

	    			  else{ System.out.println("ERROR Invalid Command after TRQ");}
	    			  
	    		  }
	    		  else{ System.out.println("ERROR: Invalid Command");}
	    	  }
	    }
	      else{	
	    	  System.out.println("TCS nao deu o OK");	  
	      }
	      System.out.println("FROM SERVER:" + modifiedSentence);
	      clientSocket.close();
	   }
}
