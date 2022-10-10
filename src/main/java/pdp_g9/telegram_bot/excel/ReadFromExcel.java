package pdp_g9.telegram_bot.excel;

import net.sf.jxls.reader.XLSReadStatus;
import net.sf.jxls.reader.XLSReader;
import net.sf.jxls.reader.XLSSheetReader;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface ReadFromExcel {
    public static class readUsersFromExcel {
        XLSReader xlsReader = new XLSReader() {
            @Override
            public XLSReadStatus read(InputStream inputStream, Map map) throws IOException, InvalidFormatException {
                return null;
            }

            @Override
            public void setSheetReaders(Map map) {

            }

            @Override
            public Map getSheetReaders() {
                return null;
            }

            @Override
            public void addSheetReader(String s, XLSSheetReader xlsSheetReader) {

            }

            @Override
            public void addSheetReader(XLSSheetReader xlsSheetReader) {

            }

            @Override
            public void addSheetReader(Integer integer, XLSSheetReader xlsSheetReader) {

            }
        };
    }
}
