package com.anax.customer.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerChangedEvent {
    private String eventId;
    private String eventType;
    private Long customerId;
    private String name;
    private String gender;
    private Integer age;
    private String identification;
    private String address;
    private String phone;
    private Boolean status;
    private String occurredAt;
}