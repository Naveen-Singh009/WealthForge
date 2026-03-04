package com.example.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Advice;

public interface AdviceRepository extends JpaRepository<Advice, Long> {

    List<Advice> findByAdvisorId(Long advisorId);
}