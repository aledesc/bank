package com.bank.repository;

import com.bank.model.ApiUser;
import org.springframework.data.repository.query.Param;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends Neo4jRepository<ApiUser, Long>
{
    ApiUser findByUsername(@Param("username") String username);
}
