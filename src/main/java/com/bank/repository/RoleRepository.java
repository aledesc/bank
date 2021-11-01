package com.bank.repository;

import com.bank.model.Role;
import org.springframework.data.repository.query.Param;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends Neo4jRepository<Role, Long>
{
    Role findByAuthority(@Param("authority") String authority);
}
