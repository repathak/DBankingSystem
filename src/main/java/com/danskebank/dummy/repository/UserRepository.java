package com.danskebank.dummy.repository;

import com.danskebank.dummy.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {
    boolean existsByAccountNumber(String accountNumber);

    Users findByAccountNumber(String accountNumber);

    //boolean  existByEmail(String email);

    Optional<Users> findByEmail(String email);

    boolean existsByEmail(String email);
}
