package com.fis.invoice.repository;

import com.fis.invoice.domain.TblUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TblUsersRepository extends JpaRepository<TblUsers, String>, JpaSpecificationExecutor<TblUsers> {
    List<TblUsers> findAllByUsername(String userName);
}
