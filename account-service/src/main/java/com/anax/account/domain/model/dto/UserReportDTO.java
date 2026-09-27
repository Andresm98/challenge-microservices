package com.anax.account.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReportDTO {
    private CustomerProfileDTO usuario;
    private List<AccountDTO> cuentas;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountDTO {
        private Long id;
        private String accountNumber;
        private String accountType;
        private Double initialBalance;
        private Boolean status;
        private List<MovementDTO> movimientos;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovementDTO {
        private Long id;
        private LocalDateTime date;
        private String movementType;
        private Double value;
        private Double balance;
    }
}