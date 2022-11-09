package pdp_g9.telegram_bot.entity.user;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pdp_g9.telegram_bot.entity.base.BaseDataBase;
import pdp_g9.telegram_bot.service.AdminState;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class UserDataBase extends BaseDataBase {

    private Long chadId;
    private String nickName;
    private int adminState;
    private int userRole;
    private int language;
}
