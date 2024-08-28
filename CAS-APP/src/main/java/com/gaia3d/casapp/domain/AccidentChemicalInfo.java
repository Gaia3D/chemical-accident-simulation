package com.gaia3d.casapp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 사고대비물질정보
 */
@Getter
@Setter
@Data
@AllArgsConstructor
public class AccidentChemicalInfo {
    /*
        chemical_no
        chemical_nm
        chemical_eng_nm
        cas_no
        aegl1_conc
        aegl2_conc
        aegl3_conc
        acu_rfc_ref
        acu_ref_unit
        chronic_rfc
        con_chronic_rfc
        chronic_rfc_ref
        chronic_ur
        con_chronic_ur
        ur_ref
        chronic_rfd
        chronic_rfd_ref
        chronic_csf
        csf_ref
        item_content
        ins_id
        ins_dttm
        upd_id
        upd_dttm
     */
    private String chemicalNo; // 화학물질 번호
    private String chemicalNm; // 화학물질 명
    private String chemicalEngNm; // 화학물질 영문명
    private String casNo; // CAS NO
    private double aegl1Conc; // AEGl1 노출농도
    private double aegl2Conc; // AEGl2 노출농도
    private double aegl3Conc; // AEGl3 노출농도
    private String acuRfcRef; // 급성 노출 시 위험성 참고
    private String acuRefUnit; // 급성 노출 시 위험성 단위
    private String chronicRfc; // 만성 노출 시 위험성
    private String conChronicRfc; // 만성 노출 시 위험성 내용
    private String chronicRfcRef; // 만성 노출 시 위험성 참고
    private String chronicUr; // 만성 노출 시 위험성
    private String conChronicUr; // 만성 노출 시 위험성 내용
    private String urRef; // 만성 노출 시 위험성 참고
    private String chronicRfd; // 만성 노출 시 위험성
    private String chronicRfdRef; // 만성 노출 시 위험성 참고
    private String chronicCsf; // 만성 노출 시 위험성
    private String csfRef; // 만성 노출 시 위험성 참고
    private String itemContent; // 항목 내용
    private String insId; // 등록자
    private String insDttm; // 등록일시
    private String updId; // 수정자
    private String updDttm; // 수정일시
}
