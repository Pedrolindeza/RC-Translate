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
	      String[]splited= sentence.split(" ");
	      TRSNode node = new TRSNode(splited[0],IPAddress,"nome da maquina",59000,58025);
	      	for(int i=1; i<splited.length -1;i++){
	      		System.out.println(splited[i]);
	      		if(splited[i].equals("-p")){
	      			node.setPort(Integer.parseInt(splited[i+1]));
	      			i++;
	      		}
	      		if(splited[i].equals("-n")){
	      			node.setName(splited[i+1]);
	      			i++;
	      		}
	      		if(splited[i].equals("-e")){
	      			node.setTCS(Integer.parseInt(splited[i+1]));
	      			i++;
	      		}
	      		
	      	}
	      	
	      String tosend= new String();
	      tosend="SRG "+node.getLanguage()+" "+node.getName()+" "+node.getPort();
	      System.out.println(tosend);
	      sendData = tosend.getBytes();
	      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
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
