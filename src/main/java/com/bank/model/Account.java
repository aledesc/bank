package com.bank.model;

import com.bank.domain.Deposit;
import com.bank.domain.Withdraw;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;

@Node(labels = "Account")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Account
{
    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String name;

    @Property
    private Float balance;

    @Relationship(type= "DEPOSIT", direction = Relationship.Direction.INCOMING)
    private List<Deposit> deposits;

    @Relationship(type= "WITHDRAW", direction = Relationship.Direction.INCOMING)
    private List<Withdraw> withdraws;
}
