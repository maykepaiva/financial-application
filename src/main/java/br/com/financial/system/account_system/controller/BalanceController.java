package br.com.financial.system.account_system.controller;


import br.com.financial.system.account_system.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
public class BalanceController {

    private final AccountService accountService;

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance(
            @RequestParam("account_id") String accountId) {

        return ResponseEntity.ok(accountService.getBalance(accountId));
    }
}
