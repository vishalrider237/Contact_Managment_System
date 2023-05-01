package com.smart.dao;

import com.smart.entities.Contact;
import com.smart.entities.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    @Query("from Contact c where c.user.id=:userID")
    // Current Page-page
    // Contact Per Page-5
    public Page<Contact> findContactByUser(@Param("userID") int userID, Pageable pageable);
    @Modifying
    @Transactional
    @Query("delete from Contact c where c.cid =:id")
    public void deleteContactById(@Param("id") Integer id);
    public List<Contact> findByNameContainingAndUser(String name, User user);
}
