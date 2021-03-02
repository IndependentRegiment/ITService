package com.zt.util;

import com.zt.dao.CardMapper;
import com.zt.dao.DeptMapper;
import com.zt.dao.UserMapper;
import com.zt.entity.common.*;
import com.zt.entity.request.SeeCardRequest;
import com.zt.entity.response.CardListResponse;
import com.zt.entity.response.UserCommentCountResponse;
import com.zt.service.impl.CardServiceImpl;
import org.apache.ibatis.jdbc.Null;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Configuration
public class CreateReportUtil {

    @Autowired
    private CardMapper cardMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private CardServiceImpl cardService;

    public static CreateReportUtil createReportUtil;
    @PostConstruct
    public void init() {
        createReportUtil = this;
    }

    public void createReport(UserCommentCountResponse response) {
        /*HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("sheet1");
        HSSFRow row = sheet.createRow(0);
        HSSFCellStyle style = wb.createCellStyle();*/
        //style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

        String [] title = {"序号", "月份", "满意度占比", "总单数"};

        // export .xlsx
        XSSFWorkbook  wb = new XSSFWorkbook();
        // 创建工作表
        XSSFSheet sheet = wb.createSheet("sheet1");
        XSSFRow row = sheet.createRow(0);

        XSSFCell cell = null;
        // 创建列头标题
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            //cell.setCellStyle(style);
        }

        //创建内容
        if (null != response.getTimeList() && 0 != response.getTimeList().size()) {
            for (int i = 0; i < response.getTimeList().size(); i++) {
                row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(response.getTimeList().get(i));
                row.createCell(2).setCellValue(response.getPartList().get(i));
                row.createCell(3).setCellValue(response.getCountList().get(i));
            }
        }
        File file = new File("C:\\Users\\Administrator\\Desktop\\monthCommentCount.xlsx");
        FileOutputStream fs = null;
        try {
            fs = new FileOutputStream(file);
            wb.write(fs);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fs) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public  void exportCard(CardListResponse response) {

        // export .xls
        /*HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("sheet1");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = null;
        */
        //HSSFCellStyle style = wb.createCellStyle();

        // export .xlsx
        XSSFWorkbook  wb = new XSSFWorkbook();
        // 创建工作表
        XSSFSheet sheet = wb.createSheet("sheet1");
        XSSFRow row = sheet.createRow(0);

        XSSFCell cell = null;
        String [] title = {"序号", "工单号", "申请人", "申请人部门", "申请人电话", "故障描述", "紧急度",
                "故障类型", "协办人", "工单状态", "工单创建时间", "工单完成时间", "处理工程师", "服务方式",
                "故障原因", "故障解决方案", "用户评价", "用户满意度"};


        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            //cell.setCellStyle(style);
        }

        //创建内容
        if (null != response.getCardList() && 0 != response.getCardList().size()) {
            for (int i = 0; i < response.getCardList().size(); i++) {
                row = sheet.createRow(i + 1);
                JobCardDetail card = response.getCardList().get(i);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(Optional.ofNullable(card.getCardId()).orElse(""));
                row.createCell(2).setCellValue(Optional.ofNullable(card.getApplyName()).orElse(""));
                row.createCell(3).setCellValue(Optional.ofNullable(card.getDeptName()).orElse(""));
                row.createCell(4).setCellValue(Optional.ofNullable(card.getMobliePhone()).orElse(""));
                row.createCell(5).setCellValue(Optional.ofNullable(card.getDescription()).orElse(""));
                row.createCell(6).setCellValue(card.getPriority());
                row.createCell(7).setCellValue(Optional.ofNullable(card.getProblemType()).orElse(""));
                row.createCell(8).setCellValue(Optional.ofNullable(card.getAssistName()).orElse(""));
                row.createCell(9).setCellValue(Optional.ofNullable(card.getStatus()).orElse(""));
                row.createCell(10).setCellValue(Optional.ofNullable(card.getCreateTime()).orElse(""));
                row.createCell(11).setCellValue(Optional.ofNullable(card.getEndTime()).orElse(""));
                row.createCell(12).setCellValue(Optional.ofNullable(card.getDeal()).orElse(""));
                row.createCell(13).setCellValue(Optional.ofNullable(card.getDealWay()).orElse(""));
                row.createCell(14).setCellValue(Optional.ofNullable(card.getReason()).orElse(""));
                row.createCell(15).setCellValue(Optional.ofNullable(card.getSolution()).orElse(""));
                row.createCell(16).setCellValue(Optional.ofNullable(card.getComment()).orElse(""));
                row.createCell(17).setCellValue(Optional.ofNullable(card.getSatisfaction()).orElse(""));
            }
        }
        File file = new File("C:\\Users\\Administrator\\Desktop\\card.xlsx");
        FileOutputStream fs = null;
        try {
            fs = new FileOutputStream(file);
            wb.write(fs);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fs) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //@Test
    //@Transactional(rollbackFor = Exception.class)
    public void importHandCard(String filePath, List<String> columns) {
        System.err.println("start import card list info~~~~~~~~~~~~~~~~~~");
        Workbook wb =null;
        Sheet sheet = null;
        Row row = null;
        List<JobCardDetail> list = null;
        String cellData = null;
        filePath = "E:\\cardlist.xlsx";

        wb = readExcel(filePath);

        if (wb != null) {
            //用来存放表中数据
            list = new ArrayList<>();
            //获取第一个sheet
            sheet = wb.getSheetAt(0);
            //获取最大行数
            int rownum = sheet.getPhysicalNumberOfRows();
            System.err.println("==========rowNum: " + rownum);
            //获取第一行
            row = sheet.getRow(0);
            //获取最大列数
            int colnum = row.getPhysicalNumberOfCells();
            for (int i = 1; i < rownum; i++) {
                row = sheet.getRow(i);
                if (row != null) {
                    System.err.println("====================row：" + i);
                    JobCardDetail cardDetail = new JobCardDetail();
                    //System.err.println("priority: " + row.getCell(0));
                    String priority = Optional.ofNullable(getCellFormatValue(row.getCell(0)).toString()).orElse("1");
                    cardDetail.setPriority(Integer.valueOf(priority).intValue());
                    cardDetail.setProblemType(Optional.ofNullable(getCellFormatValue(row.getCell(1))).orElse("") + "-" +
                            Optional.ofNullable(getCellFormatValue(row.getCell(2))).orElse(""));
                    cardDetail.setAppointmentCreate(Optional.ofNullable(getCellFormatValue(row.getCell(3))).orElse("").toString());
                    cardDetail.setAppointmentEnd(Optional.ofNullable(getCellFormatValue(row.getCell(4))).orElse("").toString());
                    cardDetail.setApplyName(Optional.ofNullable(getCellFormatValue(row.getCell(5))).orElse("").toString());
                    cardDetail.setDeptName(Optional.ofNullable(getCellFormatValue(row.getCell(6))).orElse("").toString() + "-" +
                            Optional.ofNullable(getCellFormatValue(row.getCell(7))).orElse("").toString());
                    cardDetail.setMobliePhone(Optional.ofNullable(getCellFormatValue(row.getCell(8))).orElse("").toString());
                    cardDetail.setDeal(Optional.ofNullable(getCellFormatValue(row.getCell(9))).orElse("").toString());
                    cardDetail.setDescription(Optional.ofNullable(getCellFormatValue(row.getCell(10))).orElse("").toString());
                    cardDetail.setReason(Optional.ofNullable(getCellFormatValue(row.getCell(10))).orElse("").toString());
                    cardDetail.setSolution(Optional.ofNullable(getCellFormatValue(row.getCell(11))).orElse("").toString());
                    cardDetail.setAssistName(Optional.ofNullable(getCellFormatValue(row.getCell(12))).orElse("").toString());
                    cardDetail.setCreateTime(Optional.ofNullable(getCellValue(row.getCell(13))).orElse("").toString());
                    cardDetail.setEndTime(Optional.ofNullable(getCellValue(row.getCell(14))).orElse("").toString());
                    cardDetail.setDealWay(Optional.ofNullable(getCellFormatValue(row.getCell(15))).orElse("").toString());
                    list.add(cardDetail);
                    System.err.println("row: " + i + "，date:" + cardDetail);
                } else {
                    break;
                }
            }
            // add card to dataBase
            for (JobCardDetail cardDetail : list) {
                JobCard card = new JobCard();
                card.setCardId(createReportUtil.cardService.getCardId());
                List<UserInfo> userByName = createReportUtil.userMapper.getUserByName(cardDetail.getApplyName());
                if (null != userByName && 0 != userByName.size()) {
                    card.setApplyId(userByName.get(0).getOpenId());
                }
                card.setApplyName(cardDetail.getApplyName());
                card.setPriority(cardDetail.getPriority());
                if (null != cardDetail.getProblemType()) {
                    String[] split = cardDetail.getProblemType().split("-");
                    List<ProblemInfo> problem1 = createReportUtil.cardMapper.getProblemInfoByAll(new ProblemInfo(split[0]));
                    List<ProblemInfo> problem2 = createReportUtil.cardMapper.getProblemInfoByAll(new ProblemInfo(split[1]));
                    if (null != problem2 && 0 != problem2.size()) {
                        card.setProblemType(problem2.get(0).getTypeId());
                        if (null != problem1 && problem1.size() != 0) {
                            ProblemInfo problemInfo = problem1.get(0);
                            for (ProblemInfo problemInfo1 : problem2) {
                                if (problemInfo1.getParentId().equals(problemInfo.getTypeId())) {
                                    card.setProblemType(problemInfo1.getTypeId());
                                }
                            }
                        }
                    } else if (null != problem1 && 0 != problem1.size()) {
                        card.setProblemType(problem1.get(0).getTypeId());
                    }
                } else {
                    card.setProblemType(29);
                }

                if (null != cardDetail.getApplyName()) {
                    List<UserInfo> assistName = createReportUtil.userMapper.getUserByName(cardDetail.getAssistName());
                    if (null != assistName && 0 != assistName.size()) {
                        card.setAssistId(Optional.ofNullable(assistName.get(0).getOpenId()).orElse(""));
                    }
                }

                card.setStatusId(4);
                card.setReason(Optional.ofNullable(cardDetail.getReason()).orElse(""));
                card.setSolution(Optional.ofNullable(cardDetail.getSolution()).orElse(""));
                card.setCreateTime(Optional.ofNullable(cardDetail.getCreateTime().replace("=", "-")).orElse(""));
                card.setEndTime(Optional.ofNullable(cardDetail.getEndTime().replace("=", "-")).orElse(""));
                card.setProcessNode(4);
                card.setDistribution("oAh3O4kQm93o0iYBWSWpVdlCMgVU");

                List<UserInfo> dealName = createReportUtil.userMapper.getUserByName(cardDetail.getDeal());
                if (null != dealName && 0 != dealName.size()) {
                    card.setDeal(dealName.get(0).getOpenId());
                }
                card.setDescription(Optional.ofNullable(cardDetail.getDescription()).orElse(""));

                if (null != cardDetail.getDeptName()) {
                    System.err.println("===================dept: " + cardDetail.getDeptName());
                    String[] split1 = cardDetail.getDeptName().split("-");
                    if (null != split1 && 0 != split1.length) {
                        if (split1.length < 2) {
                            List<DeptInfo> dept1 = createReportUtil.deptMapper.getDeptByName(split1[0]);
                            card.setDeptId(dept1.get(0).getDeptId());
                        } else {
                            List<DeptInfo> dept1 = createReportUtil.deptMapper.getDeptByName(split1[0].trim());
                            List<DeptInfo> dept2 = createReportUtil.deptMapper.getDeptByName(split1[1].trim());
                            if (null != dept2 && 0 != dept2.size()) {
                                card.setDeptId(dept2.get(0).getDeptId());
                                if (null != dept1 && 0 != dept1.size()) {
                                    DeptInfo deptInfo = dept1.get(0);
                                    for (DeptInfo deptInfo1 : dept2) {
                                        if (deptInfo.getDeptId() == deptInfo1.getParentId().intValue()) {
                                            card.setDeptId(deptInfo1.getDeptId());
                                            break;
                                        }
                                    }
                                }
                            } else if (null != dept1 && 0 != dept1.size()) {
                                card.setDeptId(dept1.get(0).getDeptId());
                            }
                        }
                    }
                } else {
                    card.setDeptId(5);
                }

                switch (cardDetail.getDealWay()) {
                    case "远程服务":
                        card.setDealWay(1);
                        break;
                    case "现场服务":
                        card.setDealWay(2);
                        break;
                    default:
                        card.setDealWay(1);
                        break;
                }
                if (null != cardDetail.getAppointmentCreate() && !cardDetail.getAppointmentCreate().equals("")) {
                    String appointmentCreate = cardDetail.getAppointmentCreate().replace("-", ":");
                    appointmentCreate = card.getCreateTime().substring(0, card.getCreateTime().indexOf(" ")) + " " + appointmentCreate;
                    card.setAppointmentCreate(com.zt.util.DateUtil.getTimeStampByStr(appointmentCreate));
                }
                if (null != cardDetail.getAppointmentEnd() && !cardDetail.getAppointmentEnd().equals("")) {
                    String appointmentEnd = cardDetail.getAppointmentEnd().replace("-", ":");
                    appointmentEnd = card.getEndTime().substring(0, card.getEndTime().indexOf(" ")) + " " + appointmentEnd;
                    card.setAppointmentEnd(com.zt.util.DateUtil.getTimeStampByStr(appointmentEnd));
                }

                System.err.println("==========real card: " + card);
                // if is exist
                SeeCardRequest request = new SeeCardRequest();
                request.setOpenId(card.getApplyId());
                request.setApplyName(card.getApplyName());
                request.setCreateTime(card.getCreateTime());
                request.setEndTime(card.getEndTime());
                request.setProblemType(card.getProblemType());
                /*List<JobCard> allCard = createReportUtil.cardMapper.getAllCard(request);
                if (allCard != null && 0 != allCard.size()) {
                    System.err.println("==================工单已存在：" + card);
                    continue;
                }
                try {
                    createReportUtil.cardMapper.createCard(card);
                    System.out.println("-----------------------创建工单成功------------------------");
                    // 发送邮件
                } catch (Exception ex) {
                    System.out.println("创建工单失败: " + card + ",cause：" + ex);
                }*/
            }
        }
    }

    public static Workbook readExcel(String filePath){
        Workbook wb = null;
        if(filePath==null){
            return null;
        }
        String extString = filePath.substring(filePath.lastIndexOf("."));
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            if(".xls".equals(extString)){
                return wb = new HSSFWorkbook(is);
            }else if(".xlsx".equals(extString)){
                return wb = new XSSFWorkbook(is);
            }else{
                return wb = null;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }

    public static Object getCellFormatValue(Cell cell){
        if (cell != null && cell.toString() != null && !cell.toString().equals("")) {
            CellType cellType = cell.getCellTypeEnum();
            switch (cellType) {
                case NUMERIC:
                    Object value;
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        value = cell.getDateCellValue();
                        DateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date setupTime = null;
                        try {
                            setupTime = HSSFDateUtil.getJavaDate(Double.valueOf(value.toString()));
                        } catch (Exception ex) {

                        }


                        value = formater.format(setupTime);
                    } else {
                        double dValue = cell.getNumericCellValue();
                        DecimalFormat df = new DecimalFormat("0");
                        value = df.format(dValue);
                    }
                    return value;
                case STRING:
                    return cell.getStringCellValue();
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    return cell.getCellFormula();
                case BLANK:
                    return "";
                case ERROR:
                    return String.valueOf(cell.getErrorCellValue());
                default:
                    return "";
            }
        } else {
            return "";
        }
    }

    private static Object getCellValue(Cell cell) {
        if (cell != null && cell.toString() != null && !cell.toString().equals("")) {
            System.err.println("===============double: " + cell);
            Object value = cell.getDateCellValue();
            // Date setupTime = HSSFDateUtil.getJavaDate(Double.valueOf(value.toString()));
            DateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            value = formater.format(value);
            return value;
        } else {
            return "";
        }
    }
}
