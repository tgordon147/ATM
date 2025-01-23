
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//Stores a list of transactions for a set time period
public class BankStatement implements Statement{

	List<Transaction> transactions;
	Date from;
	Date to;
	int accNum;
	
	//Constructor
	public BankStatement(List<Transaction> t, Date from, Date to, int accNum){
		this.transactions = t;				//Stores list of transactions
		this.from = from;					//Start date for the statement
		this.to = to;						//End date for the statement
		this.accNum = accNum;				//Account number to access information
	}
	
	
	@Override
	public int getAccountnum() {
		return accNum;
	}

	@Override
	public Date getStartDate() {
		return from;
	}

	@Override
	public Date getEndDate() {
		return to;
	}

	@Override
	public String getAccoutName() {
		return ""+accNum;
	}

	@Override
	public List<Transaction> getTransations() {
		return transactions;
	}

}
