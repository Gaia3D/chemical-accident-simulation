package com.gaia3d.casapp.repository;

import org.springframework.stereotype.Repository;

@Repository()
public interface SimulationRepository {
    String selectSimulation();
}
