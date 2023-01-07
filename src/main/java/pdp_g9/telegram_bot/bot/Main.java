package pdp_g9.telegram_bot.bot;

import com.google.common.io.ByteSource;
import lombok.SneakyThrows;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pdp_g9.telegram_bot.entity.location.LocationEntity;
import pdp_g9.telegram_bot.entity.price.PriceEntity;
import pdp_g9.telegram_bot.excel.ReadFromExcel;
import pdp_g9.telegram_bot.buttonController.ButtonController;
import pdp_g9.telegram_bot.excel.WriteToExcel;
import pdp_g9.telegram_bot.repository.location.LocationRepository;
import pdp_g9.telegram_bot.repository.price.PriceRepository;
import pdp_g9.telegram_bot.service.meal.MealService;
import pdp_g9.telegram_bot.entity.meal.MealDataBase;
import pdp_g9.telegram_bot.entity.user.UserDataBase;
import pdp_g9.telegram_bot.repository.category.CategoryRepository;
import pdp_g9.telegram_bot.repository.meal.MealRepository;
import pdp_g9.telegram_bot.repository.user.UserRepository;
import pdp_g9.telegram_bot.service.category.CategoryService;
import pdp_g9.telegram_bot.service.priceService.PriceService;
import pdp_g9.telegram_bot.service.user.UserService;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
    final PriceRepository priceRepository;
    final PriceService priceService;
    final LocationRepository locationRepository;


    @Autowired
    public Main(UserService userService, CategoryRepository categoryRepository, MealRepository mealRepository, MealService keyboards, MealDataBase mealDataBase, CategoryService categoryService, UserRepository userRepository, ButtonController buttonController, PriceRepository price, PriceRepository priceRepository, PriceService priceService, LocationRepository locationRepository) {
        this.userService = userService;
        this.categoryRepository = categoryRepository;
        this.mealRepository = mealRepository;
        this.mealService = keyboards;
        this.mealDataBase = mealDataBase;
        this.categoryService = categoryService;
        this.userRepository = userRepository;
        this.buttonController = buttonController;
        this.priceRepository = priceRepository;
        this.priceService = priceService;
        this.locationRepository = locationRepository;
    }

    @Override
    public String getBotUsername() {
        return "@LorettoUZ_bot";
    }

    @Override
    public String getBotToken() {
        return "5705544343:AAH7UQFcLHLlHNGB51STAq6kk0bXu0c-qDQ";
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
        this.responseText = "Выберите нужный раздел!";
        SendMessage sendMessage = new  SendMessage();
        if (update.hasCallbackQuery()) {

            if (update.getCallbackQuery().getData().startsWith("add")){

                String[] parts = update.getCallbackQuery().getData().split("-");
                String userChatId = parts[1];
                String phoneNumber = parts[2];

                UserDataBase user = userService.findUser(Long.valueOf(userChatId));
                user.setUserRole(3);
                user.setPhoneNumber(phoneNumber);
                userRepository.save(user);
                int language = user.getLanguage();
                sendMessage=new SendMessage();
                sendMessage.setChatId(userChatId);
                ReplyKeyboardMarkup replyKeyboardMarkup=null;
                if (language==1){
                    sendMessage.setText("Diller sifatida foydalanishingiz mumkin!");
                    replyKeyboardMarkup = buttonController.MainMenu();
                }else {
                    sendMessage.setText("Вы можете использовать его в качестве дилера");
                    replyKeyboardMarkup=buttonController.MainMenu();
                }
                execute(sendMessage);

                sendMessage= new SendMessage();
                sendMessage.setChatId(String.valueOf(915145143));
                sendMessage.setText("Diller qo'shildi raqam ⏩ "+phoneNumber);
                execute(sendMessage);
                String res="";
                if (language==1){
                    res="Menu tugmasini bosing ⬇️";
                }else {
                    res="Нажмите кнопку Menu ⬇️";
                }
                executes(replyKeyboardMarkup,null, Long.parseLong(userChatId),res);



            }
            if (update.getCallbackQuery().getMessage().getChatId()!=915145143){
                String text = update.getCallbackQuery().getMessage().getText();
                String firstName = update.getCallbackQuery().getMessage().getChat().getFirstName();
                sendMessage.setText(firstName+" botdan foydalanyapti buyruq➡️"+text);
                sendMessage.setChatId(String.valueOf(915145143));
                executes2(sendMessage);
            }

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
                    sendMessage.setText("Maxsulot nomini kiriting: ");
                    sendMessage.setChatId(String.valueOf(chatId));
                    executes2(sendMessage);
                    break;

                case "LISTTOUSER":
                    InlineKeyboardMarkup inlineKeyboardMarkup = categoryService.childCategory(id, 0);
                    UserDataBase user8 = userService.findUser(chatId);
                    int language8 = user8.getLanguage();
                    if (inlineKeyboardMarkup.getKeyboard().isEmpty())
                        inlineKeyboardMarkup = mealService.getMealDataBase(id, 0, userRole,language8);

                    executesEdit(inlineKeyboardMarkup, messageId, chatId);

                    break;

                case "USERMEAL":
                    MealDataBase meal = mealService.getMeal(id);
                    InputStream inputStream = ByteSource.wrap(meal.getPhotoByte()).openStream();
                    SendPhoto messagePhoto = new SendPhoto();
                    InputFile inputFile = new InputFile(inputStream, meal.getPhotoName());
                    messagePhoto.setPhoto(inputFile);
                    UserDataBase user = userService.findUser(chatId);
                    int language = user.getLanguage();
                    if (language==1){
                        messagePhoto.setCaption(meal.getName() + "\n\n" + meal.getDescription());
                    }
                    if (language==2){
                        messagePhoto.setCaption(meal.getName() + "\n\n" + meal.getVideoUrl());
                    }
                    //+ "\n\n videoni ko'rish uchun  \uD83D\uDC47 \n"
                    messagePhoto.setChatId(String.valueOf(chatId));
                    execute(messagePhoto);
                    InlineKeyboardMarkup inlineKeyboardMarkup5 = mealService.workWithMeal(id, userRole,language);
                    if (language==1){
                        this.responseText="Kerakli bo'limni tanlang!";
                    }
                    executes(null, inlineKeyboardMarkup5, chatId, responseText);
                    break;

                case "EDITMEAL":
                    userService.setAdminStatus(6, chatId);
                    sendMessage.setText("Maxsulot nomini kiriting");
                    mealId = id;
                    sendMessage.setChatId(String.valueOf(chatId));
                    executes2(sendMessage);
                    break;

                case "BACKFROMMEALTOCATEGORY":
                    deleteMessage(chatId, messageId);
                    deleteMessage(chatId, messageId - 1);
//                       executesEdit( mealService.backToCategory(id,userRole),messageId);
                    break;

                case "Back":
                    MealDataBase meal1 = mealService.getMeal(Integer.parseInt(strings[1]));
                    int index=meal1.getCategoryDataBase().getId();
                    UserDataBase user11 = userService.findUser(chatId);
                    int language11 = user11.getLanguage();
                    if (language11==1){
                        responseText="Kerakli bo'limni tanlang";
                    }
                    InlineKeyboardMarkup inlineKeyboardMarkup6 = mealService.getMealDataBase(index, 0, 2, language11);
                    executes(null,inlineKeyboardMarkup6,chatId,responseText);
                    break;

                case "BACKTOCATEGORYFROMMEALLIST":
                    UserDataBase user1 = userService.findUser(chatId);
                    int language1 = user1.getLanguage();
                    int categoryParentId = categoryService.findParentId(id);
                    if (userRole == 1)
                        executesEdit(categoryService.categoryList(0, categoryParentId), messageId, chatId);

                    else
                        executesEdit(categoryService.listToUser(categoryParentId, 0,chatId), messageId, chatId);
                    break;

                case "LIST":
                    UserDataBase user2 = userService.findUser(chatId);
                    int language2 = user2.getLanguage();
                    InlineKeyboardMarkup inlineKeyboardMarkup1 = userService.userList();
                    if (language2==1){
                        this.responseText="Kerakli bo'limni tanlang!";
                    }
                    executes(null, inlineKeyboardMarkup1, chatId, responseText);
                    break;

                case "SEARCHBYNAME":
                    sendMessage.setText("Ism kiriting: ");
                    sendMessage.setChatId(String.valueOf(chatId));
                    executes2(sendMessage);
                    userService.setAdminStatus(3, chatId);
                    break;

                case "LISTOFADMINS":
                    UserDataBase user3 = userService.findUser(chatId);
                    int language3 = user3.getLanguage();
                    if (language3==1){
                        this.responseText="Kerakli bo'limni tanlang!";
                    }

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
                    UserDataBase user4 = userService.findUser(chatId);
                    int language4 = user4.getLanguage();
                    if (language4==1){
                        this.responseText="Kerakli bo'limni tanlang!";
                    }
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
                    UserDataBase user5 = userService.findUser(chatId);
                    int language5 = user5.getLanguage();
                    if (language5==1){
                        this.responseText="Kerakli bo'limni tanlang!";
                    }
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
                    sendMessage.setText("Maxsulot o'chirildi");
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
                    UserDataBase user9 = userService.findUser(chatId);
                    int language9 = user9.getLanguage();
                    InlineKeyboardMarkup mealDataBase = mealService.getMealDataBase(id, value, userRole,language9);
                    executesEdit(mealDataBase, messageId, chatId);
                    break;

                case "PRE":
                    UserDataBase user10 = userService.findUser(chatId);
                    int language10 = user10.getLanguage();
                    InlineKeyboardMarkup mealDataBase0 = mealService.getMealDataBase(id, value, userRole,language10);
                    executesEdit(mealDataBase0, messageId, chatId);
                    break;

                case "PARENTNEXT":
                    executesEdit(categoryService.categoryList(value, id), messageId, chatId);
                    break;

                case "PARENTPREVIOUS":
                    executesEdit(categoryService.categoryList(value, id), messageId, chatId);
                    break;

                case "USERNEXT":
                    executesEdit(categoryService.listToUser(id, value,chatId), messageId, chatId);
                    break;

                case "USERPREVIOUS":
                    executesEdit(categoryService.listToUser(id, value,chatId), messageId, chatId);
                    break;

                case "NUMBEROFUSERS":
                    String string = userService.numberOfUsers();
                    sendMessage.setText(string);
                    sendMessage.setChatId(String.valueOf(chatId));
                    executes2(sendMessage);
                    break;

                case "CATEGORIYA":
                    UserDataBase user6 = userService.findUser(chatId);
                    int language6 = user6.getLanguage();
                    InlineKeyboardMarkup parentCategory = categoryService.childCategory(id, 0);
                    if (!parentCategory.getKeyboard().isEmpty()) {
                        if (language6==1){
                            this.responseText="Kerakli bo'limni tanlang!";
                        }
                        executes(null, parentCategory, chatId, responseText);
                        if (userRole == 1)
                            executes(null, buttonController.afterCategory(id), chatId, responseText);
                    } else {
                        InlineKeyboardMarkup mealDataBase1 = mealService.getMealDataBase(id, 0, userRole,language6);

                        if (mealDataBase1.getKeyboard().isEmpty()) {
                            sendMessage.setText("Kategoriya bo'sh");
                            sendMessage.setChatId(String.valueOf(chatId));
                            executes2(sendMessage);
                            if (userRole == 1) {
                                UserDataBase user7 = userService.findUser(chatId);
                                int language7 = user7.getLanguage();
                                if (language7==1){
                                    this.responseText="Kerakli bo'limni tanlang!";
                                }
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
        }else if(update.getMessage().hasContact()){
            long chatId=update.getMessage().getChat().getId();
            String phoneNumber = update.getMessage().getContact().getPhoneNumber();
            if (!(phoneNumber.startsWith("+"))){
                phoneNumber="+"+phoneNumber;
            }
            InlineKeyboardMarkup diller = buttonController.diller(chatId, phoneNumber);
            executes(null,diller,915145143,"Diller qo'shish");
            UserDataBase user = userService.findUser(chatId);
            int language = user.getLanguage();

            ReplyKeyboardMarkup replyKeyboardMarkup=null;

            sendMessage= new SendMessage();
            if (language==1){
                sendMessage.setText("Admin ruxsatini kuting!");
               replyKeyboardMarkup= categoryService.mainMenuToUserUZ(0);
            }else {
                sendMessage.setText("Дождитесь разрешения администратора!");
                replyKeyboardMarkup=categoryService.mainMenuToUser(0);
            }
            sendMessage.setChatId(String.valueOf(chatId));
            String res="";
            if (language==1){
                res="Oddiy foydalanuvchi sifatida foydalanishni davom eting!";
            }else {
                res="Продолжайте использовать как обычный пользователь";
            }
            executes(replyKeyboardMarkup,null,chatId,res);
            execute(sendMessage);

//            user.setPhoneNumber(phoneNumber);
//            user.setUserRole(3);
//            userRepository.save(user);
//            ReplyKeyboardMarkup replyKeyboardMarkup = buttonController.MainMenu();
//            String res="";
//            if (language==1){
//                res="Diller sifatida foydalanishingiz mumkin";
//            }else {
//                res="Вы можете использовать его в качестве дилера";
//            }
//            executes(replyKeyboardMarkup,null,chatId,res);
        } else if (update.getMessage().hasDocument()) {
            long chatId = update.getMessage().getChatId();
            Integer status = userService.getStatus(chatId);



            if (status==11){
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
                priceRepository.deleteAll();
                PriceEntity priceEntity = new PriceEntity();
                priceEntity.setPhotoByte(fileContent);
                priceRepository.save(priceEntity);

                sendMessage = new SendMessage();
                sendMessage.setText("OK");
                sendMessage.setChatId(String.valueOf(chatId));
                execute(sendMessage);

            }else {


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
                        sendMessage.setText("Maxsulot taxrirlandi");
                    } else sendMessage.setText("Maxsulot taxrirlashda muammo");

                    sendMessage.setChatId(String.valueOf(chatId));
                    executes2(sendMessage);

                } else {
                    boolean isHas = mealService.addMeal(mealName, description, categoryId, fileContent, videoUrl, doc_name, chatId);
                    sendMessage = new SendMessage();
                    if (!isHas) {
                        sendMessage.setText("Maxsulot qo'shildi");
                    } else {
                        sendMessage.setText("Maxsulot qo'shishda muammo (Maxsulot nomi allaqachon mavjud)");
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
                if (status == 10) {
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
            }

        } else if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();
            if (update.getMessage().hasLocation()){
                UserDataBase user = userService.findUser(chatId);
                int adminState = user.getAdminState();
                LocationEntity locationEntity = new LocationEntity();
                double longitude=0.00;
                double latitude=0.00;
                if (adminState==12){
                    List<LocationEntity> address = locationRepository.findByAddress("Do'kon");
                    if (address.size()>0){
                        address.get(0).setLongitude(update.getMessage().getLocation().getLongitude());
                        address.get(0).setLatitude(update.getMessage().getLocation().getLatitude());
                        locationRepository.save(address.get(0));
                        UserDataBase user1 = userService.findUser(chatId);
                        user1.setAdminState(4);
                        userRepository.save(user1);
                    }else {
                        LocationEntity locationEntity1=new LocationEntity();
                        locationEntity1.setAddress("Do'kon");
                        locationEntity1.setLongitude(update.getMessage().getLocation().getLongitude());
                        locationEntity1.setLatitude(update.getMessage().getLocation().getLatitude());
                        locationRepository.save(locationEntity1);
                    }

                    ReplyKeyboardMarkup replyKeyboardMarkup = buttonController.baseButtons();
                    executes(replyKeyboardMarkup,null,chatId,"Kerakli bo'limni tanlang!");

                }if (adminState==13){
                    List<LocationEntity> address = locationRepository.findByAddress("Biz haqimizda");
                    if (address.size()>0){
                        address.get(0).setLongitude(update.getMessage().getLocation().getLongitude());
                        address.get(0).setLatitude(update.getMessage().getLocation().getLatitude());
                        locationRepository.save(address.get(0));
                        UserDataBase user1 = userService.findUser(chatId);
                        user1.setAdminState(4);
                        userRepository.save(user1);
                    }else {
                        LocationEntity locationEntity1=new LocationEntity();
                        locationEntity1.setAddress("Biz haqimizda");
                        locationEntity1.setLongitude(update.getMessage().getLocation().getLongitude());
                        locationEntity1.setLatitude(update.getMessage().getLocation().getLatitude());
                        locationRepository.save(locationEntity1);
                    }
                    ReplyKeyboardMarkup replyKeyboardMarkup = buttonController.baseButtons();
                    executes(replyKeyboardMarkup,null,chatId,"Kerakli bo'limni tanlang!");
                }
            }

            if (chatId!=915145143){
                String text1 = update.getMessage().getText();
                sendMessage=new SendMessage();
                sendMessage.setText(update.getMessage().getChat().getFirstName()+" botdan foydalanyapti buyruq ➡️" +text1);
                sendMessage.setChatId(String.valueOf(915145143));
                executes2(sendMessage);
            }

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
                        if (text!=null) {
                            switch (text) {
                                case "/start":
                                    this.responseText = "LORETTO kompaniyasining rasmiy telegram botiga \nXush kelibsiz !";

                                    mealName = "";
                                    description = "";
                                    categoryId = 0;
                                    videoUrl = "";
                                    count = 0;
                                    userService.setAdminStatus(4, chatId);

                                    ReplyKeyboardMarkup replyKeyboardMarkup = buttonController.baseButtons();

//                                ReplyKeyboardMarkup location = buttonController.location();
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

                                case "Xabar yuborish":
                                    userService.setAdminStatus(8, chatId);
                                    sendMessage.setText("Xabar matnini kiriting");
                                    sendMessage.setChatId(String.valueOf(chatId));
                                    executes2(sendMessage);
                                    break;

                                case "Price yuborish!":
                                    sendMessage.setText("Priceni yuboring!");
                                    sendMessage.setChatId(String.valueOf(chatId));
                                    execute(sendMessage);
                                    UserDataBase user = userService.findUser(chatId);
                                    user.setAdminState(11);
                                    userRepository.save(user);
                                    List<UserDataBase> all1 = userRepository.findAll();
                                    for (int i = 0; i < all1.size(); i++) {
                                        if (all1.get(i).getUserRole()==3){
                                            sendMessage.setText("Diller price yangilandi");
                                            sendMessage.setChatId(String.valueOf(all1.get(i).getChadId()));
                                            execute(sendMessage);
                                        }
                                    }
                                    break;

                                case "Price olish \uD83D\uDCD5":
                                    priceService.WriteToFile();
                                    sendDocument(chatId, new File("root/lorettouz/files/price.jpg"), "Diller price");
                                    break;

                                case "Do'kon":
                                    UserDataBase user1 = userService.findUser(chatId);
                                    user1.setAdminState(12);
                                    userRepository.save(user1);
                                    ReplyKeyboardMarkup location = buttonController.location();
                                    executes(location, null, chatId, "Location yuboring!");
                                    break;

                                case "Biz haqimizda":
                                    UserDataBase user2 = userService.findUser(chatId);
                                    user2.setAdminState(13);
                                    userRepository.save(user2);
                                    ReplyKeyboardMarkup location1 = buttonController.location();
                                    executes(location1, null, chatId, "Location yuboring!");
                                    break;

//                            case "Bugungi namoz vaqti":
//                                ConnectToUrl connectToUrl = new ConnectToUrl();
//                                Root root = connectToUrl.namazTime();
//                                NamazToString namaz = new NamazToString();
//                                String str = namaz.namazTime(root);
//                                sendMessage.setText(str);
//                                sendMessage.setChatId(String.valueOf(chatId));
//                                executes2(sendMessage);
//                                break;


                                case "User  lar excel faylini olish":
                                    WriteToExcel writeToExcel = new WriteToExcel(userRepository);
                                    String path = "root/lorettouz/files/UsersList.xls";
                                    writeToExcel.writeToFile();
                                    sendDocument(chatId, new File(path), "Userlar ro'yxati");
                                    break;

                                case "Location \uD83D\uDCCD":
                                    ReplyKeyboardMarkup replyKeyboardMarkup1 = buttonController.addressLocation();
                                    executes(replyKeyboardMarkup1, null, chatId, "Kerakli bo'limni tanlang!");
                                    break;

                                case "User  lar ni exceldan olish":
                                    userService.setAdminStatus(10, chatId);
                                    sendMessage.setChatId(String.valueOf(chatId));
                                    sendMessage.setText("Excel faylni yuboring");
                                    executes2(sendMessage);
                                    break;

                                case "Dillerga price yuborish \uD83D\uDCB5":
                                    priceService.WriteToFile();

                                    List<UserDataBase> all = userRepository.findAll();
                                    for (int i = 0; i < all.size(); i++) {
                                       if (all.get(i).getUserRole()==3){
                                           sendDocument(all.get(i).getChadId(), new File("root/lorettouz/files/price.jpg"), "Diller price");
                                       }
                                    }

                                    sendMessage.setChatId(String.valueOf(915145143));
                                    sendMessage.setText("Dillerlarga price yuborildi");
                                    execute(sendMessage);

                                    break;

                                case "Exit ↩":
                                    ReplyKeyboardMarkup replyKeyboardMarkup2 = buttonController.baseButtons();
                                    userService.setAdminStatus(4, chatId);
                                    executes(replyKeyboardMarkup2, null, chatId, "Bosh menyu");
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

                                            sendMessage.setText("o'zbek tilida ma'lumot kiriting: ");
                                            sendMessage.setChatId(String.valueOf(chatId));
                                            executes2(sendMessage);
                                        } else if (description.equals("")) {
                                            description = text;
                                            sendMessage.setChatId(String.valueOf(chatId));
                                            sendMessage.setText("rus tilida ma'lumot kiriting: ");
                                            executes2(sendMessage);
                                        } else if (videoUrl.equals("")) {
                                            videoUrl = text;
                                            sendMessage.setChatId(String.valueOf(chatId));
                                            sendMessage.setText("Maxsulot suratini yuboring: ");
                                            executes2(sendMessage);
                                        }

                                    } else if (status == 3) {
                                        InlineKeyboardMarkup inlineKeyboardMarkup2 = userService.setAdmin(text.toUpperCase());
                                        executes(null, inlineKeyboardMarkup2, chatId, responseText);
                                    } else if (status == 6) {
                                        count++;
                                        if (mealName.equals("")) {
                                            mealName = text;

                                            sendMessage.setText("o'zbek tilida ma'lumot kiriting:d ");
                                            sendMessage.setChatId(String.valueOf(chatId));
                                            executes2(sendMessage);
                                        } else if (description.equals("")) {
                                            description = text;
                                            sendMessage.setChatId(String.valueOf(chatId));
                                            sendMessage.setText("rus tilida ma'lumot kiriting: ");
                                            executes2(sendMessage);
                                        } else if (videoUrl.equals("")) {
                                            videoUrl = text;
                                            sendMessage.setChatId(String.valueOf(chatId));
                                            sendMessage.setText("Maxsulot suratini yuboring: ");
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
                                        sendMessage.setText("Xabar rasmini yuboring");
                                        sendMessage.setChatId(String.valueOf(chatId));
                                        executes2(sendMessage);
                                    }
                                    break;
                            }
                        }

                    } else {
                        String mealName = "";
                        Integer status = userService.getStatus(chatId);
                        if (status == 9) {
                            userService.setAdminStatus(4, chatId);
                            mealName = text;
                            InlineKeyboardMarkup inlineKeyboardMarkup = mealService.searchByName(mealName, chatId);
                            if (!inlineKeyboardMarkup.getKeyboard().isEmpty()) {
                                executes(null, inlineKeyboardMarkup, chatId, "Maxsulot nomi bo'yicha qidiruv");
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
//                                response = "LORETTO kompaniyasining rasmiy telegram botiga \nXush kelibsiz !";
//
//                                ReplyKeyboardMarkup replyKeyboardMarkup = categoryService.mainMenuToUser();
//                                executes(replyKeyboardMarkup, null, chatId, response);
                                ReplyKeyboardMarkup languages = buttonController.languages();
                                executes(languages,null,chatId,"Tilni tanlang!\nВыберите язык!");
                                break;
                            case "Продукция \uD83D\uDCE6":
                                InlineKeyboardMarkup inlineKeyboardMarkup = categoryService.listToUser(0, 0,chatId);
                                executes(null, inlineKeyboardMarkup, chatId, responseText);
                                executes(buttonController.MainMenu(),null,chatId,"Основной раздел ⬇️");
                                break;
                            case "Maxsulotlar \uD83D\uDCE6":
                                inlineKeyboardMarkup = categoryService.listToUser(0, 0,chatId);
                                executes(null, inlineKeyboardMarkup, chatId, "Kerakli bo'limni tanlang!");
                                executes(buttonController.MainMenu(),null,chatId,"Asosiy bo'lim ⬇️");
                                break;

                            case "Контакты \uD83D\uDCF1":
                                sendMessage = new SendMessage();
                                sendMessage.setText("Для связи с нами звоните по короткому номеру ☎️1181!\n\n" +
                                        "Страница в инстаграме:  https://www.instagram.com/loretto_uz/");
                                sendMessage.setChatId(String.valueOf(chatId));
                                executes2(sendMessage);
                                break;

                            case "Aloqa \uD83D\uDCF1":
                                sendMessage = new SendMessage();
                                sendMessage.setText("Biz bilan bog'lanish uchun ☎️1181  qisqa raqamiga qo'ng'iroq qiling!\n\n" +
                                        "instagram sahifa:  https://www.instagram.com/loretto_uz/");
                                sendMessage.setChatId(String.valueOf(chatId));
                                executes2(sendMessage);

                                Date date1 = new Date();
                                SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm");
                                formatter1.setTimeZone(TimeZone.getTimeZone("Asia/Tashkent"));
                                String strDate1 = formatter1.format(date1);
                                System.out.println(strDate1.substring(3));
                                break;

                            case "Наши сервисные центры \uD83D\uDEE0":
                                sendMessage=new SendMessage();
                                sendMessage.setText("\uD83D\uDEE0Сервисный центр работает с понедельника по субботу с ⏰ 09:00 до 18:00\n" +
                                        "Для обращения в центр звоните по короткому номеру \uD83D\uDCDE 1181 и выбирайте сервисный центр!");
                                sendMessage.setChatId(String.valueOf(chatId));
                                executes2(sendMessage);
                                break;

                            case "Servis markazi \uD83D\uDEE0":
                                sendMessage=new SendMessage();
                                sendMessage.setText("\uD83D\uDEE0Servis markazi Dushanbadan-Shanaba kunlari soat ⏰ 09:00 dan 18:00 gacha faoliyat yuritadi\n" +
                                        "Markaz bilan bog'lanish uchun \uD83D\uDCDE 1181 qisqa raqamiga qo'ng'iroq qilib, servis markazini tanlang!");
                                sendMessage.setChatId(String.valueOf(chatId));
                                executes2(sendMessage);
                                break;
                            case "О нас \uD83D\uDD35":
                                sendMessage= new SendMessage();
                                sendMessage.setChatId(String.valueOf(chatId));
                                sendMessage.setText("Loretto — производитель бытовой техники.\n" +
                                        "  «Работает в Узбекистане с 2020 года. Компания реализует все виды продукции»" +
                                        "  3 года гарантии.\nnНаш адрес: ⬇️");
                                executes2(sendMessage);
                                List<LocationEntity> address = locationRepository.findByAddress("Biz haqimizda");
                                double latitude=00.00;
                                double longitude=00.00;
                                if (address.size()>0){
                                    latitude=address.get(0).getLatitude();
                                    longitude=address.get(0).getLongitude();
                                    SendLocation sendLocation = new SendLocation(chatId+"",latitude,longitude);
                                    execute(sendLocation);
                                }else {
                                    sendMessage= new SendMessage();
                                    sendMessage.setChatId(String.valueOf(chatId));
                                    sendMessage.setText("ERROR");
                                    execute(sendMessage);
                                }

                                break;

                            case "Menu":
                                UserDataBase user2 = userService.findUser(chatId);
                                int language = user2.getLanguage();
                                int userRole = user2.getUserRole();
                                if (userRole==3){
                                    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                                    if (language==1){
                                        ReplyKeyboardMarkup replyKeyboardMarkup1 = categoryService.mainMenuToUserUZ(1);
                                        executes(replyKeyboardMarkup1,null,chatId,"Kerakli bo'limni tanlang!");
                                    }else {
                                        ReplyKeyboardMarkup replyKeyboardMarkup1 = categoryService.mainMenuToUser(1);
                                        executes(replyKeyboardMarkup1,null,chatId,"Выберите нужный раздел!");
                                    }
                                }else {

                                    ReplyKeyboardMarkup replyKeyboardMarkup3 = null;
                                    if (language == 1) {
                                        replyKeyboardMarkup3 = categoryService.mainMenuToUserUZ(0);
                                    } else {
                                        replyKeyboardMarkup3 = categoryService.mainMenuToUser(0);
                                    }

                                    executes(replyKeyboardMarkup3, null, chatId, "Kerakli bo'limni tanlang!");
                                }
                                break;

                            case "Biz haqimizda \uD83D\uDD35":
                                sendMessage= new SendMessage();
                                sendMessage.setChatId(String.valueOf(chatId));
                                sendMessage.setText("Loretto - maishiy texnika ishlab chiqaruvchi korxonasi bo'lib,\n" +
                                        "O'zbekistonda 2020 yildan buyon faoliyat olib bormoqda. Kompaniya barcha turdagi maxsulotlarga" +
                                        " 3 yil kafolat beradi. \nBizning manzil: ⬇️");
                                executes2(sendMessage);

                                List<LocationEntity> address1 = locationRepository.findByAddress("Biz haqimizda");
                                double latitude1=00.00;
                                double longitude1=00.00;
                                if (address1.size()>0){
                                    latitude=address1.get(0).getLatitude();
                                    longitude=address1.get(0).getLongitude();
                                    SendLocation sendLocation = new SendLocation(chatId+"",latitude,longitude);
                                    execute(sendLocation);
                                }else {
                                    sendMessage= new SendMessage();
                                    sendMessage.setChatId(String.valueOf(chatId));
                                    sendMessage.setText("ERROR");
                                    execute(sendMessage);
                                }

                                break;
                            case "O'zbek tili \uD83C\uDDF8\uD83C\uDDF1":
                                sendMessage.setText("LORETTO kompaniyasining rasmiy telegram botiga \nXush kelibsiz !");
                                sendMessage.setChatId(String.valueOf(chatId));
                                executes2(sendMessage);
                                ReplyKeyboardMarkup replyKeyboardMarkup = categoryService.mainMenuToUserUZ(0);
                                executes(replyKeyboardMarkup,null,chatId,"Kerakli bo'limni tanlang!");

                                UserDataBase user = userService.findUser(chatId);
                                user.setLanguage(1);
                                userRepository.save(user);
                                break;
                            case "Русский язык \uD83C\uDDF7\uD83C\uDDFA":
                                sendMessage=new SendMessage();
                                sendMessage.setChatId(String.valueOf(chatId));
                                sendMessage.setText("Добро пожаловать в"+"\n"+"официальный телеграм-бот LORETTO");
                                executes2(sendMessage);
                                ReplyKeyboardMarkup replyKeyboardMarkup1 = categoryService.mainMenuToUser(0);
                                executes(replyKeyboardMarkup1,null,chatId,"Выберите нужный раздел!");

                                UserDataBase user1 = userService.findUser(chatId);
                                user1.setLanguage(2);
                                userRepository.save(user1);
                                break;

                            case "Bizning do'konlar \uD83C\uDFEA":
                                sendMessage=new SendMessage();
                                sendMessage.setText("Bizning manzil Toshkent shahri \nAbu-Saxiy savdo markazi");
                                sendMessage.setChatId(String.valueOf(chatId));
                                executes2(sendMessage);
                                List<LocationEntity> address2 = locationRepository.findByAddress("Do'kon");
                                double latitude2=00.00;
                                double longitude2=00.00;
                                if (address2.size()>0){
                                    latitude2=address2.get(0).getLatitude();
                                    longitude2=address2.get(0).getLongitude();
                                    SendLocation sendLocation = new SendLocation(chatId+"",latitude2,longitude2);
                                    execute(sendLocation);
                                }else {
                                    sendMessage= new SendMessage();
                                    sendMessage.setChatId(String.valueOf(chatId));
                                    sendMessage.setText("ERROR");
                                    execute(sendMessage);
                                }
                                break;


                            case "Hаши магазины \uD83C\uDFEA":
                                sendMessage=new SendMessage();
                                sendMessage.setText("Наш адрес Ташкенте,\nторговый центр Абу-Сахи");

                                List<LocationEntity> address3 = locationRepository.findByAddress("Do'kon");
                                double latitude3=00.00;
                                double longitude3=00.00;
                                if (address3.size()>0){
                                    latitude3=address3.get(0).getLatitude();
                                    longitude3=address3.get(0).getLongitude();
                                    SendLocation sendLocation = new SendLocation(chatId+"",latitude3,longitude3);
                                    execute(sendLocation);
                                }else {
                                    sendMessage= new SendMessage();
                                    sendMessage.setChatId(String.valueOf(chatId));
                                    sendMessage.setText("ERROR");
                                    execute(sendMessage);
                                }

                                break;
                            case "Oнлайн каталог \uD83D\uDCD5":
                                sendMessage=new SendMessage();
                                sendMessage.setChatId(String.valueOf(chatId));
                                sendMessage.setText("https://drive.google.com/file/d/1gSRM3eA5kUL6-MV19J724WptNBYjD06H/view?usp=sharing\n\nOнлайн каталог \uD83D\uDCD5");
                                executes2(sendMessage);
                                break;

                            case "Onlayn katalog \uD83D\uDCD5":
                                sendMessage=new SendMessage();
                                sendMessage.setChatId(String.valueOf(chatId));
                                sendMessage.setText("https://drive.google.com/file/d/1gSRM3eA5kUL6-MV19J724WptNBYjD06H/view?usp=sharing\n\nOnlayn katalog \uD83D\uDCD5");
                                executes2(sendMessage);
                                break;

                            case "Exit ↩":
                                ReplyKeyboardMarkup replyKeyboardMarkup2 = categoryService.mainMenuToUser(0);
                                executes(replyKeyboardMarkup2,null,chatId,"Tilni tanlang!\nВыберите язык!");
                                break;

                            case "Price \uD83D\uDCB5":
                                UserDataBase user4 = userService.findUser(chatId);
                                int userRole1 = user4.getUserRole();
                                int language2 = user4.getLanguage();
                                sendMessage=new SendMessage();
                                if (userRole1==3) {
                                    priceService.WriteToFile();
                                    sendDocument(chatId, new File("root/lorettouz/files/price.jpg"), "Diller price");
                                }else {
                                    if (language2==1){
                                        sendMessage.setText("Noto'g'ri buyruq");
                                    }else {
                                        sendMessage.setText("Неверная команда");
                                    }
                                    sendMessage.setChatId(String.valueOf(chatId));
                                    execute(sendMessage);
                                }
                                break;

                            case "/diller":
                                UserDataBase user3 = userService.findUser(chatId);
                                int language1 = user3.getLanguage();
                                String res="";
                                if (language1==1){
                                    res="Telefon raqamingizni yuboring! Contact tugmasiga bosing ⬇️";
                                }else {
                                    res="Отправьте свой номер телефона! Нажмите кнопку Contact ⬇️ ";
                                }
                                ReplyKeyboardMarkup userContact = buttonController.getUserContact();
                                executes(userContact,null,chatId,res);

//                                UserDataBase user3 = userService.findUser(chatId);
//                                user3.setUserRole(3);
//                                userRepository.save(user3);
//                                int language1 = user3.getLanguage();
//                                String res="";
//                                if (language1==1){
//                                    res="Kerakli bo'limni tanlang!";
//                                }else {
//                                    res="Выберите нужный раздел!";
//                                }
//                                ReplyKeyboardMarkup replyKeyboardMarkup4 = buttonController.MainMenu();
//                                executes(replyKeyboardMarkup4,null,chatId,res);
                                break;

                            case "Изменить язык":
                                ReplyKeyboardMarkup languages1 = buttonController.languages();
                                executes(languages1,null,chatId,"Выберите язык");
                                break;

                            case "Tilni o'zgartirish \uD83C\uDDFA\uD83C\uDDFF\uD83C\uDDF7\uD83C\uDDFA":
                                ReplyKeyboardMarkup languages2 = buttonController.languages();
                                executes(languages2,null,chatId,"Tilni tanlang");
                                break;
                        }
                    }
                } else {
                    userService.addUser(chatId, name, nickName, 4, 2);
                    ReplyKeyboardMarkup languages = buttonController.languages();
                    executes(languages, null, chatId, "Tilni tanlang!\nВыберите язык!");
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

        UserDataBase user = userService.findUser(chatId);
        int language = user.getLanguage();
        if (language==1){
            responseText="Kerakli bo'limni tanlang";
        }
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
        UserDataBase user = userService.findUser(chatId);
        int language = user.getLanguage();
        if (language==1){
            this.responseText="Kerakli bo'limni tanlang!";
        }
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

    public void sendLocation(SendLocation sendLocation){
        try {
            execute(sendLocation);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
