package com.gaia3d.casapp.controller;

import com.gaia3d.casapp.service.AccidentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET})
@RequiredArgsConstructor
@RestController
@RequestMapping("/accident")
public class AccidentController {
    private final AccidentService accidentService;

    @GetMapping("/{accidentCode}")
    public ResponseEntity<?> getAccidentInfo(@PathVariable("accidentCode") String accidentCode) {
        var accidentInfo = accidentService.getAccidentInfo(accidentCode);
        return ResponseEntity.ok(accidentInfo);
    }

    @GetMapping("/{accidentCode}/chemical")
    public ResponseEntity<?> getAccidentChemicalInfo(@PathVariable("accidentCode") String accidentCode) {
        var accidentChemicalInfo = accidentService.getAccidentChemicalInfoRepository(accidentCode);
        return ResponseEntity.ok(accidentChemicalInfo);
    }

    @GetMapping("/{accidentCode}/grid")
    public ResponseEntity<?> getAccidentGridInfo(@PathVariable("accidentCode") String accidentCode, @RequestParam("analysisTime") String analysisTime, @RequestParam("gridId") int gridId) {
        var accidentGridInfo = accidentService.getByAccidentNoAndAnalysisTime(accidentCode, analysisTime, gridId);
        return ResponseEntity.ok(accidentGridInfo);
    }

    @GetMapping("/{accidentCode}/personal/{personalId}")
    public ResponseEntity<?> getPersonalLocationInfo(@PathVariable("accidentCode") String accidentCode, @PathVariable("personalId") String personalId) {
        var personalLocationInfo = accidentService.getPersonalLocationInfo(accidentCode, personalId);
        return ResponseEntity.ok(personalLocationInfo);
    }

    @GetMapping("/{accidentCode}/personal/{personalId}/risk")
    public ResponseEntity<?> getIndividualChrRisk(@PathVariable("accidentCode") String accidentCode, @PathVariable("personalId") String personalId) {
        var individualChrRisk = accidentService.getIndividualChrRisk(accidentCode, personalId);
        return ResponseEntity.ok(individualChrRisk);
    }
}
