package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Holding;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {

	/* Find single stock holding */
	Holding findByInvestorIdAndAssetName(Long investorId, String assetName);

	/* Find all holdings of investor */
	List<Holding> findByInvestorId(Long investorId);

}