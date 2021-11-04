package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import java.time.LocalDate;

public class BalanceOperationClass implements BalanceOperation {

    private Integer id;
    private String description;
    private double money;
    private LocalDate date;
    //Other
    private String type;

    public BalanceOperationClass(){
        this.money = 0;
        this.date = LocalDate.now();
    }

    public BalanceOperationClass(double money, String type) {
        if(money<0)
            throw new RuntimeException(new Exception());
        else
            this.money = money;
        this.date = LocalDate.now();
        if(type.equalsIgnoreCase("CREDIT") || type.equalsIgnoreCase("DEBIT"))
            this.type = type;
        else
            throw new RuntimeException(new Exception());
    }

    public BalanceOperationClass(int transactionId, String description, double money, LocalDate date, String type) {
        this.id = transactionId;
        this.description = description;
        this.money = money;
        this.date = date;
        this.type = type;
    }

    @Override
    public int getBalanceId() {
        return this.id;
    }

    @Override
    public void setBalanceId(int balanceId)  {
        if(balanceId<=0 )
            throw new RuntimeException(new InvalidTransactionIdException());
        this.id = balanceId;
    }

    @Override
    public LocalDate getDate() {
        return this.date;
    }

    @Override
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public double getMoney() {
        return this.money;
    }

    @Override
    public void setMoney(double money) {
        this.money = money;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void setType(String type) {
        if(type.equalsIgnoreCase("CREDIT") || type.equalsIgnoreCase("DEBIT"))
            this.type = type;
        else
            throw new RuntimeException(new Exception());
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String desc) throws Exception {

        if (desc.length()>1000)
            throw new Exception();
        this.description = desc;
    }


}
