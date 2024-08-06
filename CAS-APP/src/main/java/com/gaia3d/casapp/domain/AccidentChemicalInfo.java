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
        chronic_rfc
        con_chronic_rfc
        aegl1_conc
        aegl2_conc
        aegl3_conc
        cas_no
        item_content
        ins_id
        ins_dttm
        upd_id
        upd_dttm
     */
    private String chemicalNo; // 화학물질 번호
    private String chemicalNm; // 화학물질 명
    private String chemicalEngNm; // 화학물질 영문명
    private int chronicRfc; // 만성 노출 시 위험성
    private int conChronicRfc; // 만성 노출 시 위험성 내용
    private int aegl1Conc; // AEGl1 노출농도
    private int aegl2Conc; // AEGl2 노출농도
    private int aegl3Conc; // AEGl3 노출농도
    private String casNo; // CAS NO
    private String itemContent; // 항목 내용
    private String insId; // 등록자
    private String insDttm; // 등록일시
    private String updId; // 수정자
    private String updDttm; // 수정일시
}
