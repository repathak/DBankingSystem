package com.danskebank.dummy.service.impl;

import com.danskebank.dummy.config.JwtTokenProvider;
import com.danskebank.dummy.dto.*;
import com.danskebank.dummy.entity.Role;
import com.danskebank.dummy.entity.Users;
import com.danskebank.dummy.repository.TransactionRepository;
import com.danskebank.dummy.repository.UserRepository;
import com.danskebank.dummy.utils.AccountUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    TransactionService  transactionService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        //CREATING A NEW SAVINGS ACCOUNT + saving new user in db

        //Check if user already have an account
        if (userRepository.existsByEmail(userRequest.getEmail())){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }


            Users newUser = Users.builder()
                    .firstName(userRequest.getFirstName())
                    .lastName(userRequest.getLastName())
                    .otherName(userRequest.getOtherName())
                    .gender(userRequest.getGender())
                    .address(userRequest.getAddress())
                    .stateOfOrigin(userRequest.getStateOfOrigin())
                    .accountNumber(AccountUtils.generateAccountNumber())
                    .accountBalance(BigDecimal.ZERO)
                    .email(userRequest.getEmail())
                    .phoneNumber(userRequest.getPhoneNumber())
                    .alternatePhoneNumber(userRequest.getAlternatePhoneNumber())
                    .status("ACTIVE")
                  // .password(userRequest.getPassword())
                    .password(passwordEncoder.encode(userRequest.getPassword()))
                    .role(Role.valueOf("ROLE_ADMIN"))
                    .build();

        Users savedUser = userRepository.save(newUser);


        //Send email Alert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations! Your Account Has been Successfully Created.\nYour Account Details: \n" +
                        "Account Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() + "\nAccount Number: " + savedUser.getAccountNumber())
                .build();
//        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " +savedUser.getLastName() +" " + savedUser.getOtherName())
                        .build())
                .build();

        }

        public BankResponse login(LoginDTO loginDTO){
            Authentication authentication =null;
authentication =authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(),loginDTO.getPassword())
);

EmailDetails loginAlert = EmailDetails.builder()
        .subject("You are logged in!")
        .recipient(loginDTO.getEmail())
        .messageBody("You logged into your account. If you did not initiate this request please contact your bank")
        .build();

//emailService.sendEmailAlert(loginAlert);
            return BankResponse.builder()
                    .responseCode("Login Success")
                    .responseMessage(jwtTokenProvider.generateToken(authentication))
                    .build();
        }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        //check if the provided account number exists in the db
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        Users foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                        .build())
                .build();
    }



    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist){
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        Users foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();
    }



    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        //checking if the account exists
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        Users userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);


        //Save Transaction

        TransactionDTO transactionDTO = TransactionDTO.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();

        transactionService.saveTransaction(transactionDTO);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getOtherName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        //check if the account exists
        //check if the amount you intend to withdraw is not more than the current account balance
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        Users userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        BigInteger availableBalance =userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();
        if ( availableBalance.intValue() < debitAmount.intValue()){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userToDebit);


            //Save in the transactions table


            TransactionDTO transactionDTO = TransactionDTO.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("DEBIT")
                    .amount(request.getAmount())
                    .build();

            transactionService.saveTransaction(transactionDTO);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(request.getAccountNumber())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .build())
                    .build();
        }

    }

    @Override
    public BankResponse transfer(TransferRequest request) {
        //get the account to debit
        //check if the balance i am withdrawing is not more than available balance
        //debit the account
        //get the account to credit
        //credit the account

        boolean  isSourceAccountExist = userRepository.existsByAccountNumber(request.getSourceAccountNumber());
        boolean isDestinationAccountExist = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if(!isDestinationAccountExist){
        return BankResponse.builder()
            .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
            .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
            .accountInfo(null)
            .build();
}
        Users sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        if(request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) < 0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(sourceAccountUser);
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .accountNumber(sourceAccountUser.getAccountNumber())
                .transactionType("DEBIT")
                .amount(request.getAmount())
                .build();

        transactionService.saveTransaction(transactionDTO);
        EmailDetails debitAlert = EmailDetails.builder()
                .subject("Debit Alert")
                .recipient(sourceAccountUser.getEmail())
                .messageBody("The sum of " + request.getAmount() + " has been deducted from  your account! Your current balance is " + sourceAccountUser.getAccountBalance())
                .build();

    //    emailService.sendEmailAlert(debitAlert);


        Users destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
        userRepository.save(destinationAccountUser);

        TransactionDTO transactionDTO1 = TransactionDTO.builder()
                .accountNumber(destinationAccountUser.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();

        transactionService.saveTransaction(transactionDTO1);

        EmailDetails creditAlert = EmailDetails.builder()
                .subject("Credit Alert")
                .recipient(sourceAccountUser.getEmail())
                .messageBody("The sum of " + request.getAmount() + " has been sent to   your account! Your current balance is " + sourceAccountUser.getAccountBalance())
                .build();

  //      emailService.sendEmailAlert(creditAlert);


        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .build();
    }


   /* public static void main(String args[]){
        UserServiceImpl userService = new UserServiceImpl();
        System.out.println(userService.passwordEncoder.encode("1234"));

    }*/
}
