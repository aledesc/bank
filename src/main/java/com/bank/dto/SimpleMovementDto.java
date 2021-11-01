package com.bank.dto;

import com.bank.domain.IMovement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SimpleMovementDto implements IMovement
{
    private String userName;
    private String accountName;
}
