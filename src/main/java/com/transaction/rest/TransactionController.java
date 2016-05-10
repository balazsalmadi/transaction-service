package com.transaction.rest;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping( TransactionController.TRANSACTION_ENDPOINT )
public class TransactionController {

    public static final String TRANSACTION_ENDPOINT = "/transaction";

    @RequestMapping( method = RequestMethod.GET )
    public @ResponseBody String greeting() {
        return "Hello world!";
    }
}
