package com.gaia3d.casapp.repository;

import com.gaia3d.casapp.domain.AccidentInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 사고정보
 */
@Repository
public interface AccidentRepository {
    AccidentInfo getByAccidentNo(@Param("accidentNo") String accidentNo);
}
