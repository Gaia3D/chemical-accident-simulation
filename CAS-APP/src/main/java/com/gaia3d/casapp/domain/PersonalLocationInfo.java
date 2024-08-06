package com.gaia3d.casapp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 사고지역 개인위치정보
 */
@Getter
@Setter
@Data
@AllArgsConstructor
public class PersonalLocationInfo {
    /*
        no
        accident_no
        personal_id
        time
        utmk_x
        utmk_y
        grid_id
        gender
        age
        concentration
        exposure
        aegl1_eval
        aegl2_eval
        aegl3_eval
    */

    private int no; // 연번
    private String accidentNo; // 사고번호
    private String personalId; // 개인ID
    private String time; // 시간
    private double utmkX; // 위치좌표 UTMK X
    private double utmkY; // 위치좌표 UTMK Y
    private int gridId; // 격자ID
    private String gender; // 성별
    private String age; // 연령
    private double concentration; // 사고물질 농도
    private double exposure; // 노출량
    private double aegl1Eval; // AEGL-1 평가결과
    private double aegl2Eval; // AEGL-2 평가결과
    private double aegl3Eval; // AEGL-3 평가결과
}
