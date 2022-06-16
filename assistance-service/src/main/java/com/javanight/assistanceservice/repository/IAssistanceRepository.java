package com.javanight.assistanceservice.repository;

import com.javanight.assistanceservice.entity.Assistance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAssistanceRepository extends JpaRepository<Assistance, Integer> {
}
