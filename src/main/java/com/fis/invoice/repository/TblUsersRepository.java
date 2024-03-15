package com.fis.invoice.repository;

import com.fis.invoice.domain.TblUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface TblUsersRepository extends JpaRepository<TblUsers, Integer> {
    List<TblUsers> findAllByUsername(String userName);

    List<TblUsers> findAllByUserIdIs (Integer userID);



    @Query("select tbl from TblUsers tbl " +
            "inner join ZtbMapCqt r on r.maCqt = tbl.maCqt " +
            "where tbl.userId = :groupId ")
    List<TblUsers> findAllTblUserGroupId(Integer groupId);
}
