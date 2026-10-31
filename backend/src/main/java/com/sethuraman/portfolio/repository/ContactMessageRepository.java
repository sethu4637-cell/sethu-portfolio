package com.sethuraman.portfolio.repository;

import com.sethuraman.portfolio.entity.ContactMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ContactMessage entities.
 */
@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    /**
     * Returns the N most recent contact messages, ordered newest-first.
     */
    List<ContactMessage> findAllByOrderBySubmittedAtDesc(Pageable pageable);
}
