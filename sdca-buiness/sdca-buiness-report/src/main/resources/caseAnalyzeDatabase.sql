-- 案件分析通用表结构
CREATE TABLE case_analyze  (
  id varchar(20) COMMENT 'id',
  ajbh varchar(50) COMMENT '案件编号',
  ajmc varchar(100) COMMENT '案件名称',
  basj varchar(100) COMMENT '报案时间',
  afsj varchar(100) COMMENT '案发时间',
  lasj varchar(100) COMMENT '立案时间',
  ladw varchar(100) COMMENT '立案单位',
  zbrxm varchar(100) COMMENT '主办人姓名',
  zbrdh varchar(100) COMMENT '主办人电话',
  js varchar(100) COMMENT '角色',
  wll varchar(100) COMMENT '网络流',
  lx varchar(100) COMMENT '号码类型',
  hm varchar(100) COMMENT '号码',
  ajdl varchar(50) COMMENT '案件大类',
  ajxl varchar(50) COMMENT '案件小类',
  jyaq varchar(2000) COMMENT '简要案情',
  apps varchar(1000) COMMENT '提取的apps',
  url varchar(1000) COMMENT '提取的url',
  phone varchar(100) COMMENT '提取的号码',
  case_date varchar(8) COMMENT '案件所属年月'
  
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
