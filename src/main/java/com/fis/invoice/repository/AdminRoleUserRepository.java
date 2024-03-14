package com.fis.invoice.repository;

import com.fis.invoice.domain.AdminFunction;
import com.fis.invoice.domain.AdminRoleUser;
import com.fis.invoice.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface AdminRoleUserRepository extends JpaRepository<AdminRoleUser, Integer>, JpaSpecificationExecutor<AdminRoleUser> {

    List<AdminRoleUser> findAllByRoleId (Integer roleId);

    @Query("select ur from AdminRoleUser adu " +
            "join AdminRole r on r.roleId = adu.roleId " +
            "join UserRole ur on ur.userId = adu.userId " +
            "where r.roleId =:groupId ")
    List<UserRole> findAllRoleUserGroupId(BigInteger groupId);
}
