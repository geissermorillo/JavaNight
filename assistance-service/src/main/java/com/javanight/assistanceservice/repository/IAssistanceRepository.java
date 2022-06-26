package com.javanight.assistanceservice.repository;

import com.javanight.assistanceservice.entity.Assistance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAssistanceRepository extends JpaRepository<Assistance, Integer> {
}
