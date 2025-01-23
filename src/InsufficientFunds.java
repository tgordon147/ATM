//Exception thrown when user attempts to withdraw more money than held in account
public class InsufficientFunds extends Exception {
	public InsufficientFunds(String m){
		super(m);
	}
}
