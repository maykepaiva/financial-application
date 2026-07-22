package br.com.financial.system.account_system.dto;

import br.com.financial.system.account_system.model.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private Account origin;
    private Account destination;
}

