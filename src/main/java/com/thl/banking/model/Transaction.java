package com.thl.banking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Transaction {

    private transient final AtomicBoolean isRunning;
    private String id;
    private Account from;
    private Account to;
    private BigDecimal amount;
    private LocalDateTime createdTime;
    private String currencyUnit;
    private String status;
    public Transaction() {
        isRunning = new AtomicBoolean(true);
    }

    public Transaction(Account from, Account to, BigDecimal amount, String currencyUnit) {
        this.isRunning = new AtomicBoolean(true);
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.createdTime = LocalDateTime.now();
        this.currencyUnit = currencyUnit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AtomicBoolean isRunning() {
        return isRunning;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Account from() {
        return from;
    }

    public void setFrom(Account from) {
        this.from = from;
    }

    public Account to() {
        return to;
    }

    public void setTo(Account to) {
        this.to = to;
    }

    public BigDecimal amount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCurrencyUnit() {
        return currencyUnit;
    }

    public void setCurrencyUnit(String currencyUnit) {
        this.currencyUnit = currencyUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(from, that.from) &&
                Objects.equals(to, that.to) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(currencyUnit, that.currencyUnit) &&
                Objects.equals(createdTime, that.createdTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, from, to, amount, createdTime, currencyUnit);
    }

}
