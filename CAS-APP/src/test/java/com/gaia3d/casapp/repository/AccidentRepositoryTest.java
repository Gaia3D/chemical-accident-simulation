package com.gaia3d.casapp.repository;

import com.gaia3d.casapp.domain.AccidentInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class AccidentRepositoryTest {

    @Autowired
    AccidentRepository accidentRepository;

    @Test
    void getByAccidentNo() {
        var accidentInfo = accidentRepository.getByAccidentNo("CA201905001");
        log.info("data: {}", accidentInfo);
    }
}