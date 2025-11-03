package ru.mifiSoul.MultiBankApp.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mifiSoul.MultiBankApp.database.entity.Role;
import ru.mifiSoul.MultiBankApp.database.entity.RoleEntity;

import java.util.Optional;
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRole(Role role);
}
