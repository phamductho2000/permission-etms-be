package com.fis.invoice.repository;

import com.fis.invoice.domain.DemoTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface DemoTableRepository extends JpaRepository<DemoTable, BigInteger>, JpaSpecificationExecutor<DemoTable> {
}
