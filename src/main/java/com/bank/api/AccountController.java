package com.bank.api;

import com.bank.dto.AccountDto;
import com.bank.dto.MovementDto;
import com.bank.dto.SimpleMovementDto;
import com.bank.service.BankService;
import com.bank.util.Constants;
import com.bank.util.Result;
import com.bank.util.ResultStatus;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(maxAge= Constants.CORS_AGE)
@NoArgsConstructor
@RestController
@RequestMapping("/account")
public class AccountController {
    final Logger logger = LoggerFactory.getLogger(AccountController.class);
    final String TAG = "Account Controller --> ";

    @Autowired
    private BankService service;

    @CrossOrigin(origins= Constants.CORS_ORIGIN)
    @PostMapping(value="/create-account",consumes= MediaType.APPLICATION_JSON_VALUE,produces= {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Result> createAccount(@RequestBody AccountDto accountDto)
    {
        if( service==null )
        {
            return ResponseEntity.ok( new Result("¡Autowired Service is NULL!", ResultStatus.NOK) );
        }

        if( accountDto==null ) {
            return ResponseEntity.ok( new Result("¡AccountDto is NULL!", ResultStatus.NOK) );
        }

        Result result = service.createAccount(accountDto.getUserName(), accountDto.getInitialBalance() );

        return ResponseEntity.ok(result);
    }

    @CrossOrigin(origins= Constants.CORS_ORIGIN)
    @PostMapping(value="/withdraw",consumes= MediaType.APPLICATION_JSON_VALUE,produces= {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Result> withdraw(@RequestBody MovementDto movement )
    {
        if( service==null )
        {
            return ResponseEntity.ok( new Result("¡Autowired Service is NULL!", ResultStatus.NOK) );
        }

        if( movement==null ) {
            return ResponseEntity.ok( new Result("¡MovementDto is NULL!", ResultStatus.NOK) );
        }

        Result result = service.withdraw(movement.getUserName(), movement.getAccountName(), movement.getAmount() );

        return ResponseEntity.ok(result);
    }

    @CrossOrigin(origins= Constants.CORS_ORIGIN)
    @PostMapping(value="/deposit",consumes= MediaType.APPLICATION_JSON_VALUE,produces= {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Result> deposit(@RequestBody MovementDto movement )
    {
        if( service==null )
        {
            return ResponseEntity.ok( new Result("¡Autowired Service is NULL!", ResultStatus.NOK) );
        }

        if( movement==null ) {
            return ResponseEntity.ok( new Result("¡MovementDto is NULL!", ResultStatus.NOK) );
        }

        Result result = service.deposit(movement.getUserName(), movement.getAccountName(), movement.getAmount() );

        return ResponseEntity.ok(result);
    }

    @CrossOrigin(origins= Constants.CORS_ORIGIN)
    @PostMapping(value="/movs",consumes= MediaType.APPLICATION_JSON_VALUE,produces= {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Result> getMovements(@RequestBody SimpleMovementDto movement )
    {
        if( service==null )
        {
            return ResponseEntity.ok( new Result("¡Autowired Service is NULL!", ResultStatus.NOK) );
        }

        if( movement==null ) {
            return ResponseEntity.ok( new Result("¡movementDto is NULL!", ResultStatus.NOK) );
        }

        Result result= service.getMovements(movement.getUserName(), movement.getAccountName());

        return ResponseEntity.ok(result);
    }



}

