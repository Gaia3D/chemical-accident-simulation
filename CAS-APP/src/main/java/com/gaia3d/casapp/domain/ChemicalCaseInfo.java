package com.gaia3d.casapp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
public class ChemicalCaseInfo {
    /*
        ACDNT_CASE_SN
        ACDNT_DE
        FGT_RCEPT_TIME
        ACDNT_ENTRPS_NM
        ACDNT_LC
        ACDNT_LC_CTPRVN
        ACDNT_LC_SIGNGU
        ACDNT_CN
        ACDNT_CAUSE
        ACDNT_TY
        FIRST_ACDNT_CHMCLS
        SECON_ACDNT_CHMCLS
        THRD_ACDNT_CHMCLS
        DPRS_CO
        INJPSN_CO
        ENVRN_DMGE_CN
        PRPRTY_DMGE_AM
     */
    private String acdntCaseSn; // 사고사례 순번
    private String acdntDe; // 사고일자
    private String fgtRceptTime; // 소방접수시간
    private String acdntEntrpsNm; // 사고업체명
    private String acdntLc; // 사고위치
    private String acdntLcCtprvn; // 사고장소 소재 시도
    private String acdntLcSigngu; // 사고장소 소재 시군구
    private String acdntCn; // 사고내용
    private String acdntCause; // 사고원인
    private String acdntTy; // 사고유형
    private String firstAcdntChmcls; // 제1사고 화학물질
    private String seconAcdntChmcls; // 제2사고 화학물질
    private String thrdAcdntChmcls; // 제3사고 화학물질
    private String dprsCo; // 사망자수
    private String injpsnCo; // 부상자수
    private String envrnDmgeCn; // 환경피해내용
    private String prprtyDmgeAm; // 재산피해금액
}
