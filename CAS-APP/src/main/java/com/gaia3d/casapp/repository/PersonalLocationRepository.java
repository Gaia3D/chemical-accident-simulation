package com.gaia3d.casapp.repository;

import com.gaia3d.casapp.domain.PersonalLocationInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 사고지역 개인위치정보
 */
@Repository
public interface PersonalLocationRepository {
    List<PersonalLocationInfo> getByAccidentNoAndPersonalId(@Param("accidentNo") String accidentNo, @Param("personalId") String personalId);
}
