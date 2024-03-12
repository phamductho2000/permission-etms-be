package com.fis.invoice.repository;

import com.fis.invoice.domain.AdminGroupUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminGroupUserRepository extends JpaRepository<AdminGroupUser,String>, JpaSpecificationExecutor<AdminGroupUser> {
}
