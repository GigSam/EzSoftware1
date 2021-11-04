package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidOrderIdException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AccountBookClass implements AccountBook {
    //Our Design

    private double balance;
    private final Map<Integer, SaleTransaction> saleTransactionMap = new HashMap<>();
    private final Map<Integer, Order> orderMap = new HashMap<>();
    private final Map<Integer, ReturnTransaction> returnTransactionMap = new HashMap<>();
    private final Map<Integer, BalanceOperation> balanceOperationMap = new HashMap<>();

    // Account Book Default Constructor
    public AccountBookClass(double balance) {
        if (balance >= 0)
            this.balance = balance;
        else this.balance = 0;
    }

    // Already existed Account Book
    @SuppressWarnings("unchecked")
    public AccountBookClass(Map<Integer, SaleTransaction> SalOp, Map<Integer, Order> OrdOp, Map<Integer, ReturnTransaction> RetOp, Map<Integer, BalanceOperation> BalOp) {

        this.saleTransactionMap.putAll(SalOp);
        this.orderMap.putAll(OrdOp);
        this.returnTransactionMap.putAll(RetOp);
        this.balanceOperationMap.putAll(BalOp);
        /*for (Map.Entry<Integer, Order> entry : orderMap.entrySet()) {
            balanceOperationMap.put(entry.getKey(), (BalanceOperation) entry.getValue());
        }
        for (Map.Entry<Integer, ReturnTransaction> entry : returnTransactionMap.entrySet()) {
            balanceOperationMap.put(entry.getKey(), (BalanceOperation) entry.getValue());
        }
        for (Map.Entry<Integer, SaleTransaction> entry : saleTransactionMap.entrySet()) {
            balanceOperationMap.put(entry.getKey(), (BalanceOperation) entry.getValue());
        }*/
        //compute balance
        this.balance = balanceOperationMap.values().stream().filter(b->b.getType().equals("CREDIT")).mapToDouble(b->b.getMoney()).sum();
        this.balance -= balanceOperationMap.values().stream().filter(b->b.getType().equals("DEBIT")).mapToDouble(b->b.getMoney()).sum();
        //this.balance = Connect.getBalance();
       /* double CREDIT = this.saleTransactionMap.values().stream().
                filter(saleTransaction -> ((SaleTransactionClass)saleTransaction).getStatus().toString().equals("PAYED")).
                mapToDouble(SaleTransaction::getPrice).sum();
        double DEBIT = this.orderMap.values().stream().
                filter(order -> ((OrderClass)order).getStatus().equals("PAYED")).
                mapToDouble(order -> ((OrderClass) order).getMoney()).sum();
        this.balance = CREDIT -DEBIT;

        // SET INITIAL BALANCE
        if(!balanceOperationMap.isEmpty()){
            double newBalance;
            //IS BALANCE MONEY VALUE ALWAYS SET TO A CORRECT VALUE?
            //It's works if the saleTransaction doesn't update after returnTransaction
            double CREDIT = this.balanceOperationMap.values().stream().
                    filter(balanceOperation -> balanceOperation.getType().equals("CREDIT")).mapToDouble(BalanceOperation::getMoney).sum();
            double DEBIT = this.balanceOperationMap.values().stream().
                    filter(balanceOperation -> ((BalanceOperationClass)balanceOperation).getDescription().equals("ORDER")).mapToDouble(BalanceOperation::getMoney).sum();
            newBalance = CREDIT - DEBIT;
            this.balance = newBalance;
            */

    }

    public boolean addBalanceOperation(BalanceOperation bo) {
    	if (balanceOperationMap.containsKey(bo.getBalanceId()))
    		return false;
    	double tmp = balance;
    	balance += bo.getType().equals("CREDIT")?bo.getMoney():-bo.getMoney();
    	if(balance < 0) {
    		balance = tmp;
    		return false;
    	}
        balanceOperationMap.put(bo.getBalanceId(), bo);
        // update balance
        Connect.addBalanceOperation((BalanceOperationClass) bo);
        return true;
    }
    @Override
    public Integer addSaleTransaction(SaleTransaction saleTransaction) {
        if(saleTransaction==null || (saleTransaction.getTicketNumber()!=-1 && saleTransaction.getTicketNumber()!=null))
            throw new RuntimeException();
        Integer newId = newId();
        saleTransaction.setTicketNumber(newId);
        this.saleTransactionMap.put(newId, saleTransaction);
        return newId;
    }

    @Override
    public Integer addReturnTransaction(ReturnTransaction returnTransaction) {
        if(returnTransaction==null || (returnTransaction.getReturnId()!=-1 && returnTransaction.getReturnId()!=null))
            throw new RuntimeException();
        Integer newId = newId();
        returnTransaction.setReturnId(newId);
        this.returnTransactionMap.put(newId, returnTransaction);
        return newId;

    }

    @Override
    public Integer addOrder(Order order) {
        if(order==null || (order.getOrderId()!=-1 && order.getOrderId()!=null))
            throw new RuntimeException();
        Integer newId = newId();
        order.setOrderId(newId);
        this.orderMap.put(newId, order);

        return newId;
    }


    //In Design Transaction Objects are passed
    @Override
    public void removeSaleTransaction(Integer saleTransactionId) throws InvalidTransactionIdException {

        if (!this.saleTransactionMap.containsKey(saleTransactionId) || saleTransactionId == null || saleTransactionId <= 0)
            throw new InvalidTransactionIdException();

        this.saleTransactionMap.remove(saleTransactionId);
        Connect.removeSaleTransaction(saleTransactionId);
        this.balanceOperationMap.remove(saleTransactionId);
        Connect.removeBalanceOperation(saleTransactionId);
    }

    @Override
    public void removeReturnTransaction(Integer returnTransactionId) throws InvalidTransactionIdException {
        if (!this.returnTransactionMap.containsKey(returnTransactionId) || returnTransactionId == null || returnTransactionId <= 0)
            throw new InvalidTransactionIdException();

        this.returnTransactionMap.remove(returnTransactionId);
        Connect.deleteReturnTransaction(returnTransactionId);
        //this.balanceOperationMap.remove(returnTransactionId);
        //Connect.removeBalanceOperation(returnTransactionId);

    }

    @Override
    public void removeOrder(Integer orderTransactionId) throws InvalidTransactionIdException {
        if (orderTransactionId == null || orderTransactionId <= 0 || !this.orderMap.containsKey(orderTransactionId))
            throw new InvalidTransactionIdException();
        this.orderMap.remove(orderTransactionId);
        Connect.removeOrder(orderTransactionId);
        this.balanceOperationMap.remove(orderTransactionId);
        Connect.removeBalanceOperation(orderTransactionId);

    }

    @Override
    public SaleTransaction getSaleTransaction(Integer id) {
        if (id == null || id <= 0 || !this.saleTransactionMap.containsKey(id))
            throw new RuntimeException(new InvalidTransactionIdException());

        return this.saleTransactionMap.get(id);
    }

    @Override
    public ReturnTransaction getReturnTransaction(Integer id) {
        if (!this.returnTransactionMap.containsKey(id) || id == null || id <= 0)
            throw new RuntimeException(new InvalidTransactionIdException());

        return this.returnTransactionMap.get(id);
    }

    @Override
    public Order getOrder(Integer id) {
        if (!this.orderMap.containsKey(id) || id == null || id <= 0)
            throw new RuntimeException(new InvalidTransactionIdException());
        return this.orderMap.get(id);
    }

    public BalanceOperation getBalanceOperation(Integer id) {
        if (!this.balanceOperationMap.containsKey(id) || id == null || id <= 0)
            throw new RuntimeException(new InvalidTransactionIdException());
        return this.balanceOperationMap.get(id);
    }

    @Override
    public Double getBalance() {
        return this.balance;
    }

    public boolean setBalance(double balance) {
        if (balance < 0)
            return false;
        this.balance = balance;
        return true;
    }

    public Map<Integer, SaleTransaction> getSaleTransactionMap()
    {return this.saleTransactionMap;
    }

    public Map<Integer, Order> getOrderMap() {
        return this.orderMap;
    }

    public Map<Integer, ReturnTransaction> getReturnTransactionMap() {
        return this.returnTransactionMap;
    }

    public Map<Integer, BalanceOperation> getBalanceOperationMap() {
        return this.balanceOperationMap;
    }

    public void setSaleTransactionMap(Map<Integer, SaleTransaction> newSaleMap) {
        if(newSaleMap==null)
            throw new RuntimeException();
        this.saleTransactionMap.clear();
        this.saleTransactionMap.putAll(newSaleMap);
    }

    public void setOrderMap(Map<Integer, Order> newOrderMap) {
        if(newOrderMap==null)
            throw new RuntimeException();
        this.orderMap.clear();
        this.orderMap.putAll(newOrderMap);
    }

    public void setReturnTransactionMap(Map<Integer, ReturnTransaction> newReturnMap) {
        if(newReturnMap==null)
            throw new RuntimeException();
        this.returnTransactionMap.clear();
        this.returnTransactionMap.putAll(newReturnMap);
    }

    public void setBalanceOperationMap(Map<Integer, BalanceOperation> newBalanceMap) {
        if(newBalanceMap==null)
            throw new RuntimeException();
        this.balanceOperationMap.clear();
        this.balanceOperationMap.putAll(newBalanceMap);
    }


    public List<BalanceOperation> getBalanceOperationByDate(LocalDate from, LocalDate to) {
        List<BalanceOperation> bo = new ArrayList<>();
        if (this.balanceOperationMap.isEmpty())
            return bo;

        if (from == null && to != null) {
            //All Balance operation from start to LocalDateTo
            bo = balanceOperationMap.values().stream().
                    filter(t -> (t.getDate().isBefore(to))).collect(Collectors.toList());
        } else if (from != null && to == null) {
            //All Balance operation from LocalDateFrom to end
            bo = balanceOperationMap.values().stream().
                    filter(t -> (t.getDate().isAfter(from))).collect(Collectors.toList());
        } else if (from == null) {
            //All Balance operation -> to==null(if it's not -> first if)
            bo = new ArrayList<>(balanceOperationMap.values());
        } else {
            bo = balanceOperationMap.values().stream().
                    filter(t -> t.getDate().isBefore(to) && t.getDate().isAfter(from)).collect(Collectors.toList());
        }
        return bo;
    }


    public Integer newId() {
        int max = Math.max(orderMap.keySet().stream().max(Comparator.comparingInt(t -> t)).orElse(0),
                returnTransactionMap.keySet().stream().max(Comparator.comparingInt(t -> t)).orElse(0));
        max = Math.max(max, saleTransactionMap.keySet().stream().max(Comparator.comparingInt(t -> t)).orElse(0));
        return Math.max(max, balanceOperationMap.keySet().stream().max(Comparator.comparingInt(t -> t)).orElse(0)) + 1;
    }


}
