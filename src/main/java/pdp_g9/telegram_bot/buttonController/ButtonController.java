package pdp_g9.telegram_bot.buttonController;

import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ButtonController {
    public InlineKeyboardMarkup afterCategory(int id){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> list= new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(list);
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();


        inlineKeyboardButton.setText("Kategoriya qo'shish");
        inlineKeyboardButton.setCallbackData("ADDCATEGORY"+ "-"+id);
        inlineKeyboardButtons.add(inlineKeyboardButton);

        inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Kategoriyani taxrirlash");
        inlineKeyboardButton.setCallbackData("EDITCATEGORY"+ "-"+id);
        inlineKeyboardButtons.add(inlineKeyboardButton);
        list.add(inlineKeyboardButtons);
        inlineKeyboardButtons=new ArrayList<>();

        inlineKeyboardButton=new InlineKeyboardButton();
        inlineKeyboardButton.setText("Kategoriyani o'chirish");
        inlineKeyboardButton.setCallbackData("DELETECATEGORY"+ "-"+id);
        inlineKeyboardButtons.add(inlineKeyboardButton);

        inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Maxsulot qo'shish");
        inlineKeyboardButton.setCallbackData("ADDMEAL"+ "-"+id);
        inlineKeyboardButtons.add(inlineKeyboardButton);
        list.add(inlineKeyboardButtons);

        return inlineKeyboardMarkup;

    }

    public ReplyKeyboardMarkup baseButtons(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton("Kategoriya"));
        keyboardRow.add(new KeyboardButton("User"));
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton(("Xabar yuborish")));
//        keyboardRow1.add(new KeyboardButton(("Bugungi namoz vaqti")));

        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton("User  lar excel faylini olish"));
//        keyboardRow2.add(new KeyboardButton("Ob-havo ma'lumotlari"));
        keyboardRow2.add(new KeyboardButton("Price yuborish!"));
        KeyboardRow keyboardRow3 = new KeyboardRow();
        keyboardRow3.add(new KeyboardButton("Price olish \uD83D\uDCD5"));
        keyboardRow3.add(new KeyboardButton("Location \uD83D\uDCCD"));

        KeyboardRow keyboardRow4 = new KeyboardRow();
        keyboardRow4.add(new KeyboardButton("Dillerga price yuborish \uD83D\uDCB5"));



        keyboardRows.add(keyboardRow);
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        keyboardRows.add(keyboardRow3);
        keyboardRows.add(keyboardRow4);
        replyKeyboardMarkup.setKeyboard(keyboardRows);

        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup weatherButtons(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows =new ArrayList<>();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(keyboardRows);

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton("Bugungi ob-havo"));
        keyboardRow.add(new KeyboardButton("3 kunlik ob-havo"));
        keyboardRows.add(keyboardRow);

        KeyboardRow keyboardRow1 = new KeyboardRow();
//        keyboardRow1.add(new KeyboardButton("5 kunlik ob-havo"));
        keyboardRows.add(keyboardRow1);

        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton("Back ↩"));
        keyboardRows.add(keyboardRow2);

        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup addressLocation(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows =new ArrayList<>();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(keyboardRows);

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton("Do'kon"));
        keyboardRow.add(new KeyboardButton("Biz haqimizda"));
        keyboardRows.add(keyboardRow);

        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup languages(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows =new ArrayList<>();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(keyboardRows);

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton("O'zbek tili \uD83C\uDDF8\uD83C\uDDF1"));
        keyboardRow.add(new KeyboardButton("Русский язык \uD83C\uDDF7\uD83C\uDDFA"));
        keyboardRows.add(keyboardRow);

        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup location(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows =new ArrayList<>();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setKeyboard(keyboardRows);

        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardButton keyboardButton=new KeyboardButton();
        keyboardButton.setText("Location");
        keyboardButton.setRequestLocation(true);
        keyboardRow.add(keyboardButton);

        keyboardRows.add(keyboardRow);

        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getUserContact(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows =new ArrayList<>();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setKeyboard(keyboardRows);

        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardButton keyboardButton=new KeyboardButton();
        keyboardButton.setText("Contact");
        keyboardButton.setRequestContact(true);
        keyboardRow.add(keyboardButton);

        keyboardRows.add(keyboardRow);

        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup MainMenu(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows =new ArrayList<>();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(keyboardRows);

        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardButton keyboardButton=new KeyboardButton();
        keyboardButton.setText("Menu");
        keyboardRow.add(keyboardButton);

        keyboardRows.add(keyboardRow);

        return replyKeyboardMarkup;
    }

    public InlineKeyboardMarkup diller(Long chatId, String phoneNumber){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> list= new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(list);
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();


        inlineKeyboardButton.setText(phoneNumber+" diller sifatida foydalanmoqchi");
        inlineKeyboardButton.setCallbackData("addDiller"+"-"+chatId+"-"+phoneNumber);
        inlineKeyboardButtons.add(inlineKeyboardButton);

        list.add(inlineKeyboardButtons);

        return inlineKeyboardMarkup;

    }

}
