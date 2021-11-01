package com.bank.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto
{
    private String userName;
    private Float initialBalance;
}
