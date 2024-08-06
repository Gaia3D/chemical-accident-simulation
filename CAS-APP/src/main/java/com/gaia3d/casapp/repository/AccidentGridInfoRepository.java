package com.gaia3d.casapp.repository;

import com.gaia3d.casapp.domain.AccidentGridInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 사고지역 격자별 정보 (10분 간격)
 */
@Repository
public interface AccidentGridInfoRepository {
    List<AccidentGridInfo> getGrid(@Param("accidentNo") String accidentNo, @Param("analysisTime") String analysisTime, @Param("gridId") int gridId);
}
