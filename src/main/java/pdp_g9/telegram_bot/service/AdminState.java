package pdp_g9.telegram_bot.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AdminState {

    ADDING_CATEGORY(1),
    ADDING_MEAL(2),
    SEARCH_USER(3),
    DEFAULT(4),
    DELETING_CATEGORY(5),
    EDIT_MEAL(6),
    EDIT_CATEGORY(7),
    SENDING_AD(8),
    SEARCHMEAL_BYNAME(9),
    WRITE_USERS_TO_DB(10);


    private int adding;
}
