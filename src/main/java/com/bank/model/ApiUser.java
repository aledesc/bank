package com.bank.model;

import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

@Node(labels= "User")
@Setter
@Getter
@NoArgsConstructor

@ToString
public class ApiUser {

    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String username;

    @Property
    private String password;

    @Property
    private boolean enabled;

    @Relationship(type = "HAS_ROLE")
    private Role role;

    public ApiUser(Long id, String username, String password, boolean enabled, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.role= role;
    }
}
