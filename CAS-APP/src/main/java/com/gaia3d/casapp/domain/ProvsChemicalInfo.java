package com.gaia3d.casapp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
public class ProvsChemicalInfo {
    /*
        CHMCLS_NM
        CHMCLS_CL_CN
        CAS_NO
        MOLE_CN
        MELTPT_CN
        BOILPT_CN
        WATER_SLBL_CN
        VAPORPRSR_CN
        HENRRYCONST_CN
        KOW_CN
     */
    private String chmclsNm; // 화학물질 명
    private String chmclsClCn; // 화학물질 분류
    private String casNo; // CAS NO
    private String moleCn; // 분자량
    private String meltptCn; // 녹는점
    private String boilptCn; // 끓는점
    private String waterSlblCn; // 수용성
    private String vaporprsrCn; // 증기압
    private String henrryconstCn; // 헨리 상수
    private String kowCn; // 옥탄올 물 배수정보
}
