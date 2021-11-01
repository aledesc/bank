package com.bank.model;

import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node(labels = "Role")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Role
{
    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String authority;
}
