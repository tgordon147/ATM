
import java.util.Date;
import java.io.Serializable;


public class Transaction implements Serializable{

	//Each transaction has the following associated with it:
	String type;				//Type of transaction - eg Deposit, withdrawal etc
	int amount;					//Amount involved in transaction
	Date date;					//Date the transaction occurred on
	int balance;				//Remaining balance after the transaction
	int accNum;					//Bank account number

	public Transaction(String type, int amount, Date d, int bal, int accNum){
		this.type = type;
		this.amount = amount;
		this.date = d;
		this.balance = bal;
		this.accNum = accNum;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}
}
