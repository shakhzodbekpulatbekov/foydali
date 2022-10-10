package pdp_g9.userReceiveModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pdp_g9.telegram_bot.service.UserRole;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserReceiveModel {

    private String name;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String password;

    @JsonProperty("user_role")
    private UserRole userRole;

    private int age;
}
