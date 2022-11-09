package pdp_g9.telegram_bot.service.user;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import pdp_g9.telegram_bot.entity.user.UserDataBase;
import pdp_g9.telegram_bot.repository.user.UserRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    final
    UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean addUser(Long chadId, String name, String nickName, int adminState, int userRole) {
        Optional<UserDataBase> users = userRepository.findByChadId(chadId);
        boolean user = false;
        if (!users.isPresent()) {
            UserDataBase userDataBase = new UserDataBase();
            userDataBase.setChadId(chadId);
            userDataBase.setName(name);
            userDataBase.setNickName(nickName);
            userDataBase.setAdminState(adminState);
            userDataBase.setUserRole(userRole);
            userRepository.save(userDataBase);
            user = true;
        } else {
            Optional<UserDataBase> byChadId = userRepository.findByChadId(chadId);
            if (byChadId.isPresent()) {
                UserDataBase userDataBase = byChadId.get();
                userDataBase.setAdminState(1);
                userRepository.save(userDataBase);
                user = true;
            }
        }
        return user;
    }

public Integer getUserRole(Long chatId){
    int userRole = 0;
    Optional<UserDataBase> byChadId = userRepository.findByChadId(chatId);

    if (byChadId.isPresent())
       userRole= byChadId.get().getUserRole();

    return userRole;
}


    public Integer getStatus(Long chatId) {
        int state = 0;
        Optional<UserDataBase> byChadId = userRepository.findByChadId(chatId);
        if (byChadId.isPresent())
            state=byChadId.get().getAdminState();

        return state;
    }

    public void setAdminStatus(Integer state, Long chatId) {

        List<UserDataBase> users = userRepository.findAll();
        boolean isTrue = false;
        for (int i = 0; i < users.size() && (!isTrue); i++) {
            if (users.get(i).getChadId().equals(chatId)) {
                users.get(i).setAdminState(state);
                isTrue = true;
                userRepository.save(users.get(i));
            }
        }

    }

    public InlineKeyboardMarkup userList() {
        List<UserDataBase> userList = userRepository.findAll();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(list);

        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        int helper = 0;
        for (UserDataBase userDataBase : userList
        ) {
            helper++;
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(userDataBase.getName());
            inlineKeyboardButton.setCallbackData("USRERS" +"-"+ userDataBase.getId());
            inlineKeyboardButtons.add(inlineKeyboardButton);

            if (helper % 2 == 0) {
                list.add(inlineKeyboardButtons);
                inlineKeyboardButtons = new ArrayList<>();
            }
        }

        if (!inlineKeyboardButtons.isEmpty()) {
            list.add(inlineKeyboardButtons);
        }
        return inlineKeyboardMarkup;
    }

    public void addAdmin(int id) {
        List<UserDataBase> userList = userRepository.findAll();

        for (UserDataBase user : userList
        ) {
            if (user.getId() == id) {
                user.setUserRole(1);
                userRepository.save(user);
            }
        }
    }

    public UserDataBase isHasUser(Long chatId) {
        List<UserDataBase> all = userRepository.findAll();

        UserDataBase userDataBase = new UserDataBase();
        for (UserDataBase userDataBase1 : all
        ) {
            if (userDataBase1.getChadId().equals(chatId))
                return userDataBase1;
        }
        return null;
    }

    public InlineKeyboardMarkup setAdmin(String name) {
        List<UserDataBase> userRepositoryAll = userRepository.findAll();
        List<UserDataBase> usersToInlineKeyboard = new ArrayList<>();
        for (UserDataBase userDataBase : userRepositoryAll
        ) {
            if (userDataBase.getName().toUpperCase().contains(name)) {
                usersToInlineKeyboard.add(userDataBase);
            }
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(buttons);

        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        int helper = 0;
        for (UserDataBase users :
                usersToInlineKeyboard
        ) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(users.getName() + " " + users.getNickName());
            inlineKeyboardButton.setCallbackData("ADDADMINBYNAME" +"-"+ users.getId());
            inlineKeyboardButtons.add(inlineKeyboardButton);
            helper++;

            if (helper % 2 == 0) {
                buttons.add(inlineKeyboardButtons);
                buttons = new ArrayList<>();
            } else if (helper == usersToInlineKeyboard.size() - 1) {
                buttons.add(inlineKeyboardButtons);
                buttons = new ArrayList<>();

            }
        }
        if (inlineKeyboardButtons.size() != 0) {
            buttons.add(inlineKeyboardButtons);
        }
        helper = 0;
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup searchUser() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(buttons);
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("List orqali qidirish");
        inlineKeyboardButton.setCallbackData("LIST");
        inlineKeyboardButtons.add(inlineKeyboardButton);

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Ism orqali qidirish");
        inlineKeyboardButton1.setCallbackData("SEARCHBYNAME");
        inlineKeyboardButtons.add(inlineKeyboardButton1);
        buttons.add(inlineKeyboardButtons);
        inlineKeyboardButtons=new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Adminlar ro'yxati");
        inlineKeyboardButton2.setCallbackData("LISTOFADMINS");
        inlineKeyboardButtons.add(inlineKeyboardButton2);

        inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Hozirgi vaqtdagi faol foydalanuvchilar soni");
        inlineKeyboardButton2.setCallbackData("NUMBEROFUSERS");
        inlineKeyboardButtons.add(inlineKeyboardButton2);

        inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Delete all");
        inlineKeyboardButton2.setCallbackData("DeleteAll");
        inlineKeyboardButtons.add(inlineKeyboardButton2);


        buttons.add(inlineKeyboardButtons);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup listOfAdmins() {
        List<UserDataBase> all = userRepository.findAll();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(list);
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

        for (UserDataBase userDataBase : all
        ) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            if (userDataBase.getUserRole() == 1) {
                inlineKeyboardButton.setText(userDataBase.getName());
                inlineKeyboardButton.setCallbackData("ADMINS" +"-"+ userDataBase.getId());
                inlineKeyboardButtons.add(inlineKeyboardButton);
                list.add(inlineKeyboardButtons);
                inlineKeyboardButtons = new ArrayList<>();
            }

        }
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup workWithAdmin(int id) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(list);
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("↩ Назад");
        inlineKeyboardButton.setCallbackData("EXITTOADMINLIST");
        inlineKeyboardButtons.add(inlineKeyboardButton);

        inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Adminlikdan bo'shatish");
        inlineKeyboardButton.setCallbackData("REMOVEADMIN" +"-"+ id);
        inlineKeyboardButtons.add(inlineKeyboardButton);

        list.add(inlineKeyboardButtons);

        return inlineKeyboardMarkup;
    }

    public boolean removeAdmin(int id) {

        Optional<UserDataBase> byId = userRepository.findById(id);
        boolean isBek = false;
        boolean isHasUser = false;
        UserDataBase userDataBase = new UserDataBase();
        if (byId.isPresent()) {
            userDataBase = byId.get();
        }
        if (userDataBase.getChadId() == 915145143)
            isBek = true;

        if (!isBek) {
            userDataBase.setUserRole(2);
            userRepository.save(userDataBase);
            isHasUser = true;
        }
        return isHasUser;
    }

    public String getUserName(Long chatId){
        Optional<UserDataBase> byChadId = userRepository.findByChadId(chatId);
        String name="";
        if (byChadId.isPresent())
            name=byChadId.get().getName();
            return name;
    }

    public boolean addBek(Long chatId){
        Optional<UserDataBase> byChadId = userRepository.findByChadId(chatId);
        return byChadId.isPresent()?true:false;
    }

    public String numberOfUsers(){
        List<UserDataBase> all = userRepository.findAll();
        String str="Hozirgi vaqtdagi faol foydalanuvchilar soni\n"+all.size();
        return str;
    }

    public void deleteUser(){
        userRepository.deleteAll();
    }

    public UserDataBase findUser(Long chatId) {
        Optional<UserDataBase> byChadId = userRepository.findByChadId(chatId);

        return byChadId.orElse(null);
    }

}
