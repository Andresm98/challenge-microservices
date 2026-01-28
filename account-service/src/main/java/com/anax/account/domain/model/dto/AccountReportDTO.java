package com.anax.account.domain.model.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountReportDTO {
    private String date;
    private String customerName;
    private String accountNumber;
    private String type;
    private Double initialBalance;
    private Boolean status;
    private Double movementValue;
    private Double availableBalance;
}