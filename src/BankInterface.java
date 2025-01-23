import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;


public interface BankInterface extends Remote {

public long login(String username, String password) throws RemoteException, InvalidLogin;

public void logout(long sessionID)throws RemoteException;

public void deposit(int accNum, int amount, long sessionID) throws RemoteException, InvalidSession, InvalidAccount;

public void withdraw(int accNum, int amount, long sessionID) throws RemoteException, InvalidSession, InvalidAccount, InsufficientFunds;

public int inquiry(int accNum, long sessionID) throws RemoteException, InvalidSession, InvalidAccount;

public Statement getStatement(int accNum, Date from, Date to, long sessionID) throws RemoteException, InvalidSession, InvalidAccount;
 
}