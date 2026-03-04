package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Advisor;
@Repository
public interface AdvisorRepository extends JpaRepository<Advisor, Long> {
}