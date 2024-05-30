package com.waither.weatherservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.waither.weatherservice.entity.Region;

public interface RegionRepository extends JpaRepository<Region, Long> {

	@Query("SELECT r FROM Region r WHERE r.startLat <= :lat AND :lat <= r.endLat AND r.startLon <= :lon AND :lon <= r.endLon")
	List<Region> findRegionByLatAndLong(double lat, double lon);
}
