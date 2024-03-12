package com.fis.invoice.repository;

import com.fis.invoice.domain.AdminRoleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRoleUserRepository extends JpaRepository<AdminRoleUser, String>, JpaSpecificationExecutor<AdminRoleUser> {
}
