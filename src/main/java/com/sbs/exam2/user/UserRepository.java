package com.sbs.exam2.user;

import com.sbs.exam2.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<SiteUser, Long> {
    Optional<SiteUser> findByUsername(String username);

    Optional<SiteUser> findByEmail(String email);

    Optional<SiteUser> findByEmailAndUsername(String email, String username);
}
