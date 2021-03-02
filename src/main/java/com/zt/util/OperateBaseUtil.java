package com.zt.util;

import com.zt.dao.DeptMapper;
import com.zt.dao.UserMapper;
import com.zt.entity.common.DeptInfo;
import com.zt.entity.common.ProblemInfo;
import com.zt.entity.common.UserInfo;
import com.zt.entity.common.UserPhone;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OperateBaseUtil {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DeptMapper deptMapper;

    // import data
    @Test
    public void test() {
        //excel文件路径 报表
        String excelPath = "E:\\诺雅克通讯录.xlsx";

            //String encoding = "GBK";
            try {
            File excel = new File(excelPath);
            if (excel.isFile() && excel.exists()) {   //判断文件是否存在

                String[] split = excel.getName().split("\\.");  //.是特殊字符，需要转义！！！！！
                Workbook wb;
                //根据文件后缀（xls/xlsx）进行判断
                if ( "xls".equals(split[1])){
                    FileInputStream fis = new FileInputStream(excel);   //文件流对象
                    wb = new HSSFWorkbook(fis);
                }else if ("xlsx".equals(split[1])){
                    wb = new XSSFWorkbook(excel);
                }else {
                    System.out.println("文件类型错误!");
                    return;
                }

                //开始解析
                Sheet sheet = wb.getSheetAt(0);     //读取sheet 0

                int firstRowIndex = sheet.getFirstRowNum()+1;   //第一行是列名，所以不读
                int lastRowIndex = sheet.getLastRowNum();
                System.out.println("firstRowIndex: "+firstRowIndex);
                System.out.println("lastRowIndex: "+lastRowIndex);

                for(int rIndex = firstRowIndex; rIndex < lastRowIndex; rIndex++) {   //遍历行
                    System.out.println("rIndex: " + rIndex);
                    Row row = sheet.getRow(rIndex);
                    if (row != null) {
                        int firstCellIndex = row.getFirstCellNum();
                        int lastCellIndex = row.getLastCellNum();
                        List<String> equipmentList = new ArrayList<>();
                        UserPhone userPhone = new UserPhone();
                        for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) {   //遍历列
                            Cell cell = row.getCell(cIndex);
                            String cellValue = "";
                            // 如果列为数字避免使用科学计数法
                            if (cell != null && cell.toString() != null && !cell.toString().equals("")) {
                                if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                                    DecimalFormat df = new DecimalFormat("0");
                                    cellValue = df.format(cell.getNumericCellValue());
                                    equipmentList.add(cellValue);
                                } else {
                                    System.out.print(cell.toString() + ",");
                                    equipmentList.add(cell.toString());
                                }
                            } else {
                                equipmentList.add(null);
                            }


                        }
                        boolean flag = false;

                        if (equipmentList.get(0) != null) {
                            List<UserInfo> userPhoneByName = userMapper.getUserPhoneByName(equipmentList.get(0));
                            if (null != userPhoneByName && 0 != userPhoneByName.size()) {
                                userPhone.setOpenId(userPhoneByName.get(0).getOpenId());
                            }
                            List<UserPhone> phoneInfoByName = userMapper.getPhoneInfoByName(equipmentList.get(0));
                            if (null != phoneInfoByName && 0 != phoneInfoByName.size()) {
                                flag = true;
                            }
                        }
                        userPhone.setUserName(equipmentList.get(0));
                        if (equipmentList.get(2) != null) {
                            if (rIndex == 1) {
                                userPhone.setMobilePhone(equipmentList.get(2).replace("86-", "").trim());
                            } else {
                                userPhone.setMobilePhone(equipmentList.get(2).replace("+86-", "").trim());
                            }

                        }
                        if (equipmentList.get(3) != null) {
                            userPhone.setEmail(equipmentList.get(3));
                        }
                        // update equipment
                        System.out.println("===========================userPhone: " + userPhone);
                       if (flag) {
                           // if exist then update
                           try{
                               userMapper.updatePhoneByName(userPhone);
                               System.err.println("========================================= 更新成功 ：" + userPhone);
                           } catch (Exception ex) {
                               System.err.println("========================================= 更新失败 ：" + userPhone +", caused:" + ex);
                           }
                       } else {
                           // if not exist then add
                           try{
                                userMapper.addUserPhone(userPhone);
                                System.err.println("========================================= 添加成功 ：" + userPhone);
                            } catch (Exception ex) {
                                System.err.println("========================================= 添加失败 ：" + userPhone +", caused:" + ex);
                            }
                       }

                    }
                    // 换行
                    System.out.println();
                }
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // export data
    @Test
    public void test01() {
        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();

        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet("sheet1");

        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
        HSSFRow row = sheet.createRow(0);

        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        //style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

        //声明列对象
        HSSFCell cell = null;
        String[] title = {"部门"};
        List<DeptInfo> values = new ArrayList<>();
        List<DeptInfo> allDept = deptMapper.getAllDept(0);
        if (null != allDept && allDept.size() != 0) {
            for (DeptInfo info : allDept) {
                if (info.getParentId() != -1) {
                    List<DeptInfo> allDept1 = deptMapper.getAllDept(info.getParentId());
                    if (null != allDept1 && allDept1.size() != 0) {
                        DeptInfo dept = new DeptInfo();
                        dept.setDeptId(info.getDeptId());
                        dept.setDeptName(allDept1.get(0).getDeptName() + "-" + info.getDeptName());
                        System.out.println("-----------------------dept: " + dept);
                        values.add(dept);
                    }
                }
            }
        }

        //创建标题
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }

        //创建内容
        for (int i = 0; i < values.size(); i++) {
            row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(values.get(i).getDeptId());
            row.createCell(1).setCellValue(values.get(i).getDeptName());
        }
        File file = new File("E:\\phone2.xls");
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

}
