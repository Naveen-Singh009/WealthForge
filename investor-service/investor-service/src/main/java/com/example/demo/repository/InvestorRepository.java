package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Investor;

public interface InvestorRepository extends JpaRepository<Investor,Long>{
}