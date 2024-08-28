package com.gaia3d.casapp.repository;

import com.gaia3d.casapp.domain.AccidentChemicalInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 사고대비물질정보
 */
@Repository
public interface AccidentChemicalInfoRepository {
    AccidentChemicalInfo getByAccidentNo(@Param("accidentNo") String accidentNo);
}
