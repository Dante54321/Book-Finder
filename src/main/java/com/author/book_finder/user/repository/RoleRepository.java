package com.author.book_finder.user.repository;

import com.author.book_finder.user.entity.Role;
import com.author.book_finder.user.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(RoleName roleName);

}
