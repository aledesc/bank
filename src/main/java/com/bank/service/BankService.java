package com.bank.service;

import com.bank.util.Result;

public interface BankService
{
    Result createAccount(String userName, Float initialBalance);
    Result deposit(String userName, String accountName, Float amount);
    Result withdraw(String userName, String accountName, Float amount);

    Result getMovements(String userName, String accountName);
}
