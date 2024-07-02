package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.dto.TransferResponse;
import com.dws.challenge.exception.InsufficientFundInAccountException;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;

  @Getter
  private final NotificationService notificationService;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository,NotificationService notificationService) {
    this.accountsRepository = accountsRepository;
      this.notificationService = notificationService;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }


  public TransferResponse transferMoney(String sourceAccountId, String targetAccountId, BigDecimal amount) throws InsufficientFundInAccountException {

    TransferResponse response = new TransferResponse(sourceAccountId,targetAccountId,amount,"Successfull Transfer");
    Account sourceAccount = accountsRepository.getAccount(sourceAccountId);
    Account targetAccount = accountsRepository.getAccount(targetAccountId);

    if (sourceAccount.getBalance().compareTo(amount) < 0) {
      throw new InsufficientFundInAccountException("Insufficient fund in  source account");
    }
    // Can use AtomicReference class for thread safe operation as it uses internal thread synchronization.
    synchronized (this) {
      sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
      targetAccount.setBalance(targetAccount.getBalance().add(amount));
      accountsRepository.saveAccount(sourceAccount);
      accountsRepository.saveAccount(targetAccount);
      notificationService.notifyAboutTransfer(sourceAccount,String.format("You have transferred $%.2f to account %s", amount, targetAccountId));
      notificationService.notifyAboutTransfer(targetAccount,String.format("You have received $%.2f from account %s", amount, sourceAccountId));
    }
    return response;
  }
}
