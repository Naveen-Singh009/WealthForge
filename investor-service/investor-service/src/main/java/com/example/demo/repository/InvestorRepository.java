package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Investor;

import java.util.Optional;

public interface InvestorRepository extends JpaRepository<Investor,Long>{
    Optional<Investor> findByEmail(String email);
}
