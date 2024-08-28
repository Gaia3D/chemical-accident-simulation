package com.gaia3d.casapp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 사고지역 격자별 정보(10분 간격)
 */
@Getter
@Setter
@Data
@AllArgsConstructor
public class AccidentGridInfo {
    /*
        no
        accident_no
        analysis_time
        grid_id
        concentration
        exposure
        aegl1_eval
        aegl2_eval
        aegl3_eval
        victim_count
     */
    private int no; // 연번
    private String accidentNo; // 사고번호
    private String analysisTime; // 분석시간
    private int gridId; // 격자 ID
    private double concentration; // 사고물질 농도
    private double exposure; // 노출량
    private double aegl1Eval; // AEGl-1 평가결과
    private double aegl2Eval; // AEGl-2 평가결과
    private double aegl3Eval; // AEGl-3 평가결과
    private int victimCount; // 피해자수
}
