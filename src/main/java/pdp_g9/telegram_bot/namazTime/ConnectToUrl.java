package pdp_g9.telegram_bot.namazTime;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ConnectToUrl {

    public Root namazTime() throws MalformedURLException {
        ObjectMapper objectMapper = new ObjectMapper();
        URL url = new URL("https://api.aladhan.com/timingsByAddress/time?address=Tashkent,Uzbekistan&method=2&school=1");
        Root root =new Root();

        {
            try {
                root = objectMapper.readValue(url, Root.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }return root;
    }

}
