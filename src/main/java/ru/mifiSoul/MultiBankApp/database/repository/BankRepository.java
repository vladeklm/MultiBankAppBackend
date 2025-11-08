package ru.mifiSoul.MultiBankApp.database.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mifiSoul.MultiBankApp.database.entity.Bank;

import java.util.Optional;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
    Optional<Bank> findByName(String name);

    boolean existsByName(String name);
}
