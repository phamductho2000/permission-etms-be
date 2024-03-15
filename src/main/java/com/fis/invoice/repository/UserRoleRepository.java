package com.fis.invoice.repository;

import com.fis.invoice.domain.TblUsers;
import com.fis.invoice.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer>, JpaSpecificationExecutor<UserRole> {

    @Query("select us from UserRole us " +
            "where us.userId = :groupId ")
    List<UserRole> findAllByUserId(Integer groupId);
}
