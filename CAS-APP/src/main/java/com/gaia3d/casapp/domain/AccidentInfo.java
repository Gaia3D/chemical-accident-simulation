package com.gaia3d.casapp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 사고정보
 */
@Getter
@Setter
@Data
@AllArgsConstructor
public class AccidentInfo {
    /*
        accident_no
        accident_dttm
        jibun_addr
        road_addr
        x_cordinate
        y_cordinate
        chemical_no
        leak_amount
        unit
        accident_type
        accident_cause
        accident_content
        regist_id
        leak_speed
        leak_time
        simulation_time
        extinct_air
        extinct_soil
        simulation_state
        regist_time
        search_time
        region_cd
        calculate_state
        expct_vctm_high
        expct_vctm_mddl
        expct_vctm_low
        expct_vctm_none
    */
    String accidentNo; // 사고번호
    String accidentDttm; // 사고일시
    String jibunAddr; // 지번주소
    String roadAddr; // 도로주소
    double xCordinate; // X좌표
    double yCordinate; // Y좌표
    int chemicalNo; // 물질 순번
    int leakAmount; // 누출량
    String unit; // 단위
    String accidentType; // 사고유형
    String accidentCause; // 사고원인
    String accidentContent; // 사고내용
    String registId; // 등록아이디
    int leakSpeed; // 누출속도
    int leakTime; // 누출시간
    int simulationTime; // 시뮬레이션 시간
    int extinctAir; // 소멸기준 농도 대기
    int extinctSoil; // 소멸기준 농도 토양
    String simulationState; // 모형구동상태
    String registTime; // 등록시간
    String searchTime; // 조회시간
    String regionCd; // 지역코드
    String calculateState; // 계산상태
    int expctVctmHigh; // 예상피해자 고위험
    int expctVctmMddl; // 예상피해자 중위험
    int expctVctmLow; // 예상피해자 저위험
    int expctVctmNone; // 예상피해자 해당없음


}
