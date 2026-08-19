package ledger.model;

import ledger.exception.InsufficientFundsException;

import java.util.UUID;

public class Account {
    private final String id;
    private double accountBalance;

    public Account(double accountBalance) {
        this.id = UUID.randomUUID().toString();
        this.accountBalance = accountBalance;
    }

    public String getId() {
        return id;
    }
    public double getAccountBalance() {
        return accountBalance;
    }
    private void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public synchronized void withdraw(double amount) {
        if(this.getAccountBalance() >= amount){
            this.setAccountBalance(this.getAccountBalance() - amount);
        }
        else{
            throw new InsufficientFundsException("Cannot withdraw as there are not enough funds");
        }
    }
    public synchronized void deposit(double amount) {
        this.setAccountBalance(this.getAccountBalance() + amount);
    }
}
