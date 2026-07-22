package br.com.financial.system.account_system.repository;

import br.com.financial.system.account_system.model.Account;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public Optional<Account> findById(String id) {
        return Optional.ofNullable(accounts.get(id));
    }

    public Account save(Account account) {
        accounts.put(account.getId(), account);
        return account;
    }

    public boolean existsById(String id) {
        return accounts.containsKey(id);
    }

    public void deleteById(String id) {
        accounts.remove(id);
    }

    public void clear() {
        accounts.clear();
    }
}
