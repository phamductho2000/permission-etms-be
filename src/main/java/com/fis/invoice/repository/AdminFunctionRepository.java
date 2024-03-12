package com.fis.invoice.repository;

import com.fis.invoice.domain.AdminFunction;
import com.fis.invoice.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminFunctionRepository extends JpaRepository<AdminFunction, String>, JpaSpecificationExecutor<AdminFunction> {
}
