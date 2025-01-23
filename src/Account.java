import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Account implements Serializable{

    private int accNum;					// Account number
    private int amt;					// Account balance
    private List<Transaction> transactions = new ArrayList<Transaction>();	// History of account transactions

    //Constructor
    public Account(int accNum, int amt){
        this.accNum = accNum;
        this.amt = amt;
    }

    //Deposit money in user bank account
    public void deposit(int deposit){
        if (deposit > 0){		//Deposit must be non-negative number
            amt += deposit;
            addTransaction(new Transaction("Deposit", deposit, new Date(), amt, accNum)); //Add transaction to list
        }
    }

    //Withdraw money from user bank account
    public boolean withdraw(int withdraw){
        int balance = amt - withdraw;			//Check final balance before setting it
        if (balance >= 0){						//User not allowed into overdraft
            amt = balance;
            addTransaction(new Transaction("Withdrawal", withdraw, new Date(), amt, accNum)); //Add transaction to list
            return true;															  //Transaction successful
        }
        return false; 	//Transaction unsuccessful
    }

    //Check balance of user bank account
    public int inquiry(){
        return amt;
    }

    //Return statement to user recording transactions from defined time period
    public Statement getStatement(Date from, Date to){

        List<Transaction> periodTransactions = new ArrayList<Transaction>();	//Stores transactions from set period
        for (Transaction t: transactions){					//Loop over all transactions
            Date date = t.getDate();
            if(date.after(from) && date.before(to)){		//Check if transaction date falls in set time period
                periodTransactions.add(t);					//Add relevant transaction to list
            }
        }
        BankStatement statement = new BankStatement(periodTransactions, from, to, accNum);	//Generate statement with relevant info
        return statement;
    }

    public void addTransaction(Transaction t){	//Adds transaction to list of total history transactions
        transactions.add(t);
    }


    //Getter and setter methods
    public int getAccNum() {
        return accNum;
    }

    public void setAccNum(int accNum) {
        this.accNum = accNum;
    }

    public int getAmt() {
        return amt;
    }

    public void setAmt(int amt) {
        this.amt = amt;
    }


}
