package com.thl.banking.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {

    private transient final Lock lock;
    private String accountId;
    private User user;
    private BigDecimal balance;
    private String createdTime;
    private String updatedTime;
    private String currencyUnit;


    public Account() {
        this.lock = new ReentrantLock();
    }

    public Account(User user, BigDecimal balance, String createdTime, String updatedTime, String currencyUnit) {
        this.lock = new ReentrantLock();
        this.user = user;
        this.balance = balance;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.currencyUnit = currencyUnit;
    }

    public Lock lock() {
        return lock;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getCurrencyUnit() {
        return currencyUnit;
    }

    public void setCurrencyUnit(String currencyUnit) {
        this.currencyUnit = currencyUnit;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountId, account.accountId) &&
                Objects.equals(user, account.user) &&
                Objects.equals(balance, account.balance) &&
                Objects.equals(createdTime, account.createdTime) &&
                Objects.equals(currencyUnit, account.currencyUnit) &&
                Objects.equals(updatedTime, account.updatedTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, user, balance, createdTime, updatedTime, currencyUnit);
    }
}
