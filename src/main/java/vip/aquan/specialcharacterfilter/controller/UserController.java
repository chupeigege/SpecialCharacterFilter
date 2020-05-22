package vip.aquan.specialcharacterfilter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: wcp
 * @date: 2020/5/22 09:41
 * @Description:
 */
@RestController
public class UserController {
    @RequestMapping("/getUserInfo")
    public String getUserInfo(@RequestParam String id){

        System.out.println(id);
        return id;
    }
}
