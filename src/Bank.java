import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

// Server Implementation
//-Djava.security.debug=access,failure (add to run config - args - vm args , for debugging)

public class Bank extends UnicastRemoteObject implements BankInterface {

private static final long serialVersionUID = 1L;
private List<Account> accounts; 				// Users accounts
private HashMap<String, String> loginDetails;	// Stores login details of all users
private HashMap<Long, Date> activeUsers;		// Stores info on all active sessions

public Bank() throws RemoteException
{
	super();
	accounts = getAccounts();					// Set up accounts
	loginDetails = getLoginDetails();			// Set up user login details
	activeUsers = new HashMap<Long, Date>();	// Create HashMap for storing info on active sessions
}

public static void main(String args[]) throws Exception {

	// initialise Bank server - see sample code in the notes for details
	//System.setProperty("java.security.policy","file:/C/DistSysAssignments/JavaRMI/server.policy");
	
	if (System.getSecurityManager() == null){				//Establish the security manager
		System.setSecurityManager(new SecurityManager());
	}
	try {
		String name = "BankServer";
		BankInterface engine = new Bank();
		Naming.rebind(name, engine);						//Bind object to registry
		System.out.println("Bank Server Started");		
		}
	catch(Exception e){
		System.err.println("Bank Server Exception!");
		e.printStackTrace();
	}
	}


//Check user entered credentials and if recognised, returns unique session ID
@Override
public long login(String username, String password) throws RemoteException, InvalidLogin {
	if(loginDetails.containsKey(username)){			//Check if username is recognised
		System.out.println("True");
		String pword = loginDetails.get(username);	
		if (password.equals(pword)){				//Check if entered password matches true password
			long uniqueID = generateUniqueID();		//unique number assigned to user session
			return uniqueID;
		}
		else{ throw new InvalidLogin(); }			//Incorrect password entered, prompt user to try again
	}
	return 0L;										//Incorrect credentials entered, return default value
}

//Deposit money in user bank account
@Override
public void deposit(int accNum, int amount, long sessionID) throws RemoteException, InvalidSession, InvalidAccount {

	    boolean sessionExp = sessionExpired(sessionID);			// Must check if sessionID is still valid
	    if (sessionExp == true){								// Throw InvalidSession exception where ID has timed out
	    	throw new InvalidSession("Session Timed Out");
	    }
		Account acc = findAccount(accounts, accNum);			// Check if user entered account exists
		if (acc != null){
			acc.deposit(amount);								// Method deposits money into referenced account
		}
		else throw new InvalidAccount("Invalid Account");
	
}


//Withdraw money from user bank account
@Override
public void withdraw(int accNum, int amount, long sessionID) throws RemoteException, InvalidSession, InvalidAccount, InsufficientFunds {
    
	boolean sessionExp = sessionExpired(sessionID);				// Must check if sessionID is still valid
    if (sessionExp == true){									// Throw InvalidSession exception where ID has timed out
    	throw new InvalidSession("Session Timed Out");
    }
	Account acc = findAccount(accounts, accNum);				// Check if user entered account exists
	boolean transactionSuccess;									// True if withdrawal successful, False if unsuccessful
	if (acc != null){
		transactionSuccess = acc.withdraw(amount);				// Method withdraws money from referenced account
		if (transactionSuccess = false){						// False returned where there are insufficient funds in account
			throw new InsufficientFunds("Insufficient Funds");
		}
	}
	else throw new InvalidAccount("Invalid Account");			// Account number not recognised
	
}

//Inquire about user bank account balance
@Override
public int inquiry(int accNum, long sessionID) throws RemoteException, InvalidSession, InvalidAccount {
    
	boolean sessionExp = sessionExpired(sessionID);				// Must check if sessionID is still valid
    if (sessionExp == true){									// Throw InvalidSession exception where ID has timed out
    	throw new InvalidSession("Session Timed Out");
    }
	Account acc = findAccount(accounts, accNum);				// Check if user entered account exists
	if (acc != null){
		return acc.inquiry();									// Method checks balance of referenced account
	}
	else throw new InvalidAccount("Invalid Account");			// Account number not recognised
}

//Generate a statement of user bank account for defined time period
@Override
public Statement getStatement(int accNum, Date from, Date to, long sessionID) throws RemoteException, InvalidSession, InvalidAccount {
    
	boolean sessionExp = sessionExpired(sessionID);				// Must check if sessionID is still valid
    if (sessionExp == true){									// Throw InvalidSession exception where ID has timed out
    	throw new InvalidSession("Session Timed Out");
    }
	Account acc = findAccount(accounts, accNum);				// Check if user entered account exists
	Statement s = null;
	if (acc != null){
		s = acc.getStatement(from, to);							// Method generates statement of referenced account for set time period
	}
	else throw new InvalidAccount("Invalid Account");			// Account number not recognised
	return s;
}


//Generate sample accounts to verify functionality
private List<Account> getAccounts() {
	
	Account acc1 = new Account(100, 1000);
	Account acc2 = new Account(101, 600);
	Account acc3 = new Account(102, 550);
	Account acc4 = new Account(103, 200);
	Account acc5 = new Account(104, 190);
	List<Account> accounts = new ArrayList<Account>();
	accounts.add(acc1);
	accounts.add(acc2);
	accounts.add(acc3);
	accounts.add(acc4);
	accounts.add(acc5);
	return accounts;
}

//Check if user entered account number is valid
private Account findAccount(List<Account> accounts, int accNum) {
	for (Account acc : accounts){
		if (acc.getAccNum() == accNum){
			return acc;
		}
	}
	return null;
}

//Generate sample login credentials
//Stored in HashMap in format 'Username, Password'
private HashMap<String, String> getLoginDetails() {

	HashMap<String, String> loginDetails = new HashMap<String, String>();
	loginDetails.put("Steve", "Jobs");
	loginDetails.put("Mark", "Zukerburg");
	loginDetails.put("Bill", "Gates");
	return loginDetails;
	
}
 
//Returns a unique ID which is assigned to a user's session and times out after defined time period
private long generateUniqueID() {
	Random randomGenerator = new Random();
	boolean notUnique = true;
	long unique = 0;
	while(notUnique){								//Loop until a new unique key is found
		unique = randomGenerator.nextLong();
		if (unique < 0){							//Want a positive sessionID number
			unique *= -1;
		}
		if (!activeUsers.containsKey(unique)){		//Check if sessionID already exists
			activeUsers.put(unique, new Date());	//Store sessionID and creation time
			notUnique = false;						//Unique sessionID has now been found
		}
	}
	System.out.println(unique);
	return unique;
}

//Check if unique session ID is still within active time period
private boolean sessionExpired(long sessionID) {
	
	Date startUp = activeUsers.get(sessionID);				//Time the ID was generated
	Date now = new Date();									//Current time											
	if (now.getTime() - startUp.getTime() >= 5*60*1000){	//Check if difference is greater than set expiry time = 5 mins
		activeUsers.remove(sessionID);						//ID expired, remove from list
		return true;										
	}
	return false;											//ID still valid
	
}

@Override
public void logout(long sessionID) throws RemoteException {
	activeUsers.remove(sessionID);
}

}
