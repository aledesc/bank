package com.bank.service.impl;

import com.bank.dto.MovementsLstDto;
import com.bank.service.BankService;
import com.bank.util.Result;
import com.bank.util.ResultStatus;
import lombok.AllArgsConstructor;
import org.jvnet.hk2.annotations.Service;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

import static com.bank.util.Constants.DATABASE_NAME;


@AllArgsConstructor
@Service
@Component
public class BankServiceImpl implements BankService {

    private final Neo4jClient client;
    private final String USER_OPENS_ACCOUNT= " MATCH (u:User {username:$userName})-[h:OPENS_ACCOUNT]->(a:Account {name:$accountName}) ";


    @Override
    @ResponseBody
    public Result getMovements(String userName, String accountName) {

        if ( ! checkUser( userName ) )
            return new Result("User does not exist!",ResultStatus.NOK);

        Double balance= checkAccount(userName,accountName);

        if (balance == null)
            return new Result("User does not have a " + accountName + " account",ResultStatus.NOK);

        final String USER_WITHDRAWS= " MATCH (u:User {username:$userName})-[w:WITHDRAW]->(a:Account {name:$accountName}) ";
        final String USER_DEPOSITS= " MATCH (u:User {username:$userName})-[d:DEPOSIT]->(a:Account {name:$accountName}) ";

        final String qry = "CALL {" +
                USER_OPENS_ACCOUNT + " RETURN apoc.temporal.format( h.since, 'YYYY-MM-dd HH:mm:ss') AS date, apoc.temporal.format( h.since, 'YYYYMMddHHmmss.SSS') AS stamp, type(h) AS type, h.amount AS amount" +
                " UNION" +
                USER_WITHDRAWS + " RETURN apoc.temporal.format( w.date, 'YYYY-MM-dd HH:mm:ss') AS date, apoc.temporal.format( w.date, 'YYYYMMddHHmmss.SSS') AS stamp, type(w) AS type, w.amount AS amount ORDER BY stamp ASC" +
                " UNION " +
                USER_DEPOSITS + " RETURN apoc.temporal.format( d.date, 'YYYY-MM-dd HH:mm:ss') AS date, apoc.temporal.format( d.date, 'YYYYMMddHHmmss.SSS') AS stamp, type(d) AS type, d.amount AS amount ORDER BY stamp ASC" +
                "}" +
                " RETURN stamp, date, type, amount ORDER by stamp ASC";

        Collection<MovementsLstDto> moves = client.query(qry)
                .in(DATABASE_NAME)
                .bind(userName).to("userName")
                .bind(accountName).to("accountName")
                .fetchAs(MovementsLstDto.class)
                .mappedBy((typeSystem, record) -> new MovementsLstDto(record.get("date").asString(), record.get("stamp").asString(), record.get("type").asString(), record.get("amount").asDouble()))
                .all();

        return new Result( moves );
    }

    @Override
    public Result deposit(String userName, String accountName, Float amount)
    {
        if ( ! checkUser( userName ) )
            return new Result("User does not exist!",ResultStatus.NOK);

        Double balance= checkAccount(userName,accountName);

        if (balance == null)
            return new Result("User does not have a " + accountName + " account",ResultStatus.NOK);

        final String qry = USER_OPENS_ACCOUNT  +
                " CREATE (u)-[d:DEPOSIT {amount:$amount,date:datetime()}]->(a)" +
                " SET a.balance=a.balance+d.amount" +
                " RETURN a.balance as balance";

        Optional<Float> mov= client.query(qry)

                .in(DATABASE_NAME)
                .bind(userName).to("userName")
                .bind(accountName).to("accountName")
                .bind(amount).to("amount")
                .fetchAs(Float.class)
                .mappedBy(((typeSystem, record) -> record.get("balance").asFloat()))
                .first();

        return new Result("Deposited " + amount.toString() + " in Account: " + accountName, ResultStatus.OK);
    }

    @Override
    public Result withdraw(String userName, String accountName, Float amount) {

        if ( ! checkUser( userName ) )
            return new Result("User does not exist!",ResultStatus.NOK);

        Double balance= checkAccount(userName,accountName);

        if (balance == null)
            return new Result("User does not have a " + accountName + " account",ResultStatus.NOK);

        if (balance < amount)
            return new Result("Not enough funds on Account: " + accountName, ResultStatus.NOK);

        String qry = USER_OPENS_ACCOUNT +
                " CREATE (u)-[d:WITHDRAW {amount:$amount,date:datetime()}]->(a)" +
                " SET a.balance=a.balance-d.amount" +
                " RETURN a.balance as balance";

        Optional<Double> mov= client.query(qry)

                .in(DATABASE_NAME)
                .bind(userName).to("userName")
                .bind(accountName).to("accountName")
                .bind(amount).to("amount")
                .fetchAs(Double.class)
                .mappedBy(((typeSystem, record) -> record.get("balance").asDouble()))
                .first();

        return new Result("Withdrawn " + amount.toString() + " from Account: " + accountName, ResultStatus.OK);
    }

    @Override
    public Result createAccount(String userName, Float initialBalance) {

        if ( ! checkUser( userName ) )
            return new Result("User does not exist!",ResultStatus.NOK);

        final String[] names = {"First", "Second", "Third"};
        final String USER_HAS_ACCOUNT= "MATCH (u:User {username:$userName})-[h:OPENS_ACCOUNT]->(a:Account) ";

        Optional<Integer> many = client.query( USER_HAS_ACCOUNT + " RETURN count(*) AS many")
                .in(DATABASE_NAME)
                .bind(userName).to("userName")
                .fetchAs(Integer.class)
                .mappedBy(((typeSystem, record) -> record.get("many").asInt()))
                .first();

        if (many.get() == names.length)
            return new Result("Do you think accounts are infinite? You have already THREE allowed accounts!", ResultStatus.OK);

        String accountName = names[many.get()];

        Optional<String> account = client.query("MATCH (u:User) WHERE u.username=$username" +
                        " CREATE (a:Account {name:$accountName,balance:$initialBalance})<-[:OPENS_ACCOUNT {amount:$initialBalance,since:datetime()} ]-(u) " +
                        " RETURN a.name as name")
                .in(DATABASE_NAME)
                .bind(userName).to("username")
                .bind(accountName).to("accountName")
                .bind(initialBalance).to("initialBalance")
                .fetchAs(String.class)
                .mappedBy(((typeSystem, record) -> record.get("name").asString()))
                .first();

        return new Result("Created " + account.get() + " account  for " + userName + ". Initial balance: " + initialBalance.toString() + " €", ResultStatus.OK);
    }


    private boolean checkUser(String userName)
    {
        final String qry = "MATCH (u:User {username:$userName}) RETURN u.username AS username";
        String name= null;

        try {
            name= client.query(qry)
                    .in(DATABASE_NAME)
                    .bind(userName).to("userName")
                    .fetchAs(String.class)
                    .mappedBy((typeSystem, record) -> record.get("username").asString())
                    .first()
                    .get();
        }
        catch(NoSuchElementException e)
        {
            name= null;
            e.printStackTrace();
        }

        return name!=null && name.equals( userName );
    }

    private Double checkAccount(String userName, String accountName) {
        String qry = USER_OPENS_ACCOUNT + " RETURN a.balance as balance";

        Double balance= null;
        try {
            balance = client.query(qry)
                    .in(DATABASE_NAME)
                    .bind(userName).to("userName")
                    .bind(accountName).to("accountName")
                    .fetchAs(Double.class)
                    .mappedBy(((typeSystem, record) -> record.get("balance").asDouble()))
                    .first()
                    .get();
        }
        catch(NoSuchElementException e)
        {
            balance= null;
            e.printStackTrace();
        }

        return balance;
    }
}