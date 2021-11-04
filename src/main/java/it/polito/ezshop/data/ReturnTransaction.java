package it.polito.ezshop.data;

import java.time.LocalDate;
import java.util.Map;

public interface ReturnTransaction {

    Integer getReturnId();

    void setReturnId(Integer balanceId);

    Map<ProductType, Integer> getReturnedProduct();

    void setReturnedProduct(Map<ProductType,Integer> returnedProduct) ;

    SaleTransaction getSaleTransaction();

    void setSaleTransaction(SaleTransaction saleTransaction);

    String getStatus() ;

    void setStatus(String status);
}
