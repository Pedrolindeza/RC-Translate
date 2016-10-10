package rc.translate.g25;


import java.io.*;
import java.net.*;
import java.util.ArrayList;

class TCS{
	
	private static Integer TCSPort;
	private static DatagramSocket serverSocket;
    
    private static ArrayList<TRSNode> nodes = new ArrayList<TRSNode>();
    
	public static void main(String args[]) throws Exception{
    	
        String receivedmsg = null ;
        String sendmsg= null;
        String[] parts = new String[5];
        int port = 0;
        InetAddress IPAddress = null;

        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        
    	/* Port Definition */
        if(args.length==2 && args[0].equals("-p")){
            setTCSPort(Integer.parseInt(args[1]));
        }
        else setTCSPort(58025);
        
        /* Socket initialization */
        setSocket( getTCSPort() );
        System.out.println("\n\t//------ porta ->  " + getTCSPort() + " -------- //\n");
        	    
        while(true){
        	
        	
        	DatagramPacket receivePacket = packetCreator(receiveData);
            receber(receiveData, receivePacket);
            IPAddress = getIP(receivePacket);
        	port = getPort(receivePacket);
        	
        	/*----- RECEIVED MESSAGE ----- */
        	receivedmsg = getMessage(receivePacket);
        	
            if (receivedmsg.contains(" ")){
            	parts = receivedmsg.split(" ");
            }
            else parts[0] = receivedmsg;
            
            System.out.println("\n" +"RECEIVED: " + receivedmsg + " / COMMAND : " + parts[0] );
        	
        	/*------ COMMAND SELECTION ------*/
            if(parts[0].equals("SRG"))
            	sendmsg = SRG(parts);
            else if (parts[0].equals("ULQ\n")) 
            	sendmsg = ULQ();
            else if (parts[0].equals("SUN"))
            	sendmsg = SUN(parts);
            else if (parts[0].equals("UNQ"))
            	sendmsg = UNQ(parts);
            else
            	sendmsg= "UNKNOWN COMMAND";
            
            System.out.println("SENT : " + sendmsg);
            sendData = sendmsg.getBytes();
            		
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);
            
        }
    }
    
	private static DatagramPacket packetCreator(byte[] receiveData) {
		return new DatagramPacket(receiveData, receiveData.length);
	}

	private static String getMessage(DatagramPacket receivePacket) throws IOException {
		String receivedmsg = new String(receivePacket.getData()); 							
        receivedmsg = receivedmsg.substring(0,receivedmsg.indexOf(0));
		return receivedmsg;
	}

	private static int getPort(DatagramPacket receivePacket)  {
		return receivePacket.getPort();
	}

	private static InetAddress getIP(DatagramPacket receivePacket){	
	 	return receivePacket.getAddress();
	}
	
	private static String UNQ(String[] parts) {
		String aux = new String();
		
		if( parts.length == 2 ){
			parts[1] = parts[1].replace("\n", "");
			for (TRSNode a : nodes){
				if (a.getLanguage().equals(parts[1]) ){
					return aux = "UNR " + a.getAddress().getHostAddress() + " " + a.getPort() + "\n";
				}
			}
			return aux = "UNR EOF\n";
		}
		return aux = "UNR ERR\n";
	}


	private static String SUN(String[] parts) {
		
		String aux = new String();
		if( parts.length == 4 ){
			parts[3] = parts[3].replace("\n", ""); 
			for (TRSNode a : nodes){
				if (a.getLanguage().equals(parts[1]) ){
					nodes.remove(a);
					return aux = "SUR OK\n";
				}
			}
			return aux = "SUR NOK\n";
		}
		else 
			return aux = "SUR ERR\n";
	}


	private static String SRG(String[] parts) throws NumberFormatException, UnknownHostException {
    	
		String aux = new String();
		
		if( parts.length == 4 ){
			parts[3] = parts[3].replace("\n", ""); 
			TRSNode nsv = new TRSNode(parts[1], InetAddress.getByName(parts[2]), Integer.parseInt(parts[3]));
			aux = "SRR OK\n";
			if( isinlist(parts[1]))
				aux = "SRR NOK\n";
			else nodes.add(nsv);
		}
		else 
			aux = "SRR ERR\n";
		
		return aux;
	}

	private static String ULQ() {
		
		String aux = new String("ULR");
	
    	if( nodes.isEmpty()) aux += " 0\n";
    	else {
    		aux += " " + Integer.toString(nodes.size());
    		for( TRSNode a : nodes)
    			aux += " " +  a.getLanguage();
    		aux += "\n";
    	}
		return aux;
	}

	public static void setSocket(int porta) throws SocketException {
    	serverSocket = new DatagramSocket ( porta );
    	return;
	}

	public TCS( int port){
    	setTCSPort(port);
    }
    
    public static void setTCSPort(int a){
    	TCSPort = new Integer(a);
    	return;
    }
    
    public static Integer getTCSPort(){
    	return TCSPort;
    }
    
    public static void receber(byte[] receiveData, DatagramPacket receivePacket) throws IOException, SocketException{
    	java.util.Arrays.fill(receiveData, (byte) 0); 								
        serverSocket.receive(receivePacket); 
    }
    
    public static boolean isinlist(String lang){
    	for(TRSNode a : nodes){
    		if (a.getLanguage().equals(lang)) { return true; }
    	}
    	return false;
    }
}