package br.com.financial.system.account_system.exception;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(String accountId) {
        super("Insufficient balance for account: " + accountId);
    }
}
