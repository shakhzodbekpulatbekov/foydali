package pdp_g9.telegram_bot.namazTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Params {
    @JsonProperty("Fajr")
    public int fajr;
    @JsonProperty("Isha")
    public int isha;
}
