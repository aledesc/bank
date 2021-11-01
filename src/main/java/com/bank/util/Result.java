package com.bank.util;

import com.bank.dto.MovementsLstDto;
import lombok.Getter;

import java.util.Collection;

@Getter
public class Result {
    public static final int NOK= 0;
    public static final int OK= 1;

    private final String msg;
    private final ResultStatus result;
    private Collection<MovementsLstDto> data;

    public Result()
    {
        result= ResultStatus.OK;
        msg= "All right!";
        data= null;
    }

    public Result(String msg, ResultStatus status)
    {
        this.result= status;
        this.msg= msg;
        data= null;
    }

    public Result(Collection<MovementsLstDto> data)
    {
        this();
        this.data= data;
    }


}
