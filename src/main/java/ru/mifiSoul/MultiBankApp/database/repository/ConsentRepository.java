package ru.mifiSoul.MultiBankApp.database.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mifiSoul.MultiBankApp.database.entity.Consent;

import java.util.Optional;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, Long> {
    Optional<Consent> findByBankIdentifier(String bankIdentifier);
}