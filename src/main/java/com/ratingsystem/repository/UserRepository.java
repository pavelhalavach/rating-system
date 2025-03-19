package com.ratingsystem.repository;

import com.ratingsystem.entity.Role;
import com.ratingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    List<User> findAllByRole(Role role);
    Optional<User> findByIdAndRole(Integer id, Role role);
    Optional<User> findByNickname(String nickname);
    List<User> findAllByRoleAndIsVerified(Role role, boolean isVerified);
    Optional<User> findAllByIdAndIsVerified(Integer id, boolean isVerified);
}
