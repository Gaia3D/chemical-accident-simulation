package com.gaia3d.casapp.service;

import com.gaia3d.casapp.domain.AccidentChemicalInfo;
import com.gaia3d.casapp.domain.AccidentGridInfo;
import com.gaia3d.casapp.domain.AccidentInfo;
import com.gaia3d.casapp.domain.PersonalLocationInfo;
import com.gaia3d.casapp.repository.AccidentChemicalInfoRepository;
import com.gaia3d.casapp.repository.AccidentGridInfoRepository;
import com.gaia3d.casapp.repository.AccidentRepository;
import com.gaia3d.casapp.repository.PersonalLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AccidentService {

    private final AccidentRepository accidentRepository;
    private final AccidentChemicalInfoRepository accidentChemicalInfoRepository;
    private final AccidentGridInfoRepository accidentGridInfoRepository;
    private final PersonalLocationRepository personalLocationRepository;

    public AccidentInfo getAccidentInfo(String accidentCode) {
        var accidentInfo = accidentRepository.getByAccidentNo(accidentCode);
        return accidentInfo;
    }

    public AccidentChemicalInfo getAccidentChemicalInfoRepository(String accidentCode) {
        var accidentChemicalInfo = accidentChemicalInfoRepository.getByAccidentNo(accidentCode);
        return accidentChemicalInfo;
    }

    public List<AccidentGridInfo> getByAccidentNoAndAnalysisTime(String accidentCode, String analysisTime, int gridId) {
        var accidentGridInfo = accidentGridInfoRepository.getGrid(accidentCode, analysisTime, gridId);
        return accidentGridInfo;
    }

    public List<PersonalLocationInfo> getPersonalLocationInfo(String accidentCode, String personalId) {
        var personalLocationInfos = personalLocationRepository.getByAccidentNoAndPersonalId(accidentCode, personalId);
        return personalLocationInfos;
    }
}
