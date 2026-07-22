package br.com.financial.system.account_system.service;

import br.com.financial.system.account_system.dto.AccountResponse;
import br.com.financial.system.account_system.dto.EventRequest;
import br.com.financial.system.account_system.exception.AccountNotFoundException;
import br.com.financial.system.account_system.exception.InsufficientBalanceException;
import br.com.financial.system.account_system.exception.InvalidAmountException;
import br.com.financial.system.account_system.model.Account;
import br.com.financial.system.account_system.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;

    public BigDecimal getBalance(String accountId) {

        Account account = getAccount(accountId);

        return account.getBalance();
    }

    public AccountResponse deposit(EventRequest request) {
        validateAmount(request.getAmount());

        Account account = repository.findById(request.getDestination())
                .orElseGet(() -> createAccount(request.getDestination()));

        account.setBalance(account.getBalance().add(request.getAmount()));

        Account saved = repository.save(account);

        log.info("Deposit of {} performed on account {}", request.getAmount(), saved.getId());

        return AccountResponse.builder()
                .destination(saved)
                .build();
    }

    public AccountResponse withdraw(EventRequest request) {
        validateAmount(request.getAmount());

        Account account = getAccount(request.getOrigin());

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(account.getId());
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));

        Account saved = repository.save(account);

        log.info("Withdraw of {} performed on account {}", request.getAmount(), saved.getId());

        return AccountResponse.builder()
                .origin(saved)
                .build();
    }

    public synchronized AccountResponse transfer(EventRequest request) {
        validateAmount(request.getAmount());

        Account origin = getAccount(request.getOrigin());

        Account destination = repository.findById(request.getDestination())
                .orElseGet(() -> createAccount(request.getDestination()));

        if (origin.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(origin.getId());
        }

        origin.setBalance(origin.getBalance().subtract(request.getAmount()));
        destination.setBalance(destination.getBalance().add(request.getAmount()));

        Account savedOrigin = repository.save(origin);
        Account savedDestination = repository.save(destination);

        log.info("Transfer of {} performed from account {} to account {}", request.getAmount(), savedOrigin.getId(), savedDestination.getId());

        return AccountResponse.builder()
                .origin(savedOrigin)
                .destination(savedDestination)
                .build();
    }

    public void reset() {

        repository.clear();

    }

    private Account getAccount(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }
    }

    private Account createAccount(String id) {
        Account newAccount = Account.builder()
                .id(id)
                .balance(BigDecimal.ZERO)
                .build();

        return repository.save(newAccount);
    }

}
