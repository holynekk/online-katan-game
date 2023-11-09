package com.group12.repository;

import com.group12.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
  Optional<User> findByUsername(String encryptedUsername);

  Optional<User> findByEmail(String userEmail);

}