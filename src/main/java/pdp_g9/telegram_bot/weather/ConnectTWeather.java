package pdp_g9.telegram_bot.weather;

import com.fasterxml.jackson.databind.ObjectMapper;
import pdp_g9.telegram_bot.weather.connection.Root;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

public class ConnectTWeather {
    public Root weather() throws MalformedURLException {
        ObjectMapper objectMapper = new ObjectMapper();
        URL url = new URL("https://api.openweathermap.org/data/2.5/forecast?q=Tashkent&units=metric&appid=a8d42f86e821b21a91bb3129f2333377");
        Root root = new Root();

//        Date date=new Date();
//        System.out.println("date "+date);
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
//        System.out.println(dayOfWeek);

        {
            try {
                root = objectMapper.readValue(url, Root.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return root;
    }

}
