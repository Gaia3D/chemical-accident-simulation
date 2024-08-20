package com.gaia3d.casapp.service;

import com.gaia3d.casapp.domain.*;
import com.gaia3d.casapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.proj4j.BasicCoordinateTransform;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.ProjCoordinate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccidentService {

    private final AccidentRepository accidentRepository;
    private final AccidentChemicalInfoRepository accidentChemicalInfoRepository;
    private final AccidentGridInfoRepository accidentGridInfoRepository;
    private final PersonalLocationRepository personalLocationRepository;
    private final IndividualChrRiskRepository individualChrRiskRepository;

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

        CRSFactory factory = new CRSFactory();
        CoordinateReferenceSystem wgs84 = factory.createFromParameters("WGS84", "+proj=longlat +datum=WGS84 +no_defs");
        CoordinateReferenceSystem utmk = factory.createFromParameters("EPSG:5179", "+proj=tmerc +lat_0=38 +lon_0=127.5 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=GRS80 +units=m +no_defs");
        BasicCoordinateTransform transformer = new BasicCoordinateTransform(utmk, wgs84);

        // UTMK 좌표를 경위도 좌표로 변환
        personalLocationInfos.forEach(personalLocationInfo -> {
            var utmkX = personalLocationInfo.getUtmkX();
            var utmkY = personalLocationInfo.getUtmkY();
            ProjCoordinate utmkCoordinate = new ProjCoordinate(utmkX, utmkY);
            ProjCoordinate wgsCoordinate = new ProjCoordinate();
            transformer.transform(utmkCoordinate, wgsCoordinate);

            log.info("UTMK 좌표: {}, {}", utmkX, utmkY);
            log.info("경위도 좌표: {}, {}", wgsCoordinate.x, wgsCoordinate.y);

            personalLocationInfo.setUtmkX(wgsCoordinate.x);
            personalLocationInfo.setUtmkY(wgsCoordinate.y);
        });
        return personalLocationInfos;
    }

    public IndividualChrRisk getIndividualChrRisk(String accidentNo, String personalId) {
        var individualChrRisk = individualChrRiskRepository.selectIndividualChrRisk(accidentNo, personalId);
        return individualChrRisk;
    }
}
