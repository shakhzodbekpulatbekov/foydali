package pdp_g9.telegram_bot.service;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum UserRole {
    ADMIN(1),
    USER(2);

    private int userRole;


}
