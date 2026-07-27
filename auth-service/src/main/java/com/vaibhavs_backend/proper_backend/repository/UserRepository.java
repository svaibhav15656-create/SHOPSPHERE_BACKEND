package com.vaibhavs_backend.proper_backend.repository;
import com.vaibhavs_backend.proper_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long>{
    User findByEmail(String email);
}

