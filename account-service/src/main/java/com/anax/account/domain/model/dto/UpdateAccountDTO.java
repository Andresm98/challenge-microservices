package com.anax.account.domain.model.dto;

import lombok.Data;

@Data
public class UpdateAccountDTO {
    private String accountNumber;
    private String accountType;
    private Boolean status;
    //    private Long customerId;  -> TODO:: en caso de que se permita cambiar el cliente
}