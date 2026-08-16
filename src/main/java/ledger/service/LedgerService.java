package ledger.service;

import ledger.exception.*;
import ledger.model.Account;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LedgerService {

    private Map<String,Account> ledger;
    //TODO: when to use final ??
    public LedgerService() {
        this.ledger = new ConcurrentHashMap<>();
    }

    public String createAccount(double accountBalance){
        if(accountBalance < 0)
            throw new InvalidAccountBalanceException("Cannot create an account with negative balance " + accountBalance);
        Account account = new Account(accountBalance);
        //TODO: Check after how many numbers uuid collides? read about concurrent hashmaps
        ledger.put(account.getId(),account);
        return account.getId();
    }

    public double getAccountBalance(String id){
        if(ledger.containsKey(id)){
            return ledger.get(id).getAccountBalance();
        }
        else{
            throw new InvalidAccountIdException("Invalid Account ID " + id);
        }
    }

    public void deposit(String id, double amount) {
        if(ledger.containsKey(id)){
            if(amount < 0)
                throw new InvalidAmountException("Cannot deposit negative amount " + amount + " for account id " + id);
            Account account =  ledger.get(id);
            account.deposit(amount);
        }
        else{
            throw new InvalidAccountIdException("Invalid Account ID " + id);
        }
    }

    public void withdraw(String id, double amount) {
        if(ledger.containsKey(id)){
            if(amount < 0)
                throw new InvalidAmountException("Cannot withdraw negative amount");
            Account account =  ledger.get(id);
            account.withdraw(amount);
        }
        else{
            throw new InvalidAccountIdException("Invalid Account ID");
        }
    }

    public void transfer(String fromAccountId, String toAccountId, double amount) {
        if(fromAccountId.equals(toAccountId)){
            throw new InvalidTransferException("Cannot transfer to self");
        }
        if(!ledger.containsKey(fromAccountId) || !ledger.containsKey(toAccountId)){
            throw new InvalidAccountIdException("Invalid sender or receiver account id");
        }
        if(amount < 0)
            throw new InvalidAmountException("Cannot withdraw negative amount");
        try{
            withdraw(fromAccountId,amount);

        }catch(Exception e) {
            // withdraw failed
        }
        finally {
            try{
                deposit(toAccountId,amount);
            } catch (Exception e) {
                // deposit failed
            }
            finally {
                deposit(fromAccountId,amount);
            }
        }
    }
}
