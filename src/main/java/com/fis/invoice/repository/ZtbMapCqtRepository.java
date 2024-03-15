package com.fis.invoice.repository;

import com.fis.invoice.domain.ZtbMapCqt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZtbMapCqtRepository extends JpaRepository<ZtbMapCqt, String> {
}
