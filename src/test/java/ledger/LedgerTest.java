package ledger;

import ledger.exception.*;
import ledger.service.LedgerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

public class LedgerTest {
    LedgerService ledgerService;

    @BeforeEach
    void setup() {
        ledgerService = new LedgerService();
    }

    @Test
    void shouldCreateAccountWithOpeningBalance() {
       String accountId = ledgerService.createAccount(1000);
       assertEquals(1000, ledgerService.getAccountBalance(accountId));
    }
    @Test
    void shouldNotCreateAccountWithNegativeOpeningBalance() {
        assertThrows(InvalidAccountBalanceException.class, () -> ledgerService.createAccount(-1000));
    }
    @Test
    void shouldThrowWhenGettingBalanceForNonExistentAccount() {
        assertThrows(InvalidAccountIdException.class,
        () -> ledgerService.getAccountBalance("123"));
    }
    @Test
    void shouldDepositMoneyIntoAccount() {
        String accountId = ledgerService.createAccount(1000);
        ledgerService.deposit(accountId,500);
        assertEquals(1500,ledgerService.getAccountBalance(accountId));
    }
    @Test
    void shouldNotDepositAccountWithNegativeAmount() {
        String accountId = ledgerService.createAccount(1000);
        assertThrows(InvalidAmountException.class, () -> ledgerService.deposit(accountId,-1000));
    }
    @Test
    void shouldThrowWhenDepositingToNonExistentAccount() {
        assertThrows(InvalidAccountIdException.class,
                () -> ledgerService.deposit("nonexistent-id", 500));
    }
    @Test
    void shouldBeAbleToWithdrawAmountFromAccount() {
        String accountId = ledgerService.createAccount(1000);
        ledgerService.withdraw(accountId,500);
        assertEquals(500,ledgerService.getAccountBalance(accountId));
    }

    @Test
    void shouldThrowWhenTryingToWithdrawMoreThanAccountBalance(){
        String accountId = ledgerService.createAccount(100);

        assertThrows(InsufficientFundsException.class,() -> ledgerService.withdraw(accountId,500));
    }
    @Test
    void shouldNotWithdrawNegativeAmount() {
        String accountId = ledgerService.createAccount(1000);
        assertThrows(InvalidAmountException.class, () -> ledgerService.withdraw(accountId,-1000));
    }
    @Test
    void shouldThrowWhenWithdrawingFromNonExistentAccount() {
        assertThrows(InvalidAccountIdException.class,
                () -> ledgerService.withdraw("nonexistent-id", 500));
    }
    @Test
    void shouldAllowWithdrawalOfExactBalance() {
        String accountId = ledgerService.createAccount(500);
        ledgerService.withdraw(accountId, 500);
        assertEquals(0, ledgerService.getAccountBalance(accountId));
    }
    @Test
    void shouldNotAllowTransferToSelf() {
        String accountId = ledgerService.createAccount(1000);
        assertThrows(InvalidTransferException.class,
                () -> ledgerService.transfer(accountId, accountId, 100));
    }
    @Test
    void shouldPreserveTotalBalanceAfterTransfer() {
        String fromId = ledgerService.createAccount(1000);
        String toId = ledgerService.createAccount(500);

        ledgerService.transfer(fromId, toId, 300);

        assertEquals(700, ledgerService.getAccountBalance(fromId));
        assertEquals(800, ledgerService.getAccountBalance(toId));
    }
    @Test
    void shouldNotTransferNegativeAmount() {
        String fromAccountId = ledgerService.createAccount(1000);
        String toAccountId = ledgerService.createAccount(1000);
        assertThrows(InvalidAmountException.class, () -> ledgerService.transfer(fromAccountId,toAccountId,-1000));
    }
    @Test
    void shouldRollbackWithdrawalIfDepositFails() {
        String fromId = ledgerService.createAccount(1000);

        assertThrows(InvalidAccountIdException.class,
                () -> ledgerService.transfer(fromId, "nonexistent-id", 300));

        assertEquals(1000, ledgerService.getAccountBalance(fromId));
    }
    @Test
    void shouldThrowWhenTransferingFromNonExistentAccount() {
        String toId = ledgerService.createAccount(1000);
        assertThrows(InvalidAccountIdException.class,
                () -> ledgerService.transfer("nonexistent-id", toId,500));
    }

    @Test
    void shouldPreserveTotalBalanceUnderConcurrentTransfers() throws InterruptedException {
        String acc1 = ledgerService.createAccount(1000);
        String acc2 = ledgerService.createAccount(1000);

        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    ledgerService.transfer(acc1, acc2, 10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        double finalTotal = ledgerService.getAccountBalance(acc1) + ledgerService.getAccountBalance(acc2);
        assertEquals(2000, finalTotal);
    }
}
