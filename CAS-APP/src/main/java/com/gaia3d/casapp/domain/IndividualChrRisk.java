package com.gaia3d.casapp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 개인별 만성 위해도
 */
@Getter
@Setter
@Data
@AllArgsConstructor
public class IndividualChrRisk {
    /*
         no
        accident_no
        personal_id
        chronic_exp_inhal_carcin
        chronic_exposure_amt
        chronic_exp_soil_ingcarc
        chronic_exp_soil_ingnoncarc
        chronic_hazard_inhcarc
        chronic_hazard_index
        chronic_hazard_soil_ingcarc
        chronic_hazard_soil_ingnoncarc
        chronic_resp_carc_damage
        chronic_resp_noncarc_damage
        chronic_soil_carc_damage
        chronic_soil_noncarc_risk
        indi_damage_grade
        aegl_eval_max
    */
    private int no; // 번호
    private String accidentNo; // 사고번호
    private String personalId; // 개인ID
    private String chronicExpInhalCarcin; // 만성노출흡입암
    private String chronicExposureAmt; // 만성노출량
    private String chronicExpSoilIngcarc; // 만성노출토양흡입암
    private String chronicExpSoilIngnoncarc; // 만성노출토양흡입비암
    private String chronicHazardInhcarc; // 만성위해흡입암
    private String chronicHazardIndex; // 만성위해지수
    private String chronicHazardSoilIngcarc; // 만성위해토양흡입암
    private String chronicHazardSoilIngnoncarc; // 만성위해토양흡입비암
    private String chronicRespCarcDamage; // 만성흡입암피해
    private String chronicRespNoncarcDamage; // 만성흡입비암피해
    private String chronicSoilCarcDamage; // 만성토양흡입암피해
    private String chronicSoilNoncarcRisk; // 만성토양흡입비암위험
    private String indiDamageGrade; // 개인피해등급
    private String aeglEvalMax; // AEGl평가최대
}
