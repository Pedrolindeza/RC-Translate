package rc.translate.g25;


import java.io.*;
import java.net.*;
import java.util.ArrayList;

class TCS{
	
	private static Integer TCSPort;
	private static DatagramSocket serverSocket;
    
    private static ArrayList<TRSNode> nodes = new ArrayList<TRSNode>();
    
    @SuppressWarnings("null")
	public static void main(String args[]) throws Exception{
    	
        String receivedmsg ;
        String sendmsg= new String();
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
        setSocket( getTCSPort());
	    
        System.out.println("\n\t//------ porta ->  " + getTCSPort() + " -------- //\n");
        	    
        while(true){
        	
        	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            receber(receiveData, receivePacket);
            IPAddress = receivePacket.getAddress();
        	port = receivePacket.getPort();
        	
        	receivedmsg = new String(receivePacket.getData()); 										// cria string
            receivedmsg = receivedmsg.substring(0,receivedmsg.indexOf(0)); 							// Limita string ate primeiro zero
            if (receivedmsg.contains(" ")){
            	parts= receivedmsg.split(" ");
            	System.out.println("\n" +"entrei");
            }
            
            System.out.println("\n" +"RECEIVED: " + parts[0]);
            
            /*if (parts[0].equals("ULQ")) sendmsg = new String("ULR" + Integer.toString(nodes.size())	);
            else if (receivedmsg.equals("Sim e ctg?\n")) sendmsg = new String("\n-> tambem mpt\n");*/
            
            
            sendData = sendmsg.getBytes();
            		
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendmsg.length(), IPAddress, port);
            serverSocket.send(sendPacket);
            
            
        }
    }
    
    public static void setSocket(int porta) throws SocketException {
		// TODO Auto-generated method stub
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
    
    public static void receber(byte[] receiveData, DatagramPacket receivePacket) throws IOException{
    	java.util.Arrays.fill(receiveData, (byte) 0); 											//Enche tudo com zeros 
        serverSocket.receive(receivePacket); 													// Bloqueante ate receber
        
    }
    
}