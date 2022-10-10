package pdp_g9.controller.userController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pdp_g9.telegram_bot.service.user.UserService;
import pdp_g9.userReceiveModel.UserReceiveModel;

@RestController
@RequestMapping("api/user")
public class UserController {

private UserService userService;

//    @PostMapping("/add")
//    public BaseResponse addUser(
//            @RequestBody UserReceiveModel userReceiveModel
//    ) {
//        return userService.addUser(userReceiveModel);
//    }



}
