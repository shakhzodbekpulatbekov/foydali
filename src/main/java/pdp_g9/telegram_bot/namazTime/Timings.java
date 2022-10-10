package pdp_g9.telegram_bot.namazTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
@lombok.Data
public class Timings{
    @JsonProperty("Fajr")
    public String fajr;
    @JsonProperty("Sunrise")
    public String sunrise;
    @JsonProperty("Dhuhr")
    public String dhuhr;
    @JsonProperty("Asr")
    public String asr;
    @JsonProperty("Sunset")
    public String sunset;
    @JsonProperty("Maghrib")
    public String maghrib;
    @JsonProperty("Isha")
    public String isha;
    @JsonProperty("Imsak")
    public String imsak;
    @JsonProperty("Midnight")
    public String midnight;
}


