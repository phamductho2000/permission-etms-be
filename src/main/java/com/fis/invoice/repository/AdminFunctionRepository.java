package com.fis.invoice.repository;

import com.fis.invoice.domain.AdminFunction;
import com.fis.invoice.domain.TblUsers;
import com.fis.invoice.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminFunctionRepository extends JpaRepository<AdminFunction, Integer>, JpaSpecificationExecutor<AdminFunction> {

    // tìm kiếm theo FuncName
    List<AdminFunction> findAllByFuncName(String FuncName);

}
