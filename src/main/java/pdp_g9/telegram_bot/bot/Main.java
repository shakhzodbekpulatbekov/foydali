package pdp_g9.telegram_bot.bot;

import com.google.common.io.ByteSource;
import lombok.SneakyThrows;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pdp_g9.telegram_bot.excel.ReadFromExcel;
import pdp_g9.telegram_bot.weather.ConnectTWeather;
import pdp_g9.telegram_bot.buttonController.ButtonController;
import pdp_g9.telegram_bot.excel.WriteToExcel;
import pdp_g9.telegram_bot.namazTime.ConnectToUrl;
import pdp_g9.telegram_bot.namazTime.NamazToString;
import pdp_g9.telegram_bot.namazTime.Root;
import pdp_g9.telegram_bot.service.meal.MealService;
import pdp_g9.telegram_bot.entity.meal.MealDataBase;
import pdp_g9.telegram_bot.entity.user.UserDataBase;
import pdp_g9.telegram_bot.repository.category.CategoryRepository;
import pdp_g9.telegram_bot.repository.meal.MealRepository;
import pdp_g9.telegram_bot.repository.user.UserRepository;
import pdp_g9.telegram_bot.service.category.CategoryService;
import pdp_g9.telegram_bot.service.user.UserService;
import pdp_g9.telegram_bot.weather.WeatherToString;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Component
public class Main extends TelegramLongPollingBot implements ReadFromExcel {
    private String responseText = "";

    final UserService userService;
    final CategoryRepository categoryRepository;
    final MealRepository mealRepository;
    final MealService mealService;
    final MealDataBase mealDataBase;
    final CategoryService categoryService;
    final UserRepository userRepository;
    final ButtonController buttonController;


    @Autowired
    public Main(UserService userService, CategoryRepository categoryRepository, MealRepository mealRepository, MealService keyboards, MealDataBase mealDataBase, CategoryService categoryService, UserRepository userRepository, ButtonController buttonController) {
        this.userService = userService;
        this.categoryRepository = categoryRepository;
        this.mealRepository = mealRepository;
        this.mealService = keyboards;
        this.mealDataBase = mealDataBase;
        this.categoryService = categoryService;
        this.userRepository = userRepository;
        this.buttonController = buttonController;
    }

    @Override
    public String getBotUsername() {
        return "@Foydali001_bot";
    }

    @Override
    public String getBotToken() {
        return "2085597139:AAFX_ZVuiyqLTvUsL-DiSAS3cLYOTWj9o0U";
    }

    String categoryName = "";
    int parentId = 0;

    int categoryId = 0;
    String mealName = "";
    String description = "";
    String videoUrl = "";
    int count = 0;
    int mealId = 0;
    String addText = "";
    String fileId = "";

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        this.responseText = "Kerakli bo'limni tanlang!";
        SendMessage sendMessage = new  SendMessage();
        if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            sendMessage = new SendMessage();
            String data = update.getCallbackQuery().getData();
            String[] strings = splitData(data);
            String str = strings[0];

            int id = 0;
            int messageId = update.getCallbackQuery().getMessage().getMessageId();
            Integer userRole = userService.getUserRole(chatId);

            if (strings.length > 1) {
                id = Integer.parseInt(strings[1]);
            }
            Long getChatId = 0L;
            int value = 0;
            if (strings.length == 3
            ) {
                value = Integer.parseInt(strings[2]);
            }
            switch (str) {
                case "ADDCATEGORY":
                    sendMessage.setText("Kategoriya nomini kiriting: ");
                    sendMessage.setChatId(String.valueOf(chatId));
                    parentId = id;
                    userService.setAdminStatus(1, chatId);
                    executes2(sendMessage);
                    break;

                case "USRERS":
                    userService.addAdmin(id);
                    sendMessage.setChatId(String.valueOf(chatId));
                    sendMessage.setText("Admin qo'shildi");
                    executes2(sendMessage);
                    break;


                case "ADDMEAL":
                    userService.setAdminStatus(2, chatId);
                    categoryId = id;
                    sendMessage.setText("Ta'om nomini kiriting: ");
                    sendMessage.setChatId(String.valueOf(chatId));
                    executes2(sendMessage);
                    break;

                case "LISTTOUSER":
                    InlineKeyboardMarkup inlineKeyboardMarkup = categoryService.childCategory(id, 0);
                    if (inlineKeyboardMarkup.getKeyboard().isEmpty())
                        inlineKeyboardMarkup = mealService.getMealDataBase(id, 0, userRole);

                    executesEdit(inlineKeyboardMarkup, messageId, chatId);

                    break;

                case "USERMEAL":
                    MealDataBase meal = mealService.getMeal(id);
                    InputStream inputStream = ByteSource.wrap(meal.getPhotoByte()).openStream();
                    SendPhoto messagePhoto = new SendPhoto();
                    InputFile inputFile = new InputFile(inputStream, meal.getPhotoName());
                    messagePhoto.setPhoto(inputFile);
                    messagePhoto.setCaption(meal.getName() + "\n\n" + meal.getDescription() + "\n\n videoni ko'rish uchun  \uD83D\uDC47 \n" + meal.getVideoUrl());
                    messagePhoto.setChatId(String.valueOf(chatId));
                    execute(messagePhoto);
                    InlineKeyboardMarkup inlineKeyboardMarkup5 = mealService.workWithMeal(id, userRole);
                    executes(null, inlineKeyboardMarkup5, chatId, responseText);
                    break;

                case "EDITMEAL":
                    userService.setAdminStatus(6, chatId);
                    sendMessage.setText("Ta'om nomini kiriting");
                    mealId = id;
                    sendMessage.setChatId(String.valueOf(chatId));
                    executes2(sendMessage);
                    break;

                case "BACKFROMMEALTOCATEGORY":
                    deleteMessage(chatId, messageId);
                    deleteMessage(chatId, messageId - 1);
//                       executesEdit( mealService.backToCategory(id,userRole),messageId);
                    break;

                case "BACKTOCATEGORYFROMMEALLIST":
                    int categoryParentId = categoryService.findParentId(id);
                    if (userRole == 1)
                        executesEdit(categoryService.categoryList(0, categoryParentId), messageId, chatId);

                    else
                        executesEdit(categoryService.listToUser(categoryParentId, 0), messageId, chatId);
                    break;

                case "LIST":
                    InlineKeyboardMarkup inlineKeyboardMarkup1 = userService.userList();
                    executes(null, inlineKeyboardMarkup1, chatId, responseText);
                    break;

                case "SEARCHBYNAME":
                    sendMessage.setText("Ism kiriting: ");
                    sendMessage.setChatId(String.valueOf(chatId));
                    executes2(sendMessage);
                    userService.setAdminStatus(3, chatId);
                    break;

                case "LISTOFADMINS":
                    InlineKeyboardMarkup inlineKeyboardMarkup3 = userService.listOfAdmins();
                    executes(null, inlineKeyboardMarkup3, chatId, responseText);
                    break;

                case "DeleteAll":
                    userService.deleteUser();
                    sendMessage.setText("ok");
                    sendMessage.setChatId(String.valueOf(chatId));
                    executes2(sendMessage);
                    break;

                case "ADMINS":
                    InlineKeyboardMarkup inlineKeyboardMarkup4 = userService.workWithAdmin(id);
                    executes(null, inlineKeyboardMarkup4, chatId, responseText);
                    break;

                case "DELETECATEGORY":
                    boolean b1 = categoryService.deleteCategory(id);
                    if (b1) {
                        sendMessage.setText("Kategoriya o'chirildi");
                    } else
                        sendMessage.setText("Kategoriya o'chirishda muammo");

                    sendMessage.setChatId(String.valueOf(chatId));
                    executes2(sendMessage);
                    break;

                case "BACKTOPARENTCATEGORY":
                    executes(null, categoryService.findParentCategory(id), chatId, responseText);
                    deleteMessage(chatId, messageId);
                    try {
                        deleteMessage(chatId, messageId - 1);
                    } catch (Exception e) {

                    }

                    if (userRole == 1) {
                        try {
                            deleteMessage(chatId, messageId + 1);
                        } catch (Exception e) {

                        }
                    }
                    break;

                case "EDITCATEGORY":
                    sendMessage.setText("Kategoriyani yangi nomini kiriting");
                    categoryId = id;
                    sendMessage.setChatId(String.valueOf(chatId));
                    executes2(sendMessage);
                    userService.setAdminStatus(7, chatId);
                    break;

                case "MEALDELETE":
                    mealService.deleteMeal(id);
                    sendMessage.setText("Ta'om o'chirildi");
                    sendMessage.setChatId(String.valueOf(chatId));
                    executes2(sendMessage);
                    break;

                case "REMOVEADMIN":
                    boolean b = userService.removeAdmin(id);
                    if (b) {
                        sendMessage.setText("Admin ro'yxatidan chiqarildi");
                    } else
                        sendMessage.setText("Admin topilmadi yoki u BEK");

                    sendMessage.setChatId(String.valueOf(chatId));
                    executes2(sendMessage);
                    break;

                case "ADDADMINBYNAME":
                    userService.addAdmin(id);
                    sendMessage.setText("Admin qo'shildi");
                    sendMessage.setChatId(String.valueOf(chatId));
                    executes2(sendMessage);
                    break;

                case "EXITTOADMINLIST":
                    executesEdit(userService.listOfAdmins(), messageId, chatId);
                    break;

                case "NEX":
                    InlineKeyboardMarkup mealDataBase = mealService.getMealDataBase(id, value, userRole);
                    executesEdit(mealDataBase, messageId, chatId);
                    break;

                case "PRE":
                    InlineKeyboardMarkup mealDataBase0 = mealService.getMealDataBase(id, value, userRole);
                    executesEdit(mealDataBase0, messageId, chatId);
                    break;

                case "PARENTNEXT":
                    executesEdit(categoryService.categoryList(value, id), messageId, chatId);
                    break;

                case "PARENTPREVIOUS":
                    executesEdit(categoryService.categoryList(value, id), messageId, chatId);
                    break;

                case "USERNEXT":
                    executesEdit(categoryService.listToUser(id, value), messageId, chatId);
                    break;

                case "USERPREVIOUS":
                    executesEdit(categoryService.listToUser(id, value), messageId, chatId);
                    break;

                case "NUMBEROFUSERS":
                    String string = userService.numberOfUsers();
                    sendMessage.setText(string);
                    sendMessage.setChatId(String.valueOf(chatId));
                    executes2(sendMessage);
                    break;

                case "CATEGORIYA":
                    InlineKeyboardMarkup parentCategory = categoryService.childCategory(id, 0);
                    if (!parentCategory.getKeyboard().isEmpty()) {
                        executes(null, parentCategory, chatId, responseText);
                        if (userRole == 1)
                            executes(null, buttonController.afterCategory(id), chatId, responseText);
                    } else {
                        InlineKeyboardMarkup mealDataBase1 = mealService.getMealDataBase(id, 0, userRole);

                        if (mealDataBase1.getKeyboard().isEmpty()) {
                            sendMessage.setText("Kategoriya bo'sh");
                            sendMessage.setChatId(String.valueOf(chatId));
                            executes2(sendMessage);
                            if (userRole == 1) {
                                InlineKeyboardMarkup inlineKeyboardMarkup2 = buttonController.afterCategory(id);
                                executes(null, inlineKeyboardMarkup2, chatId, responseText);
                            }
                        } else {
                            executesEdit(mealDataBase1, messageId, chatId);

                        }

                    }
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + str);
            }
        } else if (update.getMessage().hasDocument()) {
            long chatId = update.getMessage().getChatId();
            Integer status = userService.getStatus(chatId);

            String doc_id = update.getMessage().getDocument().getFileId();
            String doc_name = update.getMessage().getDocument().getFileName();
            String doc_mine = update.getMessage().getDocument().getMimeType();
            int doc_size = update.getMessage().getDocument().getFileSize();
            String getID = String.valueOf(update.getMessage().getFrom().getId());

            Document document = new Document();
            document.setMimeType(doc_mine);
            document.setFileName(doc_name);
            document.setFileSize(doc_size);
            document.setFileId(doc_id);

            File file1 = null;
            GetFile getFile = new GetFile();
            getFile.setFileId(document.getFileId());
            File file2 = null;
            try {
                org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
//                this.path += "/data/userDoc/" + getID + "_" + doc_name;
//                file1 = downloadFile(file, new File("./data/userDoc/" + getID + "_" + doc_name));
                file2 = downloadFile(file);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            byte[] fileContent = FileUtil.readAsByteArray(file2);

            if (status == 6) {
                sendMessage = new SendMessage();
                userService.setAdminStatus(4, chatId);
                boolean b = mealService.editMeal(mealName, description, fileContent, videoUrl, doc_name, mealId);
                if (b) {
                    sendMessage.setText("Ta'om taxrirlandi");
                } else sendMessage.setText("Ta'om taxrirlashda muammo");

                sendMessage.setChatId(String.valueOf(chatId));
                executes2(sendMessage);

            } else {
                boolean isHas = mealService.addMeal(mealName, description, categoryId, fileContent, videoUrl, doc_name, chatId);
                 sendMessage = new SendMessage();
                if (!isHas) {
                    sendMessage.setText("Ta'om qo'shildi");
                } else {
                    sendMessage.setText("Ta'om qo'shishda muammo (Ta'om nomi allaqachon mavjud)");
                }
                sendMessage.setChatId(String.valueOf(chatId));
                executes2(sendMessage);
            }
            if (count == 3) {
                mealName = "";
                description = "";
                categoryId = 0;
                videoUrl = "";
                count = 0;
                mealId = 0;

            }
            if (status==10){
                String excel_id = update.getMessage().getDocument().getFileId();
                String excel_name = update.getMessage().getDocument().getFileName();
                String excel_mine = update.getMessage().getDocument().getMimeType();
                int excel_size = update.getMessage().getDocument().getFileSize();
                String excel_getID = String.valueOf(update.getMessage().getFrom().getId());

                Document document1 = new Document();
                document.setMimeType(doc_mine);
                document.setFileName(doc_name);
                document.setFileSize(doc_size);
                document.setFileId(doc_id);

                File file = null;
                getFile = new GetFile();
                getFile.setFileId(document.getFileId());
                File file3 = null;
                try {
                    org.telegram.telegrambots.meta.api.objects.File file4 = execute(getFile);
//                this.path += "/data/userDoc/" + getID + "_" + doc_name;
//                file1 = downloadFile(file, new File("./data/userDoc/" + getID + "_" + doc_name));
                    file3 = downloadFile(file4);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

        } else if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();
            sendMessage = new SendMessage();
            Chat contact = update.getMessage().getChat();
            String name = contact.getFirstName() + "-" + contact.getLastName();
            String nickName = contact.getUserName();
            List<UserDataBase> users = userRepository.findAll();

            if (chatId == 915145143) {
                boolean b = userService.addBek(chatId);
                if (!b) {
                    userService.addUser(chatId, name, nickName, 4, 1);
                }
            }
            if (update.getMessage().getPhoto() != null) {
                if (userService.getStatus(chatId) == 8) {
                    List<UserDataBase> all = userRepository.findAll();
                    fileId = update.getMessage().getPhoto().get(0).getFileId();
                    userService.setAdminStatus(4, chatId);
                    SendPhoto sendPhoto = new SendPhoto();
                    sendPhoto.setCaption(addText);
                    sendPhoto.setPhoto(new InputFile(fileId));
                    for (UserDataBase userDataBase : all
                    ) {
                        try {
                            sendPhoto.setChatId(String.valueOf(userDataBase.getChadId()));
                            execute(sendPhoto);
                        }catch (Exception e){

                        }

                    }
                    addText = "";
                    fileId = "";
                }
            } else {
                UserDataBase signedUser = userService.isHasUser(chatId);
                if (signedUser != null) {

                    if (signedUser.getUserRole() == 1 || chatId == 915145143) {
                        switch (text) {
                            case "/start":
                                this.responseText = "Salom " + name + "\nXush kelibsiz!";

                                mealName = "";
                                description = "";
                                categoryId = 0;
                                videoUrl = "";
                                count = 0;
                                userService.setAdminStatus(4, chatId);

                                ReplyKeyboardMarkup replyKeyboardMarkup = buttonController.baseButtons();

                                executes(replyKeyboardMarkup, null, chatId, responseText);
                                break;

                            case "Kategoriya":
                                InlineKeyboardMarkup inlineKeyboardMarkup1 = categoryService.categoryList(0, 0);
                                if (inlineKeyboardMarkup1.getKeyboard().isEmpty()) {
                                    userService.setAdminStatus(1, chatId);
                                    sendMessage.setText("Kategoriyalar mavjud emas\nKategoriya qo'shish uchun nomini kiriting: ");
                                    sendMessage.setChatId(String.valueOf(chatId));
                                    executes2(sendMessage);
                                } else executes(null, inlineKeyboardMarkup1, chatId, responseText);

                                break;

                            case "User":
                                InlineKeyboardMarkup inlineKeyboardMarkup = userService.searchUser();
                                executes(null, inlineKeyboardMarkup, chatId, responseText);

                                break;

                            case "Reklama yuborish":
                                userService.setAdminStatus(8, chatId);
                                sendMessage.setText("Reklama matnini kiriting");
                                sendMessage.setChatId(String.valueOf(chatId));
                                executes2(sendMessage);
                                break;

                            case "Bugungi namoz vaqti":
                                ConnectToUrl connectToUrl = new ConnectToUrl();
                                Root root = connectToUrl.namazTime();
                                NamazToString namaz = new NamazToString();
                                String str = namaz.namazTime(root);
                                sendMessage.setText(str);
                                sendMessage.setChatId(String.valueOf(chatId));
                                executes2(sendMessage);
                                break;


                            case "User  lar excel faylini olish":
                                WriteToExcel writeToExcel = new WriteToExcel(userRepository);
                                String path= "src/main/resources/UsersList.xls";
                                writeToExcel.writeToFile();
                                sendDocument(chatId, new File(path), "Userlar ro'yxati");
                                break;

                                case "User  lar ni exceldan olish":
                                userService.setAdminStatus(10,chatId);
                                sendMessage.setChatId(String.valueOf(chatId));
                                sendMessage.setText("Excel faylni yuboring");
                                executes2(sendMessage);
                                break;

                            case "Ob-havo ma'lumotlari":
                                ReplyKeyboardMarkup buttons1 = buttonController.weatherButtons();
                                executes(buttons1,null,chatId,responseText);
                                break;
                            case "Bugungi ob-havo":
                                ConnectTWeather connectTWeather1 = new ConnectTWeather();
                                pdp_g9.telegram_bot.weather.connection.Root root2 = connectTWeather1.weather();
                                WeatherToString weatherToString = new WeatherToString();
                                String weather2 = weatherToString.weather(root2,1);
                                sendMessage.setText(weather2);
                                sendMessage.setChatId(String.valueOf(chatId));
                                executes2(sendMessage);
                                break;

                            case "3 kunlik ob-havo":
                                connectTWeather1 = new ConnectTWeather();
                                pdp_g9.telegram_bot.weather.connection.Root root3 = connectTWeather1.weather();
                                WeatherToString weatherToString3 = new WeatherToString();
                                String weather3 = weatherToString3.weather(root3,3);
                                sendMessage.setText(weather3);
                                sendMessage.setChatId(String.valueOf(chatId));
                                executes2(sendMessage);
                                break;

                            case "5 kunlik ob-havo":
                                connectTWeather1 = new ConnectTWeather();
                                pdp_g9.telegram_bot.weather.connection.Root root4 = connectTWeather1.weather();
                                WeatherToString weatherToString4 = new WeatherToString();
                                String weather4 = weatherToString4.weather(root4,5);
                                sendMessage.setText(weather4);
                                sendMessage.setChatId(String.valueOf(chatId));
                                executes2(sendMessage);
                                break;


                            case "Exit ↩":
                                ReplyKeyboardMarkup replyKeyboardMarkup2 = buttonController.baseButtons();

                                executes(replyKeyboardMarkup2,null,chatId,"Bosh menyu");
                                break;

                            default:
                                Integer status = userService.getStatus(chatId);
                                if (status == 1) {
                                    categoryName = text;
                                    boolean isAdded = categoryService.addCategory(categoryName, parentId, chatId);
                                    if (!isAdded) {
                                        sendMessage.setText("Kategoriya qo'shildi");
                                        sendMessage.setChatId(String.valueOf(chatId));
                                        executes2(sendMessage);
                                        categoryName = "";
                                        parentId = 0;
                                    } else {
                                        sendMessage.setText("Categoriya nomi allaqachon mavjud, boshqa nom tanlang");
                                        sendMessage.setChatId(String.valueOf(chatId));
                                        executes2(sendMessage);
                                    }
                                } else if (status == 2) {
                                    count++;
                                    if (mealName.equals("")) {
                                        mealName = text;

                                        sendMessage.setText("description kiriting: ");
                                        sendMessage.setChatId(String.valueOf(chatId));
                                        executes2(sendMessage);
                                    } else if (description.equals("")) {
                                        description = text;
                                        sendMessage.setChatId(String.valueOf(chatId));
                                        sendMessage.setText("Video url yuboring: ");
                                        executes2(sendMessage);
                                    } else if (videoUrl.equals("")) {
                                        videoUrl = text;
                                        sendMessage.setChatId(String.valueOf(chatId));
                                        sendMessage.setText("Ta'om suratini yuboring: ");
                                        executes2(sendMessage);
                                    }

                                } else if (status == 3) {
                                    InlineKeyboardMarkup inlineKeyboardMarkup2 = userService.setAdmin(text.toUpperCase());
                                    executes(null, inlineKeyboardMarkup2, chatId, responseText);
                                } else if (status == 6) {
                                    count++;
                                    if (mealName.equals("")) {
                                        mealName = text;

                                        sendMessage.setText("description kiriting: ");
                                        sendMessage.setChatId(String.valueOf(chatId));
                                        executes2(sendMessage);
                                    } else if (description.equals("")) {
                                        description = text;
                                        sendMessage.setChatId(String.valueOf(chatId));
                                        sendMessage.setText("Video url yuboring: ");
                                        executes2(sendMessage);
                                    } else if (videoUrl.equals("")) {
                                        videoUrl = text;
                                        sendMessage.setChatId(String.valueOf(chatId));
                                        sendMessage.setText("Ta'om suratini yuboring: ");
                                        executes2(sendMessage);
                                    }
                                } else if (status == 7) {
                                    userService.setAdminStatus(4, chatId);
                                    boolean b = categoryService.editCategory(categoryId, text);
                                    if (b)
                                        sendMessage.setText("Kategoriya taxrirlandi");

                                    else
                                        sendMessage.setText("Kategoriya taxrirlashda muammo");

                                    sendMessage.setChatId(String.valueOf(chatId));
                                    executes2(sendMessage);
                                } else if (status == 8) {
                                    addText = text;
                                    sendMessage.setText("Reklama rasmini yuboring");
                                    sendMessage.setChatId(String.valueOf(chatId));
                                    executes2(sendMessage);
                                }
                                break;

                        }

                    } else {
                        String mealName = "";
                        Integer status = userService.getStatus(chatId);
                        if (status == 9) {
                            userService.setAdminStatus(4, chatId);
                            mealName = text;
                            InlineKeyboardMarkup inlineKeyboardMarkup = mealService.searchByName(mealName, chatId);
                            if (!inlineKeyboardMarkup.getKeyboard().isEmpty()) {
                                executes(null, inlineKeyboardMarkup, chatId, "Ta'om nomi bo'yicha qidiruv");
                            }
                            else {
                                sendMessage.setText("Qidiruv bo'yicha natija topilmadi");
                                sendMessage.setChatId(String.valueOf(chatId));
                                executes2(sendMessage);
                            }

                        }
                        String response = "";
                        String weatherResponse="";
                        switch (text) {
                            case "/start":
                                String userName = userService.getUserName(chatId);
                                String[] strings = splitData(userName);
                                response = "Xush kelibsiz " + strings[0];

                                ReplyKeyboardMarkup replyKeyboardMarkup = categoryService.mainMenuToUser();
                                executes(replyKeyboardMarkup, null, chatId, response);
                                break;
                            case "Kategoriya ro'yxati":
                                InlineKeyboardMarkup inlineKeyboardMarkup = categoryService.listToUser(0, 0);
                                executes(null, inlineKeyboardMarkup, chatId, responseText);
                                break;

                            case "Bugungi namoz vaqti":
                                ConnectToUrl connectToUrl = new ConnectToUrl();
                                Root root = connectToUrl.namazTime();
                                NamazToString namaz = new NamazToString();
                                String str = namaz.namazTime(root);
                                sendMessage.setText(str);
                                sendMessage.setChatId(String.valueOf(chatId));
                                executes2(sendMessage);
                                break;

                            case "Ta'omni nomi orqali qidirish":
                                userService.setAdminStatus(9, chatId);
                                sendMessage.setText("Ta'om nomini kiriting");
                                sendMessage.setChatId(String.valueOf(chatId));
                                executes2(sendMessage);

                                break;
                            case "Ob-havo ma'lumoti":
                                ReplyKeyboardMarkup replyKeyboardMarkup1 = buttonController.weatherButtons();
                                executes(replyKeyboardMarkup1, null, chatId, "Kunlik ro'yxatni tanlang");
                                break;

                            case "Bugungi ob-havo":
                                ConnectTWeather connectTWeather = new ConnectTWeather();
                                pdp_g9.telegram_bot.weather.connection.Root root1 = connectTWeather.weather();
                                WeatherToString weatherToString = new WeatherToString();
                                weatherResponse = weatherToString.weather(root1,1);
                                sendMessage=new SendMessage();
                                sendMessage.setText(weatherResponse);
                                sendMessage.setChatId(String.valueOf(chatId));
                                executes2(sendMessage);
                                break;

                            case "3 kunlik ob-havo":
                                ConnectTWeather connectTWeather1 = new ConnectTWeather();
                                pdp_g9.telegram_bot.weather.connection.Root root2 = connectTWeather1.weather();
                                WeatherToString weatherToString1 = new WeatherToString();
                                weatherResponse = weatherToString1.weather(root2,3);
                                sendMessage=new SendMessage();
                                sendMessage.setText(weatherResponse);
                                sendMessage.setChatId(String.valueOf(chatId));
                                executes2(sendMessage);
                                break;

                            case "5 kunlik ob-havo":
                                ConnectTWeather connectTWeather2 = new ConnectTWeather();
                                pdp_g9.telegram_bot.weather.connection.Root root3 = connectTWeather2.weather();
                                WeatherToString weatherToString2 = new WeatherToString();
                                weatherResponse = weatherToString2.weather(root3,5);
                                sendMessage=new SendMessage();
                                sendMessage.setText(weatherResponse);
                                sendMessage.setChatId(String.valueOf(chatId));
                                executes2(sendMessage);
                                break;

                            case "Exit ↩":
                                ReplyKeyboardMarkup replyKeyboardMarkup2 = categoryService.mainMenuToUser();
                                executes(replyKeyboardMarkup2,null,chatId,"Bosh menyu");
                                break;
                        }
                    }
                } else {
                    userService.addUser(chatId, name, nickName, 4, 2);
                    ReplyKeyboardMarkup replyKeyboardMarkup = categoryService.mainMenuToUser();
                    executes(replyKeyboardMarkup, null, chatId, responseText);
                }
            }
        }
    }

    public void executes
            (
                    ReplyKeyboardMarkup replyKeyboardMarkup,
                    InlineKeyboardMarkup inlineKeyboardMarkup,
                    long chatId,
                    String response
            ) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(response);
        sendMessage.setChatId(String.valueOf(chatId));
        if (replyKeyboardMarkup != null)
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        if (inlineKeyboardMarkup != null)
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void executesEdit
            (
                    InlineKeyboardMarkup inlineKeyboardMarkup,
                    Integer messageId,
                    long chatId
            ) {
        EditMessageText sendMessage = new EditMessageText();
        sendMessage.setMessageId(messageId);
        sendMessage.setText(this.responseText);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void executeDocument(SendDocument sendDocument) {
        try {
            execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public void executes2(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String[] splitData(String str) {
        String[] arrays = str.split("-");
        return arrays;
    }

    public void deleteMessage(long chatId, int messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(String.valueOf(chatId));
        deleteMessage.setMessageId(messageId);
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendDocument(Long chatId, File save, String caption){

        SendDocument sendDocumentRequest = new SendDocument();
        sendDocumentRequest.setChatId(String.valueOf(chatId));
        sendDocumentRequest.setDocument(new InputFile(save));
        sendDocumentRequest.setCaption(caption);
        executeDocument(sendDocumentRequest);
    }

}
