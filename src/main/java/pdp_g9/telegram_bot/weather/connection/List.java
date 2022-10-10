package pdp_g9.telegram_bot.weather.connection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class List {
    public int dt;
    public Main main;
    public java.util.List<Weather> weather;
    public Clouds clouds;
    public Wind wind;
    public int visibility;
    public int pop;
    public Sys sys;
    public String dt_txt;

}
