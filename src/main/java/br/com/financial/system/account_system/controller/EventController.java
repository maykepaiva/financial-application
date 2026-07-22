package br.com.financial.system.account_system.controller;

import br.com.financial.system.account_system.dto.AccountResponse;
import br.com.financial.system.account_system.dto.EventRequest;
import br.com.financial.system.account_system.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final AccountService accountService;

    @PostMapping("/event")
    public ResponseEntity<AccountResponse> event(
            @RequestBody EventRequest request) {

        AccountResponse response = switch (request.getType()) {

            case "deposit" ->
                    accountService.deposit(request);

            case "withdraw" ->
                    accountService.withdraw(request);

            case "transfer" ->
                    accountService.transfer(request);

            default ->
                    throw new IllegalArgumentException("Invalid event type");
        };

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
