package pdp_g9.telegram_bot.service.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import pdp_g9.telegram_bot.entity.category.CategoryDataBase;
import pdp_g9.telegram_bot.entity.meal.MealDataBase;
import pdp_g9.telegram_bot.repository.category.CategoryRepository;
import pdp_g9.telegram_bot.repository.meal.MealRepository;
import pdp_g9.telegram_bot.repository.user.UserRepository;
import pdp_g9.telegram_bot.service.category.CategoryService;
import pdp_g9.telegram_bot.service.user.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MealService {
    final UserService userService;
    final UserRepository userRepository;
    final MealRepository mealRepository;
    final CategoryRepository categoryRepository;
    final MealDataBase mealDataBase;
    final CategoryService categoryService;

    @Autowired
    public MealService(UserService userService, UserRepository userRepository, MealRepository mealRepository, CategoryRepository categoryRepository, MealDataBase mealDataBase, CategoryService categoryService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.categoryRepository = categoryRepository;
        this.mealDataBase = mealDataBase;
        this.categoryService = categoryService;
    }

    // This method for user (list of meals in InlineKeyboardMarkup)

    public InlineKeyboardMarkup getMealDataBase(Integer categoryId, int num,int userRole,Integer lang) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(list);
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();


        List<MealDataBase> meals = mealRepository.findAll();

        List<MealDataBase> mealDataBaseList = new ArrayList<>();

        for (MealDataBase mealDataBase: meals
             ) {
            if (mealDataBase.getCategoryDataBase().getId() == categoryId) {
            mealDataBaseList.add(mealDataBase);
            }
            }


        int helper = 0;

        if (mealDataBaseList.size()!=0) {
            for (int i = num; i < mealDataBaseList.size(); i++) {

                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                if (helper == 0 && userRole==1) {
                    inlineKeyboardButton.setText("Kategoriyani taxrirlash");
                    inlineKeyboardButton.setCallbackData("EDITCATEGORY" + "-" + categoryId);
                    inlineKeyboardButtons.add(inlineKeyboardButton);

                    inlineKeyboardButton = new InlineKeyboardButton();
                    inlineKeyboardButton.setText("Maxsulot qo'shish");
                    inlineKeyboardButton.setCallbackData("ADDMEAL" + "-" + categoryId);
                    inlineKeyboardButtons.add(inlineKeyboardButton);
                    list.add(inlineKeyboardButtons);
                    inlineKeyboardButtons = new ArrayList<>();
                }

                inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText((helper + 1) + ") " +mealDataBaseList.get(i).getName());
                inlineKeyboardButton.setCallbackData("USERMEAL" + "-" + mealDataBaseList.get(i).getId());
                inlineKeyboardButtons.add(inlineKeyboardButton);
                helper++;

                if (helper % 3 == 0) {
                    list.add(inlineKeyboardButtons);
                    inlineKeyboardButtons = new ArrayList<>();

                }

                if (helper == 15) {
                    break;
                }
            }
        }
        if (!inlineKeyboardButtons.isEmpty()) {
            list.add(inlineKeyboardButtons);
            inlineKeyboardButtons = new ArrayList<>();
        }
int num1=0;
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        if (!inlineKeyboardMarkup.getKeyboard().isEmpty()) {
            if (lang==1){
                inlineKeyboardButton.setText("kategoriyaga qaytish");
            }else {
                inlineKeyboardButton.setText("Вернуться к категории");
            }

            inlineKeyboardButton.setCallbackData("BACKTOCATEGORYFROMMEALLIST"+"-"+categoryId);
            inlineKeyboardButtons.add(inlineKeyboardButton);
            list.add(inlineKeyboardButtons);
            inlineKeyboardButtons=new ArrayList<>();


            if (num == 0 && ((num + helper) < mealDataBaseList.size())) {
                inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText("⏩ next");
                inlineKeyboardButton.setCallbackData("NEX"+"-" +categoryId+"-" +(num + helper));
                inlineKeyboardButtons.add(inlineKeyboardButton);
                list.add(inlineKeyboardButtons);
            } else if (num != 0 && ((num + helper) < mealDataBaseList.size())) {
                if (num%15==0){
                    num1=num-15;
                }else if(num%(num-(num%15))==0) {
                   num1=num- ((num%15)+15);
                }
                inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText("⏪ previous");
                inlineKeyboardButton.setCallbackData("PRE"+"-" +categoryId +"-"+num1);
                inlineKeyboardButtons.add(inlineKeyboardButton);

                inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText("⏩ next");
                inlineKeyboardButton.setCallbackData("NEX" +"-"+categoryId +"-"+(num + helper));
                inlineKeyboardButtons.add(inlineKeyboardButton);
                list.add(inlineKeyboardButtons);
            } else if (num != 0 && ((num + helper) == mealDataBaseList.size())) {
                if (num%15==0){
                    num1=num-15;
                }else {
                    num1=(num%15);
                }
                inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText("⏪ previous");
                inlineKeyboardButton.setCallbackData("PRE"+"-"+categoryId+"-" + num1);
                inlineKeyboardButtons.add(inlineKeyboardButton);
                list.add(inlineKeyboardButtons);
            }
        }


        return inlineKeyboardMarkup;
    }

    // This method for admin (adding meal)


    public boolean addMeal(String name, String description, int categoryId, byte[] array, String videoUrl, String photoName, Long chatId) throws IOException {
        userService.setAdminStatus(4, chatId);
        MealDataBase mealDataBase = new MealDataBase();
        CategoryDataBase categoryDataBase = new CategoryDataBase();
        boolean isHas = false;
        categoryDataBase.setId(categoryId);
        List<MealDataBase> all = mealRepository.findAll();
        if (all.size() != 0) {
            for (MealDataBase meal : all) {
                if (meal.getName().equalsIgnoreCase(name) && meal.getCategoryDataBase().getId() == categoryId)
                    isHas = true;
            }
        }
        if (!isHas) {
            mealDataBase.setName(name);
            mealDataBase.setDescription(description);
            mealDataBase.setCategoryDataBase(categoryDataBase);
            mealDataBase.setPhotoByte(array);
            mealDataBase.setVideoUrl(videoUrl);
            mealDataBase.setPhotoName(photoName);
            mealRepository.save(mealDataBase);
        }
        return isHas;


    }


    //This method for sending meal to user

    public MealDataBase getMeal(int mealId) {
        MealDataBase mealDataBase = new MealDataBase();
        Optional<MealDataBase> byId = mealRepository.findById(mealId);
        if (byId.isPresent()) {
            mealDataBase = byId.get();
        }
        return mealDataBase;
    }

    public InlineKeyboardMarkup workWithMeal(int id, int status,int lang) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(list);

        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton =new InlineKeyboardButton();
        if (lang==1){
            inlineKeyboardButton.setText("Berkitish❌");
        }else {
            inlineKeyboardButton.setText("Закрытие❌");
        }
        inlineKeyboardButton.setCallbackData("BACKFROMMEALTOCATEGORY"+"-"+id);
        inlineKeyboardButtons.add(inlineKeyboardButton);

         inlineKeyboardButton =new InlineKeyboardButton();
        if (lang==1){
            inlineKeyboardButton.setText("↩ ortga");
        }else {
            inlineKeyboardButton.setText("↩ Назад");
        }
        inlineKeyboardButton.setCallbackData("Back"+"-"+id);
        inlineKeyboardButtons.add(inlineKeyboardButton);

        if (status==1) {
            inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText("M_Taxrirlash");
            inlineKeyboardButton.setCallbackData("EDITMEAL" + "-" + id);
            inlineKeyboardButtons.add(inlineKeyboardButton);

            inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText("M_o'chirish");
            inlineKeyboardButton.setCallbackData("MEALDELETE" + "-" + id);
            inlineKeyboardButtons.add(inlineKeyboardButton);
        }
        list.add(inlineKeyboardButtons);
        return inlineKeyboardMarkup;
    }


    public void deleteMeal(int id) {
        Optional<MealDataBase> byId = mealRepository.findById(id);

        byId.ifPresent(mealRepository::delete);
    }

    public boolean editMeal(String name, String description, byte[] array, String videoUrl, String photoName, int id) {
        Optional<MealDataBase> byId = mealRepository.findById(id);
        boolean isEdited = false;
        if (byId.isPresent()) {
            byId.get().setName(name);
            byId.get().setDescription(description);
            byId.get().setPhotoByte(array);
            byId.get().setVideoUrl(videoUrl);
            byId.get().setPhotoName(photoName);
            mealRepository.save(byId.get());
            isEdited = true;
        }

        return isEdited;
    }

    public InlineKeyboardMarkup backToCategory(int id,int userRole){
        Optional<MealDataBase> byId = mealRepository.findById(id);
        int categoryId = 0;
        if (byId.isPresent()){
           categoryId= byId.get().getCategoryDataBase().getId();
        }

        return getMealDataBase(categoryId,0,userRole,1);
    }

    public InlineKeyboardMarkup searchByName(String text, Long chatId){
        List<MealDataBase> all = mealRepository.findAll();
        List<MealDataBase> byName=new ArrayList<>();

        for (MealDataBase mealDataBase: all
             ) {
            if (mealDataBase.getName().toUpperCase().contains(text.toUpperCase())){
                byName.add(mealDataBase);
            }
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> list=new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(list);
        List<InlineKeyboardButton> buttons=new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();

        if (!byName.isEmpty()){
            for (MealDataBase meal: byName
                 ) {
                inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(meal.getName());
                inlineKeyboardButton.setCallbackData("USERMEAL-"+meal.getId());
                buttons.add(inlineKeyboardButton);
                list.add(buttons);
                buttons=new ArrayList<>();
            }
        }

        return inlineKeyboardMarkup;
    }
}
