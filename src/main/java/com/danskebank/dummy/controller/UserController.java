package com.danskebank.dummy.controller;

import com.danskebank.dummy.dto.*;
import com.danskebank.dummy.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

@Operation(
        summary = "Create New User Account",
        description = "Creating a new user and assigning a new accountId"
)

@ApiResponse(
        responseCode = "201",
        description ="Http status 201 created"

)
    @PostMapping
    public BankResponse createAccount(@RequestBody  UserRequest userRequest){
        return userService.createAccount(userRequest);
    }


    @Operation(
            summary = "Check the balance of account ",
            description = "Check the balance of account using accountId"
    )

    @ApiResponse(
            responseCode = "200",
            description ="Http status 200 success"

    )
    @GetMapping("balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request){
        return userService.balanceEnquiry(request);
    }

    @GetMapping("nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest request){
        return userService.nameEnquiry(request);
    }

    @PostMapping("credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request){
        return userService.creditAccount(request);
    }

    @PostMapping("debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request){
        return userService.debitAccount(request);
    }

    @PostMapping("transfer")
    public BankResponse transfer(@RequestBody TransferRequest request){
        return userService.transfer(request);
    }

    @PostMapping("login")
    public BankResponse login(@RequestBody LoginDTO loginDTO)
    {
        return  userService.login(loginDTO);
    }
}
