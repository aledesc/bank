package com.bank.dto;

import com.bank.domain.IMovement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MovementsLstDto implements IMovement
{
    private String date;
    private String stamp;
    private String type;
    private Double amount;
}
