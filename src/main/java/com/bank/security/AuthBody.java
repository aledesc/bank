package com.bank.security;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthBody {

    private String username;
    private String password;

}
