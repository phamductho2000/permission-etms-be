package com.fis.invoice.repository;

import com.fis.invoice.domain.TblUsers;
import com.fis.invoice.domain.ZtbMapCqt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZtbMapCqtRepository extends JpaRepository<ZtbMapCqt, String> {
    // tìm kiếm theo MA_CQT
    List<ZtbMapCqt> findAllByMaCqt(String maCqt);
}
