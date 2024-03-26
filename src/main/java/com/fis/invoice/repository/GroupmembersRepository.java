package com.fis.invoice.repository;

import com.fis.invoice.domain.Groupmembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupmembersRepository extends JpaRepository<Groupmembers, String> {

}
