package pdp_g9.telegram_bot.namazTime;

public class NamazToString {
    public String namazTime(Root root){
        String times="";
        times="Sana: "+root.data.date.gregorian.date+"\nToshkent vaqti bilan:\n\n\n";
        times+="1️⃣ Bomdod kirish vaqti: "+root.data.timings.fajr+"\n\n";
        times+="\uD83C\uDF1E Quyosh chiqish vaqti: "+root.data.timings.sunrise+"\n\n";
        times+="2️⃣ Peshin kirish vaqti: "+root.data.timings.dhuhr+"\n\n";
        times+="3️⃣ Asr kirish vaqti: "+root.data.timings.asr+"\n\n";
        times+="4️⃣ Shom kirish vaqti: "+root.data.timings.maghrib+ " (+5min)\n\n";
        times+="5️⃣ Xufton kirish vaqti: "+root.data.timings.isha+"\n\n";
        times+="\uD83C\uDF19 Yarim tun: "+root.data.timings.midnight+"\n\n";
        times+="\uD83C\uDF12️Tahajjud chiqish vaqti: "+root.data.timings.imsak;


        return times;
    }
}
