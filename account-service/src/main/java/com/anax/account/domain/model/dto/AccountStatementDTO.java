package com.anax.account.domain.model.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AccountStatementDTO {

    private LocalDateTime date;
    private String customer;
    private String accountNumber;
    private String type;
    private Double initialBalance;
    private Boolean status;
    private Double movement;
    private Double availableBalance;

}
