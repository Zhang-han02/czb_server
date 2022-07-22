package me.zhengjie.modules.czb.archive.domain;

import lombok.Data;

import java.util.List;

/**
 * Erp获取数据后的映射通用对象
 */
@Data
public class ErpEntity {
    private List<ErpEntity> ErpEntityList;
    private Integer  Sonnum;
    private String ParId;
    private String TypeID;
    private Integer leveal;
    private String FullName;
    private String Name;
    private String UserCode;
    private String Person;
    private String taxnumber;
    private Integer isclient;
    private String SettleBtypeId;
    private String Tel;
    private String Standard;
    private String Type;
    private String Unit1;
    private String Unit2;
    private String Unit3;
    private String Comment1;
    private String Comment2;
    private String Comment3;
    private String Comment4;
    private String Comment5;
    private String Comment6;
    private String Comment7;
    private String Comment8;

    /**
     private Integer bblockno;
     private Integer  UnitRate2;
     private Integer  UnitRate1;
     private Integer safedaycount;
     private Integer takeout;
     private String c2usercode;
     private String brfullname;
     private String c4usercode;
     private String kusercode;
     private Integer refprice;
     private String c3fullname;
     private String wfullname;
     private Integer  Sonnum;
     private String c1fullname;
     private Integer RowNum;
     private Integer bcustom3;
     private Integer bcustom4;
     private String Area;
     private String brusercode;
     private Integer safeqty;
     private String unitother;
     private Integer ConsignBeforeDay;
     private Integer beforedate;
     private String c1usercode;
     private Integer producebeforeday;
     private String c3usercode;
     private Integer costmode;
     private Integer costprice;
     private String busercode;
     private Integer bcustom1;
     private Integer bcustom2;
     private Integer definunit;
     private Integer defoutunit;
     private String UnitFz;
     private Integer manserialnum;
     private String NamePY;
     private Integer Deleted;
     private String wusercode;
     private String gpfullname;
     private String Comment;
     private String Source2;
     private String Source1;
     private String c4fullname;
     private String bfullname;
     private String c2fullname;
     private Integer istaxprice;
     private Integer Rec;
     private Integer bconsign;
     private String bpurchase;
     private String kfullname;
     private Integer defpandianunit;
     private Integer bproduce;
     *
     */


}
