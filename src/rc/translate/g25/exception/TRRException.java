package rc.translate.g25.exception;

public class TRRException extends Exception {
	private String input;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5504908026720640389L;
	public TRRException(String input){
		this.input = input;
	}
	public String getInput(){
		return this.input;
	}

}
