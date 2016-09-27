package rc.translate.g25;
import java.net.InetAddress;

public class TRSNode {
	private String language;
	private InetAddress address;
	private int port;
	
	public TRSNode(String language, InetAddress address, int port){
		this.language = language;
		this.address = address;
		this.port = port;
	}
	
	public String getLanguage(){
		return this.language;
	}
	
	public InetAddress getAddress(){
		return this.address;
	}
	
	public int getPort(){
		return this.port;
	}
}
