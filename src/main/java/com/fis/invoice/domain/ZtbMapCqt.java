package com.fis.invoice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "ZTB_MAP_CQT")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZtbMapCqt {
  

    @Column(name = "MANDT")
    private Long mandt;

    @Id
    @Column(name = "MA_CQT")
    private String maCqt;
  
    @Column(name = "TEN_CQT_NGAN")
    private String tenCqtNgan;
  
    @Column(name = "MA_CQT_7")
    private String maCqt7;
  
    @Column(name = "TEN_CQT_DAI")
    private String tenCqtDai;
  
    @Column(name = "MA_CHA_4")
    private String maCha4;
  
    @Column(name = "MA_CHA_7")
    private String maCha7;
  
    @Column(name = "MA_QLT")
    private String maQlt;
  
    @Column(name = "MA_KHO_BAC_NC")
    private String maKhoBacNc;
  
    @Column(name = "TEN_KB")
    private String tenKb;
  
    @Column(name = "MA_TINH")
    private String maTinh;
  
    @Column(name = "HL_TU")
    private String hlTu;
  
    @Column(name = "HL_DEN")
    private Long hlDen;
  
    @Column(name = "VPFLG")
    private String vpflg;
  
    @Column(name = "MA_TINH_OLD")
    private String maTinhOld;
  
    @Column(name = "GET_TK_DF")
    private String getTkDf;
  
    @Column(name = "YN_NPT_DF")
    private String ynNptDf;
  
    @Column(name = "YN_NTK_TMS")
    private String ynNtkTms;
  
    @Column(name = "BUKGR")
    private String bukgr;
  
    @Column(name = "BGRST")
    private Long bgrst;
  
    @Column(name = "BUKLK")
    private String buklk;
  
    @Column(name = "BLKFR")
    private Long blkfr;
  
    @Column(name = "BLKTO")
    private Long blkto;
  
    @Column(name = "BGRNM1")
    private String bgrnm1;
  
    @Column(name = "BGRNM2")
    private String bgrnm2;
  
    @Column(name = "ENGNAME")
    private String engname;
  
    @Column(name = "BGRNM3")
    private String bgrnm3;
}
