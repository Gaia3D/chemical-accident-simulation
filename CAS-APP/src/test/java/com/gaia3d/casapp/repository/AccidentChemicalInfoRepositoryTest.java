package com.gaia3d.casapp.repository;

import com.gaia3d.casapp.domain.AccidentChemicalInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class AccidentChemicalInfoRepositoryTest {

    @Autowired
    AccidentChemicalInfoRepository accidentChemicalInfoRepository;

    @Test
    void getByAccidentNo() {
        var accidentChemicalInfo = accidentChemicalInfoRepository.getByAccidentNo("CA201905001");
        log.info("data: {}", accidentChemicalInfo);
    }
}