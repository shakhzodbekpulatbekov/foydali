package pdp_g9.telegram_bot.excel;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
                HSSFSheet xssfSheet =xssfWorkbook.createSheet("Users");
                HSSFRow row=xssfSheet.createRow(0);
                row.createCell(0).setCellValue("name");
                row.createCell(1).setCellValue("adminState");
                row.createCell(2).setCellValue("chatId");
                row.createCell(3).setCellValue("nickName");
                row.createCell(4).setCellValue("userRole");

                for (int i = 0; i < users.size(); i++) {
                    row=xssfSheet.createRow(i+1);
                    row.createCell(0).setCellValue(users.get(i).getName());
                    row.createCell(1).setCellValue(users.get(i).getAdminState());
                    row.createCell(2).setCellValue(users.get(i).getChadId());
                    row.createCell(3).setCellValue(users.get(i).getNickName());
                    row.createCell(4).setCellValue(users.get(i).getUserRole());
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
