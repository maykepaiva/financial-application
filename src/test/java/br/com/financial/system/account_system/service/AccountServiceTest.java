package br.com.financial.system.account_system.service;

import br.com.financial.system.account_system.dto.AccountResponse;
import br.com.financial.system.account_system.dto.EventRequest;
import br.com.financial.system.account_system.exception.AccountNotFoundException;
import br.com.financial.system.account_system.exception.InsufficientBalanceException;
import br.com.financial.system.account_system.exception.InvalidAmountException;
import br.com.financial.system.account_system.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class   AccountServiceTest {

    private AccountService service;

    @BeforeEach
    void setUp() {
        service = new AccountService(new AccountRepository());
    }

    @Test
    void depositCreatesAccountWhenItDoesNotExist() {
        AccountResponse response = service.deposit(EventRequest.builder()
                .destination("100")
                .amount(BigDecimal.TEN)
                .build());

        assertThat(response.getDestination().getId()).isEqualTo("100");
        assertThat(response.getDestination().getBalance()).isEqualByComparingTo("10");
    }

    @Test
    void depositIncreasesBalanceOfExistingAccount() {
        service.deposit(EventRequest.builder().destination("100").amount(BigDecimal.TEN).build());

        AccountResponse response = service.deposit(EventRequest.builder()
                .destination("100")
                .amount(BigDecimal.TEN)
                .build());

        assertThat(response.getDestination().getBalance()).isEqualByComparingTo("20");
    }

    @Test
    void depositWithZeroAmountThrows() {
        assertThatThrownBy(() -> service.deposit(EventRequest.builder()
                .destination("100")
                .amount(BigDecimal.ZERO)
                .build()))
                .isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void withdrawReducesBalanceOfExistingAccount() {
        service.deposit(EventRequest.builder().destination("100").amount(BigDecimal.TEN).build());

        AccountResponse response = service.withdraw(EventRequest.builder()
                .origin("100")
                .amount(new BigDecimal(4))
                .build());

        assertThat(response.getOrigin().getBalance()).isEqualByComparingTo("6");
    }

    @Test
    void withdrawFromNonExistingAccountThrows() {
        assertThatThrownBy(() -> service.withdraw(EventRequest.builder()
                .origin("200")
                .amount(BigDecimal.TEN)
                .build()))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void withdrawWithInsufficientBalanceThrows() {
        service.deposit(EventRequest.builder().destination("100").amount(BigDecimal.TEN).build());

        assertThatThrownBy(() -> service.withdraw(EventRequest.builder()
                .origin("100")
                .amount(new BigDecimal(20))
                .build()))
                .isInstanceOf(InsufficientBalanceException.class);
    }

    @Test
    void transferMovesBalanceBetweenAccounts() {
        service.deposit(EventRequest.builder().destination("100").amount(new BigDecimal(15)).build());

        AccountResponse response = service.transfer(EventRequest.builder()
                .origin("100")
                .destination("300")
                .amount(new BigDecimal(15))
                .build());

        assertThat(response.getOrigin().getBalance()).isEqualByComparingTo("0");
        assertThat(response.getDestination().getBalance()).isEqualByComparingTo("15");
    }

    @Test
    void transferFromNonExistingAccountThrows() {
        assertThatThrownBy(() -> service.transfer(EventRequest.builder()
                .origin("200")
                .destination("300")
                .amount(BigDecimal.TEN)
                .build()))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void getBalanceReturnsCurrentBalance() {
        service.deposit(EventRequest.builder().destination("100").amount(BigDecimal.TEN).build());

        assertThat(service.getBalance("100")).isEqualByComparingTo("10");
    }

    @Test
    void getBalanceForNonExistingAccountThrows() {
        assertThatThrownBy(() -> service.getBalance("999"))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void resetClearsAllAccounts() {
        service.deposit(EventRequest.builder().destination("100").amount(BigDecimal.TEN).build());

        service.reset();

        assertThatThrownBy(() -> service.getBalance("100"))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
