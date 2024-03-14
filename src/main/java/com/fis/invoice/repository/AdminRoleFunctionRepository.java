package com.fis.invoice.repository;

import com.fis.invoice.domain.AdminFunction;
import com.fis.invoice.domain.AdminRoleFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRoleFunctionRepository extends JpaRepository<AdminRoleFunction, Integer>, JpaSpecificationExecutor<AdminRoleFunction> {

    List<AdminRoleFunction> findAllByRoleId (Integer roleId);

    @Query("select tk from AdminRoleFunction arf " +
            "join AdminRole r on r.roleId = arf.roleId " +
            "join AdminFunction tk on tk.funcId = arf.funcId " +
            "where r.roleId =:groupId ")
    List<AdminFunction> findAllRoleFunctionGroupId(Integer groupId);
}
