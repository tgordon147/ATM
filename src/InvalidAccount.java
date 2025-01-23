//Exception thrown when user enters unrecognised bank account number
public class InvalidAccount extends Exception{
	
	public InvalidAccount(String m){
		super(m);
	}
}
