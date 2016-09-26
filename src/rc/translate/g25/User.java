package rc.translate.g25;

public class User {
	public static void main(String args[]){
		String TCSname = "127.0.0.1";
		int TCSport = 58025;
		
        for(int i = 0; i < args.length-1; i++){
            if(args[i].equals("-p")){
                TCSport = Integer.parseInt(args[i+1]);
            }
        }
	}
}
