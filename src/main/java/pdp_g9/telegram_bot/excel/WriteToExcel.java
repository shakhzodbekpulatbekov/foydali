package pdp_g9.telegram_bot.excel;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import pdp_g9.telegram_bot.entity.user.UserDataBase;
import pdp_g9.telegram_bot.repository.user.UserRepository;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class WriteToExcel {
   final UserRepository userRepository;

    public WriteToExcel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    

    public void writeToFile(){
        List<UserDataBase> users = userRepository.findAll();

        {
            try(FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/UsersList.xls")) {
                HSSFWorkbook xssfWorkbook = new HSSFWorkbook();

                CellStyle style=xssfWorkbook.createCellStyle();
                style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                style.setBorderBottom(BorderStyle.THIN);
                style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                style.setBorderTop(BorderStyle.THIN);
                style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                style.setBorderLeft(BorderStyle.THIN);
                style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                style.setBorderRight(BorderStyle.THIN);
                style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                Font font = xssfWorkbook.createFont();
                font.setBold(true);
                font.setUnderline(Font.U_SINGLE);
                font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
                style.setFont(font);


                HSSFSheet xssfSheet =xssfWorkbook.createSheet("Users");
                HSSFRow row=xssfSheet.createRow(0);
                HSSFCell cell=row.createCell(0);
                cell.setCellValue("Ism");
                cell.setCellStyle(style);
//                row.createCell(0).setCellValue("name");
                cell=row.createCell(1);
                cell.setCellValue("adminState");
                cell.setCellStyle(style);

                cell=row.createCell(2);
                cell.setCellValue("chatId");
                cell.setCellStyle(style);


                cell=row.createCell(3);
                cell.setCellValue("nickName");
                cell.setCellStyle(style);

                cell=row.createCell(4);
                cell.setCellValue("userRole");
                cell.setCellStyle(style);


                for (int i = 0; i < users.size(); i++) {
                    row=xssfSheet.createRow(i+1);
                    cell=row.createCell(0);
                    cell.setCellValue(users.get(i).getName());
                    cell.setCellStyle(style);

                    cell=row.createCell(1);
                    cell.setCellValue(users.get(i).getAdminState());
                    cell.setCellStyle(style);

                    cell=row.createCell(2);
                    cell.setCellValue(users.get(i).getChadId());
                    cell.setCellStyle(style);

                    cell=row.createCell(3);
                    cell.setCellValue(users.get(i).getNickName());
                    cell.setCellStyle(style);

                    cell=row.createCell(4);
                    cell.setCellValue(users.get(i).getUserRole());
                    cell.setCellStyle(style);
                }
                xssfWorkbook.write(fileOutputStream);
                xssfWorkbook.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
