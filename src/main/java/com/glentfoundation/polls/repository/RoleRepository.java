package com.glentfoundation.polls.repository;

import com.glentfoundation.polls.models.Role;
import com.glentfoundation.polls.models.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
