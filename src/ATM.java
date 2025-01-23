import java.rmi.Naming;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

// Client Implementation
// -Djava.security.debug=access,failure (add to run config - args - vm args , for debugging)
public class ATM {

	BankInterface bank;
	static Long sessionID = 0L;				//ID unique to user for server defined time period
	Scanner scan = new Scanner(System.in);
	
	//Constructor that starts the sign-in process
	public ATM(BankInterface bank){
		this.bank = bank;					
		this.signin();
	}
	
	//Enables user to sign-in to the server
	private void signin() {
		System.out.println("Please enter command 'login' to begin or 'quit' to exit: ");
		String s = scan.next(); 							// getting entered command
		
		while (!s.equals("login") && !s.equals("quit")){	// Loop until a recognised command is entered
			System.out.println("Invalid Command!  Please enter command 'login' to begin or 'quit' to exit: ");
			s = scan.next(); 								// getting a String value
		}
		if(s.equals("quit")){								// terminate the ATM (client)
			System.out.println("Goodbye!");
			System.exit(0);
		}
		sessionID = 0L;										// Covers case where user session has timed out, need new session
		while (sessionID.equals(0L)){						// Loop until unique ID is assigned to user
			System.out.println("Please enter username : ");
			String username = scan.next(); 					// getting username
			System.out.println("Please enter password : ");
			String password = scan.next(); 					// getting password
			try {											// check entered credentials on server-side
				sessionID = bank.login(username, password);	// For successful login, user assigned unique session ID
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (InvalidLogin e){
				System.out.println("Invalid credentials entered. Please try again");
			}
		}
		System.out.println(sessionID);
		System.out.println("Login Successful - This session is valid for 5 minutes");
		mainMenu(sessionID);							// User now logged in, go to main menu for additional actions
	}

	//Provides user with banking options: deposit, withdraw, get statement, balance inquiry
	private void mainMenu(long sessionID) {
		System.out.println("Please choose from the following options: \n"
				+ "1 - Deposit\n" + "2 - Withdraw\n" + "3 - Balance Inquiry\n"
						+ "4 - Statement\n" + "5 - Exit System" );
		int result = scan.nextInt();
		
		switch (result){										//Select method based on user input
			case 1: deposit(sessionID);
					break;
			case 2: withdraw(sessionID);
					break;
			case 3: inquiry(sessionID);
					break;
			case 4: getStatement(sessionID);
					break;
			case 5: logout(sessionID);
					break;
			default:System.out.println("Invalid selection!"); 	// Invalid option chosen, return user to main menu
					mainMenu(sessionID);
					break;
		}
		
	}

	// Set up and run client
	public static void main (String args[]) throws Exception {
	if (System.getSecurityManager() == null){						//Establish the security manager
		System.setSecurityManager(new SecurityManager());
	}
	try
	{
		String name = "BankServer";											
		BankInterface bank = (BankInterface) Naming.lookup(name);	//Perform lookup of object that is binded to registry
		new ATM(bank);												//Starts the ATM(client) and signs in		
		System.out.println("Connected");
		
	}
	catch(Exception e){
		System.err.println("ATM Error!");
		e.printStackTrace();
	}

}

// Deposit money in user bank account
private void deposit(long sessionID){
	System.out.println("Please enter your account number: ");
	int acnt = scan.nextInt();
	System.out.println("Please enter the amount you wish to deposit: ");
	int amt = scan.nextInt();
	
	try {
		bank.deposit(acnt, amt, sessionID);									//Call server side method to deposit money
	} catch (RemoteException e) {
		e.printStackTrace();
	} catch (InvalidAccount e){												//Exception thrown when account does not exist
			System.out.println("Invalid Accoung Number! Try again");
			deposit(sessionID);												//Re-call the method for user to enter correct acc num
	} catch (InvalidSession e) {											//Exception thrown when user session has timed out
		System.out.println("This session has expired - please log in again.");	//Notify user that the session has timed out
		signin();																// Prompt user to sign-in again
	}
	
	
	System.out.println("Successfully deposited $" + amt + " to account " + acnt);	// Deposit was successful
	mainMenu(sessionID);															// Return user to main menu
}

//Withdraw money from user bank account
private void withdraw(long sessionID){
	System.out.println("Please enter your account number: ");
	int acnt = scan.nextInt();
	System.out.println("Please enter the amount you wish to withdraw: ");
	int amt = scan.nextInt();
	
	try {
		bank.withdraw(acnt, amt, sessionID);								//Call server side method to withdraw money
	} catch (RemoteException e) {
		e.printStackTrace();
	} catch (InvalidAccount e){												//Exception thrown when account does not exist
		System.out.println("Invalid Accoung Number! Try again");
		withdraw(sessionID);
	} catch (InsufficientFunds e){											//Exception thrown when there are insufficient funds to complete the withdrawal
		System.out.println("There are insufficient funds in the account to complete this transaction");
		System.out.println("Try again");
		withdraw(sessionID);												//Restart the withdrawal method for user
	} catch (InvalidSession e) {											//Exception thrown when user session has timed out
		System.out.println("This session has expired - please log in again.");	//Notify user that the session has timed out
		signin();																// Prompt user to sign-in again
	}
	System.out.println("Successfully withdrew $" +amt+ " from account " + acnt);	// Withdrawal was successful
	mainMenu(sessionID);															// Return user to main menu
}

//Inquire about user bank account balance
private void inquiry(long sessionID){
	float balance = 0;
	System.out.println("Please enter your account number: ");
	int acnt = scan.nextInt();
	
	try {
		balance = bank.inquiry(acnt, sessionID);							//Call server side method to check balance
	} catch (RemoteException e) {
		e.printStackTrace();
	} catch (InvalidAccount e){												//Exception thrown when account does not exist
			System.out.println("Invalid Accoung Number! Try again");
			inquiry(sessionID);
	} catch (InvalidSession e){												//Exception thrown when user session has timed out
		System.out.println("This session has expired - please log in again.");	//Notify user that the session has timed out
		signin();																// Prompt user to sign-in again
	}
	System.out.println("The current balance of account " + acnt + " is $" + balance);	//Display balance
	mainMenu(sessionID);																// Return user to main menu
}

//Generate a statement of user bank account for defined time period
private void getStatement(long sessionID){
	Statement statement = null;
	System.out.println("Please enter your account number: ");							//User enters account number
	int acnt = scan.nextInt();
	System.out.println("Please enter the start date (dd/mm/yyyy) for the statement: "); // Start date for the statement
	String start = scan.next();
	System.out.println("Please enter the end date (dd/mm/yyyy) for the statement: ");	// Finish date for the statement
	String end = scan.next();
	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");							// Date format required
	Date from = null, to = null; 
	
	
	try {
		from = (Date)formatter.parse(start);											// Apply format to date
		to = (Date)formatter.parse(end);
		statement = bank.getStatement(acnt, from, to, sessionID);						// Call server side method for generating bank statement
	}catch (RemoteException e) {
		e.printStackTrace();
	} catch (InvalidAccount e){															//Exception thrown when account does not exist
			System.out.println("Invalid Accoung Number! Try again");
			getStatement(sessionID);
	} catch (InvalidSession e) {														//Exception thrown when user session has timed out
		System.out.println("This session has expired - please log in again.");	//Notify user that the session has timed out
		signin();																// Prompt user to sign-in again
	}
	catch(ParseException ex){															//Exception thrown for incorrect date input
		System.out.println("Invalid Dates - Please start again");
		getStatement(sessionID);														//Return user to start of method
	}
	
	
	List<Transaction> trans = statement.getTransations();								//Gets the transactions of that time period as a List
	System.out.println("Date\t\t\t\t\t\t Transaction Type\tAmount\t\tBalance");			
	for(Transaction t: trans){															//Loop over all transactions and print details to console
		System.out.println(t.getDate().toString() + "\t\t\t " + t.getType() + "\t\t" + t.getAmount() +
				"\t\t" + t.getBalance());
	}
	System.out.println();
	mainMenu(sessionID);																//Return user to main menu
}

private void logout(long sessionID){
	try{
		bank.logout(sessionID);
	}
	catch(RemoteException e) {
		e.printStackTrace();
	}
	System.out.println("Goodbye!");
	System.exit(0);
}

}
