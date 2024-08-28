package com.gaia3d.casapp.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class SimulationRepositoryTest {

    @Autowired
    SimulationRepository simulationRepository;

    @Test
    void selectSimulation() {
        String test = simulationRepository.selectSimulation();
        System.out.println(test);
        log.info("test: {}", test);
    }
}