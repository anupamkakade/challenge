package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.dto.TransferRequest;
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


  public TransferResponse transferMoney(TransferRequest request) throws InsufficientFundInAccountException {

    String sourceAccountId = request.sourceAccountId();
    String targetAccountId = request.destinationAccountId();
    BigDecimal amount = request.amount();
    TransferResponse response = new TransferResponse(sourceAccountId,targetAccountId,amount,"Successfull Transfer");
    Account sourceAccount = accountsRepository.getAccount(sourceAccountId);
    Account targetAccount = accountsRepository.getAccount(targetAccountId);
    synchronized (sourceAccountId) {
      if (sourceAccount.getBalance().compareTo(amount) < 0) {
        throw new InsufficientFundInAccountException("Insufficient fund in  source account");
      }
    }
    /*
      Ordering of lock is crucial to prevent deadlocks.
      The below code ensures that all threads acquire locks in the same order across all transfers.
      Other approach to get the lock  at more fine grained level is to use ReentrantLock
     */
    if(sourceAccountId.compareTo(targetAccountId) > 0) {
      synchronized (sourceAccountId) {
        synchronized (targetAccountId) {
          doTransfer(sourceAccount,targetAccount,amount);
        }
      }
    } else {
      synchronized (targetAccountId) {
        synchronized (sourceAccountId) {
          doTransfer(targetAccount,sourceAccount,amount);
        }
      }
    }

    return response;
  }

  public void doTransfer(Account sourceAccount, Account targetAccount, BigDecimal amount) {
    sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
    targetAccount.setBalance(targetAccount.getBalance().add(amount));
    accountsRepository.saveAccount(sourceAccount);
    accountsRepository.saveAccount(targetAccount);
    notificationService.notifyAboutTransfer(sourceAccount, String.format("You have transferred $%.2f to account %s", amount, targetAccount.getAccountId()));
    notificationService.notifyAboutTransfer(targetAccount, String.format("You have received $%.2f from account %s", amount, sourceAccount.getAccountId()));
  }
}
