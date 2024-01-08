package com.danskebank.dummy.service.impl;

import com.danskebank.dummy.dto.*;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest request);

    String nameEnquiry(EnquiryRequest request);

    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);

    BankResponse transfer(TransferRequest request);

    BankResponse login(LoginDTO loginDTO);
}
