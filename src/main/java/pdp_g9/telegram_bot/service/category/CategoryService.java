package pdp_g9.telegram_bot.service.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import pdp_g9.telegram_bot.entity.category.CategoryDataBase;
import pdp_g9.telegram_bot.entity.user.UserDataBase;
import pdp_g9.telegram_bot.repository.category.CategoryRepository;
import pdp_g9.telegram_bot.service.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    final CategoryRepository categoryRepository;
    final UserService userService;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, UserService userService) {
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    public List<CategoryDataBase> getList() {
        return categoryRepository.findAll();
    }


    public InlineKeyboardMarkup categoryList(int num, int parentId) {

        List<CategoryDataBase> categoryDataBaseList = getList();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(list);

        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        List<CategoryDataBase> categoryDataBaseList1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();

        for (CategoryDataBase categories : categoryDataBaseList
        ) {
            if (parentId == categories.getParentId()) {
                categoryDataBaseList1.add(categories);
            }

        }
        int helper = 0;
        if (categoryDataBaseList1.size() != 0) {
            for (int i = num; i < categoryDataBaseList1.size(); i++) {

                inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(categoryDataBaseList1.get(i).getName());
                inlineKeyboardButton.setCallbackData("CATEGORIYA" + "-" + categoryDataBaseList1.get(i).getId() + "-" + (helper + num));
                inlineKeyboardButtons.add(inlineKeyboardButton);
                helper++;
                if (helper % 2 == 0) {
                    list.add(inlineKeyboardButtons);
                    inlineKeyboardButtons = new ArrayList<>();
                }
                if (helper == 10) {
                    break;
                }
            }
        }
        if (!inlineKeyboardButtons.isEmpty()) {
            list.add(inlineKeyboardButtons);
            inlineKeyboardButtons = new ArrayList<>();
        }
        if (categoryDataBaseList1.size() != 0) {
            if (categoryDataBaseList1.get(0).getParentId() != 0) {
                inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText("↩ Назад");
                inlineKeyboardButton.setCallbackData("BACKTOPARENTCATEGORY" + "-" + parentId);
                inlineKeyboardButtons.add(inlineKeyboardButton);
                list.add(inlineKeyboardButtons);
                inlineKeyboardButtons = new ArrayList<>();
            }

            inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText("Asosiy kategoriya sifatida qo'shish!");
            inlineKeyboardButton.setCallbackData("ADDCATEGORY" + "-" + 0);
            inlineKeyboardButtons.add(inlineKeyboardButton);
            list.add(inlineKeyboardButtons);
            inlineKeyboardButtons = new ArrayList<>();

            int num1 = 0;
            if (!inlineKeyboardMarkup.getKeyboard().isEmpty()) {
                if (num == 0 && ((num + helper) < categoryDataBaseList1.size())) {
                    inlineKeyboardButton = new InlineKeyboardButton();
                    inlineKeyboardButton.setText("⏩ next");
                    inlineKeyboardButton.setCallbackData("PARENTNEXT" + "-" + parentId + "-" + (num + helper));
                    inlineKeyboardButtons.add(inlineKeyboardButton);
                    list.add(inlineKeyboardButtons);
                } else if (num != 0 && ((num + helper) < categoryDataBaseList1.size())) {
                    if (num % 10 == 0) {
                        num1 = num - 10;
                    } else if (num % (num - (num % 10)) == 0) {
                        num1 = num - ((num % 10) + 10);
                    }
                    inlineKeyboardButton = new InlineKeyboardButton();
                    inlineKeyboardButton.setText("⏪ previous");
                    inlineKeyboardButton.setCallbackData("PARENTPREVIOUS" + "-" + parentId + "-" + num1);
                    inlineKeyboardButtons.add(inlineKeyboardButton);

                    inlineKeyboardButton = new InlineKeyboardButton();
                    inlineKeyboardButton.setText("⏩ next");
                    inlineKeyboardButton.setCallbackData("PARENTNEXT" + "-" + parentId + "-" + (num + helper));
                    inlineKeyboardButtons.add(inlineKeyboardButton);
                    list.add(inlineKeyboardButtons);
                } else if (num != 0 && ((num + helper) == categoryDataBaseList1.size())) {
                    if (num % 10 == 0) {
                        num1 = num - 10;
                    } else {
                        num1 = (num % 10);
                    }
                    inlineKeyboardButton = new InlineKeyboardButton();
                    inlineKeyboardButton.setText("⏪ previous");
                    inlineKeyboardButton.setCallbackData("PARENTPREVIOUS" + "-" + parentId + "-" + num1);
                    inlineKeyboardButtons.add(inlineKeyboardButton);
                    list.add(inlineKeyboardButtons);
                }
            }
        }

        return inlineKeyboardMarkup;
    }

    //This method for admin (adding category)
    public boolean addCategory(String category, int parentId, long chatId) {
        CategoryDataBase categoryDataBase = new CategoryDataBase();
        userService.setAdminStatus(4, chatId);
        List<CategoryDataBase> categoryList = categoryRepository.findAll();
        boolean isAdded = false;
        categoryDataBase.setName(category);
        categoryDataBase.setParentId(parentId);
        if (categoryList.size() != 0) {
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getId() == parentId) {

                    for (int j = 0; j < categoryList.size(); j++) {

                        if (categoryList.get(j).getName().equals(category)) {
                            isAdded = true;
                        }
                    }
                    if (!isAdded)
                        try {
                            categoryRepository.save(categoryDataBase);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                } else if (parentId == 0) {
                    for (int j = 0; j < categoryList.size(); j++) {

                        if (categoryList.get(j).getName().equals(category)) {
                            isAdded = true;
                        }
                    }
                    if (!isAdded)
                        try {
                            categoryRepository.save(categoryDataBase);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
            }
        } else categoryRepository.save(categoryDataBase);
        return isAdded;
    }

    //This method for admin (list of category)

    public List<String> listToString() {

        ArrayList<String> listArray = new ArrayList<>();
        List<CategoryDataBase> list = categoryRepository.findAll();
        for (CategoryDataBase categorys : list
        ) {
            listArray.add(categorys.getName() + " (id: " + categorys.getId() + "  P-id: " + categorys.getParentId() + ")");
        }

        return listArray;

    }


    //This method for user (list of category in InlineKeyboardMarkup)
    public InlineKeyboardMarkup listToUser(int parentId, int num,Long chatId) {
        List<CategoryDataBase> categoryDataBaseList = getList();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(list);

        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        List<CategoryDataBase> categoryDataBaseList1 = new ArrayList<>();

        for (CategoryDataBase categories : categoryDataBaseList
        ) {
            if (parentId == categories.getParentId()) {
                categoryDataBaseList1.add(categories);
            }

        }
        int helper = 0;
        if (categoryDataBaseList1.size() != 0) {
            for (int i = num; i < categoryDataBaseList1.size(); i++) {

                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(categoryDataBaseList1.get(i).getName());
                inlineKeyboardButton.setCallbackData("LISTTOUSER" + "-" + categoryDataBaseList1.get(i).getId() + "-" + (helper + num));
                inlineKeyboardButtons.add(inlineKeyboardButton);
                helper++;
                list.add(inlineKeyboardButtons);
                inlineKeyboardButtons=new ArrayList<>();
//                if (helper % 2 == 0) {
//                    list.add(inlineKeyboardButtons);
//                    inlineKeyboardButtons = new ArrayList<>();
//                }
                if (helper == 10) {
                    break;
                }
            }
        }
        if (!inlineKeyboardButtons.isEmpty()) {
            list.add(inlineKeyboardButtons);
            inlineKeyboardButtons = new ArrayList<>();
        }
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();

        int num1 = 0;
        if (!inlineKeyboardMarkup.getKeyboard().isEmpty()) {
            UserDataBase user = userService.findUser(chatId);
            int lang=user.getLanguage();
            if (parentId != 0) {
                inlineKeyboardButton.setText("↩ Назад");
                inlineKeyboardButton.setCallbackData("BACKTOPARENTCATEGORY" + "-" + parentId);
                inlineKeyboardButtons.add(inlineKeyboardButton);
                list.add(inlineKeyboardButtons);
                inlineKeyboardButtons = new ArrayList<>();
            }
            if (num == 0 && ((num + helper) < categoryDataBaseList1.size())) {
                inlineKeyboardButton = new InlineKeyboardButton();
                if (lang==1){
                    inlineKeyboardButton.setText("⏩ keyingi");
                }else {
                    inlineKeyboardButton.setText("⏩ следующий");
                }

                inlineKeyboardButton.setCallbackData("USERNEXT" + "-" + parentId + "-" + (num + helper));
                inlineKeyboardButtons.add(inlineKeyboardButton);
                list.add(inlineKeyboardButtons);
            } else if (num != 0 && ((num + helper) < categoryDataBaseList1.size())) {
                if (num % 10 == 0) {
                    num1 = num - 10;
                } else if (num % (num - (num % 10)) == 0) {
                    num1 = num - ((num % 10) + 10);
                }
                inlineKeyboardButton = new InlineKeyboardButton();
                if (lang==1){
                    inlineKeyboardButton.setText("⏪ avvalgi");
                }else {
                    inlineKeyboardButton.setText("⏪ предыдущий");
                }

                inlineKeyboardButton.setCallbackData("USERPREVIOUS" + "-" + parentId + "-" + num1);
                inlineKeyboardButtons.add(inlineKeyboardButton);

                inlineKeyboardButton = new InlineKeyboardButton();
                if (lang==1){
                    inlineKeyboardButton.setText("⏩ keyingi");
                }else {
                    inlineKeyboardButton.setText("⏩ следующий");
                }
                inlineKeyboardButton.setCallbackData("USERNEXT" + "-" + parentId + "-" + (num + helper));
                inlineKeyboardButtons.add(inlineKeyboardButton);
                list.add(inlineKeyboardButtons);
            } else if (num != 0 && ((num + helper) == categoryDataBaseList1.size())) {
                if (num % 10 == 0) {
                    num1 = num - 10;
                } else {
                    num1 = (num % 10);
                }
                inlineKeyboardButton = new InlineKeyboardButton();
                if (lang==1){
                    inlineKeyboardButton.setText("⏪ avvalgi");
                }else {
                    inlineKeyboardButton.setText("⏪ предыдущий");
                }
                inlineKeyboardButton.setCallbackData("USERPREVIOUS" + "-" + parentId + "-" + num1);
                inlineKeyboardButtons.add(inlineKeyboardButton);
                list.add(inlineKeyboardButtons);
            }
        }


        return inlineKeyboardMarkup;
    }

    // This method for admin it's used to list of meal in meal Service
    public CategoryDataBase getCategoryName(int id) {
        Optional<CategoryDataBase> byId = categoryRepository.findById(id);
        return byId.orElse(null);
    }

    //This method deleted category by id
    public boolean deleteCategory(int id) {
        Optional<CategoryDataBase> byId = categoryRepository.findById(id);

        if (byId.isPresent()) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public ReplyKeyboardMarkup mainMenuToUser() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton("Продукция \uD83D\uDCE6"));
        keyboardRow.add(new KeyboardButton("Контакты \uD83D\uDCF1"));
        KeyboardRow keyboardRow1=new KeyboardRow();
        keyboardRow1.add(new KeyboardButton("Наши сервисные центры \uD83D\uDEE0"));
        keyboardRow1.add(new KeyboardButton("О нас \uD83D\uDD35"));
        KeyboardRow keyboardRow2=new KeyboardRow();
//        keyboardRow1.add(new KeyboardButton("Exit ↩"));
        keyboardRow2.add(new KeyboardButton("Hаши магазины \uD83C\uDFEA"));
        keyboardRow2.add(new KeyboardButton("Oнлайн каталог \uD83D\uDCD5"));
        keyboardRows.add(keyboardRow);
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        return replyKeyboardMarkup;

    }

    public ReplyKeyboardMarkup mainMenuToUserUZ() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton("Maxsulotlar \uD83D\uDCE6"));
        keyboardRow.add(new KeyboardButton("Aloqa \uD83D\uDCF1"));
        KeyboardRow keyboardRow1=new KeyboardRow();
        keyboardRow1.add(new KeyboardButton("Servis markazi \uD83D\uDEE0"));
        keyboardRow1.add(new KeyboardButton("Biz haqimizda \uD83D\uDD35"));
        KeyboardRow keyboardRow2=new KeyboardRow();
//        keyboardRow1.add(new KeyboardButton("Exit ↩"));
        keyboardRow2.add(new KeyboardButton("Bizning do'konlar \uD83C\uDFEA"));
        keyboardRow2.add(new KeyboardButton("Onlayn katalog \uD83D\uDCD5"));
        keyboardRows.add(keyboardRow);
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        return replyKeyboardMarkup;

    }

    public boolean editCategory(int categoryId, String name) {
        boolean isEdited = false;
        Optional<CategoryDataBase> byId = categoryRepository.findById(categoryId);
        if (byId.isPresent()) {
            byId.get().setName(name);
            categoryRepository.save(byId.get());
            isEdited = true;
        }
        return isEdited;
    }

    public InlineKeyboardMarkup childCategory(int parentId, int num) {

        List<CategoryDataBase> categoryDataBaseList = getList();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(list);

        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        List<CategoryDataBase> categoryDataBases = new ArrayList<>();
        int helper = 0;
        for (int i = 0; i < categoryDataBaseList.size(); i++) {
            if (parentId == categoryDataBaseList.get(i).getParentId()) {
                categoryDataBases.add(categoryDataBaseList.get(i));
            }
        }

        if (!categoryDataBases.isEmpty()) {
            for (int i = 0; i < categoryDataBases.size(); i++) {


                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(categoryDataBases.get(i).getName());
                inlineKeyboardButton.setCallbackData("CATEGORIYA" + "-" + categoryDataBases.get(i).getId() + "-" + (helper + num));
                inlineKeyboardButtons.add(inlineKeyboardButton);
                helper++;
                if (helper % 2 == 0) {
                    list.add(inlineKeyboardButtons);
                    inlineKeyboardButtons = new ArrayList<>();
                }
                if (helper == 10) {
                    break;
                }
            }
        }

        if (!inlineKeyboardButtons.isEmpty()) {
            list.add(inlineKeyboardButtons);
            inlineKeyboardButtons = new ArrayList<>();
        }
        if (!inlineKeyboardMarkup.getKeyboard().

                isEmpty()) {

            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            if (parentId != 0) {
                inlineKeyboardButton.setText("↩ Назад");
                inlineKeyboardButton.setCallbackData("BACKTOPARENTCATEGORY" + "-" + parentId);
                inlineKeyboardButtons.add(inlineKeyboardButton);
                list.add(inlineKeyboardButtons);
                inlineKeyboardButtons = new ArrayList<>();
            }

            if (num == 0 && categoryDataBases.size() > 10) {
                inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText("⏩");
                inlineKeyboardButton.setCallbackData("NEX" + "-" + (num + helper));
                inlineKeyboardButtons.add(inlineKeyboardButton);
                list.add(inlineKeyboardButtons);
            } else if ((num + helper) > 10 && (num + helper) < categoryDataBaseList.size()) {

                inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText("⏪");
                inlineKeyboardButton.setCallbackData("PRE" + "-" + (num + helper));
                inlineKeyboardButtons.add(inlineKeyboardButton);
                inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText("⏩");
                inlineKeyboardButton.setCallbackData("NEX" + "-" + (num + helper));
                inlineKeyboardButtons.add(inlineKeyboardButton);
                list.add(inlineKeyboardButtons);
            } else if ((helper + num) > 10 && (helper + num == categoryDataBaseList.size())) {
                inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText("⏪");
                inlineKeyboardButton.setCallbackData("PRE" + "-" + (num + helper));
                inlineKeyboardButtons.add(inlineKeyboardButton);
                list.add(inlineKeyboardButtons);
            }
        }

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup findParentCategory(int id) {
        Optional<CategoryDataBase> byId = categoryRepository.findById(id);

        return categoryList(0, byId.get().getParentId());
    }


    public int findParentId(int id) {
        Optional<CategoryDataBase> byId = categoryRepository.findById(id);
        return byId.map(CategoryDataBase::getParentId).orElse(0);
    }
}

