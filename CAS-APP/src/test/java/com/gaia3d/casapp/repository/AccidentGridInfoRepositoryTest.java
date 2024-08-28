
package com.gaia3d.casapp.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class AccidentGridInfoRepositoryTest {

    @Autowired
    AccidentGridInfoRepository accidentGridInfoRepository;

    @Test
    void getByAccidentNo() {
        var accidentGridInfo = accidentGridInfoRepository.getByAccidentNo("CA201905001");
        log.info("data: {}", accidentGridInfo);
    }
}