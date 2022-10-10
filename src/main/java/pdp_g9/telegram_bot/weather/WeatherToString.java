package pdp_g9.telegram_bot.weather;


import lombok.Data;
import pdp_g9.telegram_bot.weather.connection.Root;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Data
public class WeatherToString {
    public String weather(Root root, int num) {
        String str = "";

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Tashkent"));
        String strDate = formatter.format(date);

        int length = root.list.size();
        int helper = 0;
        boolean day = true;
        boolean oneDay = true;
        if (num == 1) {
            str += root.city.name + "\n\n";
            if (length == 1) {
                str += "Sana: " + strDate + "\n";
            } else
                str += root.list.get(0).dt_txt + "\n";


            str += "Xozirgi xarorat: " + (int) Math.round((root.list.get(0).main.temp)) + " C \uD83C\uDF21" + "\n";
            for (int j = 0; j < length && oneDay; j++) {
                if (root.list.get(j).dt_txt.contains("03:00:00")) {
                    oneDay = false;
                    str += "Kechasi xarorat: " + (int) Math.round((root.list.get(j).main.temp)-4) + " C \uD83C\uDF21" + "\n";
                }
            }

            str += "Namlik: " + root.list.get(0).main.humidity + " \uD83D\uDCA7" + "\n";


            String icon = root.list.get(0).weather.get(0).icon;
            if (icon.contains("01")) {
                str += "Kun ob-havosi: Quyoshli ☀️\n\n\n";
            }
            if (icon.contains("02")) {
                str += "Kun ob-havosi: Ozroq bulutli ⛅️️\n\n\n";
            }
            if (icon.contains("03")) {
                str += "Kun ob-havosi: Bulutli  ☁️️\n\n\n";
            }
            if (icon.contains("04")) {
                str += "Kun ob-havosi: Bulutli  ☁️️\n\n\n";
            }
            if (icon.contains("09")) {
                str += "Kun ob-havosi: Yomgirli \uD83C\uDF27️\n\n\n";
            }
            if (icon.contains("10")) {
                str += "Kun ob-havosi: Yomgirli  \uD83C\uDF26️\n\n\n";
            }
            if (icon.contains("11")) {
                str += "Kun ob-havosi: Momaqaldiroqli \uD83C\uDF29  \uD83C\uDF26️\n\n\n";
            }
            if (icon.contains("13")) {
                str += "Kun ob-havosi: Qor ❄️  \uD83C\uDF26️\n\n\n";
            }
            if (icon.contains("50")) {
                str += "Kun ob-havosi: Tuman \uD83C\uDF2B  \uD83C\uDF26️\n\n\n";
            }
            str += "---------------------\n\n";
        }
        for (int i = 0; i < length && day && num > 1; i++) {
            String dt_txt = root.list.get(i).dt_txt;

            if (dt_txt.contains("12:00:00")) {
                helper++;
                if (helper == num) {
                    day = false;
                }
                str += root.city.name + "\n\n";
                if (length == 1) {
                    str += "Sana: " + strDate + "\n";
                } else
                    str += dt_txt + "\n";


                str += "Kunduzi xarorat: " + (int) Math.round((root.list.get(i).main.temp)) + " C \uD83C\uDF21" + "\n";
                if (root.list.get(i + 5).dt_txt.contains("03:00:00")) {
                    str += "Kechasi xarorat: " + (int) Math.round((root.list.get(i + 5).main.temp)-5) + " C \uD83C\uDF21" + "\n";
                }


                str += "Namlik: " + root.list.get(i).main.humidity + " \uD83D\uDCA7" + "\n";


                String icon = root.list.get(i).weather.get(0).icon;
                if (icon.contains("01")) {
                    str += "Kun ob-havosi: Quyoshli ☀️\n\n\n";
                }
                if (icon.contains("02")) {
                    str += "Kun ob-havosi: Ozroq bulutli ⛅️️\n\n\n";
                }
                if (icon.contains("03")) {
                    str += "Kun ob-havosi: Bulutli  ☁️️\n\n\n";
                }
                if (icon.contains("04")) {
                    str += "Kun ob-havosi: Bulutli  ☁️️\n\n\n";
                }
                if (icon.contains("09")) {
                    str += "Kun ob-havosi: Yomgirli \uD83C\uDF27️\n\n\n";
                }
                if (icon.contains("10")) {
                    str += "Kun ob-havosi: Yomgirli  \uD83C\uDF26️\n\n\n";
                }
                if (icon.contains("11")) {
                    str += "Kun ob-havosi: Momaqaldiroqli \uD83C\uDF29  \uD83C\uDF26️\n\n\n";
                }
                if (icon.contains("13")) {
                    str += "Kun ob-havosi: Qor ❄️  \uD83C\uDF26️\n\n\n";
                }
                if (icon.contains("50")) {
                    str += "Kun ob-havosi: Tuman \uD83C\uDF2B  \uD83C\uDF26️\n\n\n";
                }
                str += "---------------------\n\n";
            }
        }

        return str;
    }
}
