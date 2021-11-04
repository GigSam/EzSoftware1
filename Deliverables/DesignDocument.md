# Design Document 

Authors:
Gambino Matteo, Valentini Valeria, Gigante Samuele, Basilico Michele

Date:
30/04/2021

Version:
1.0

# Contents

- [High level design](#package-diagram)
- [Low level design](#class-diagram)
- [Verification traceability matrix](#verification-traceability-matrix)
- [Verification sequence diagrams](#verification-sequence-diagrams)

# Instructions

The design must satisfy the Official Requirements document, notably functional and non functional requirements

# High level design 

pattern used:
- MVC
- Layered- 3 tiered

```plantuml
@startuml
 package GUI{}
note left of EZShop: Contains application logic and model
package EZShop{}
package Exceptions{}
EZShop -- Exceptions
GUI -- EZShop
@enduml
```
# Low level design

## Package EZShop

### All classes are persistent
```plantuml
@startuml
class EZShop{
    - users: Map<Integer, User>
    - products: Map<Integer, ProductType>
    - attachedCard: Map<LoyaltyCard, Customer>
    - customers: Map<Integer, Customer>
    - loyaltyCards: Map<String, LoyaltyCard> 
    - creditCards: Map<String, Double>
    - book: AccountBook 
    - currentUser: User 
    - productRFID: Map<String, Product>
    ' methods
    + login(String username, String password)
    + logout()
    + reset()
    + getAllProductTypes()
    + defineCustomer(String customerName)
    + modifyCustomer(Integer id, String newCustomerName, String newCustomerCard)
    + deleteCustomer(Integer id)
    + getCustomer(Integer id)
    + getAllCustomers()
    + createCard()
    + attachCardToCustomer(String customerCard, Integer customerId)
    + modifyPointsOnCard(String customerCard, int pointsToBeAdded)
    + startSaleTransaction()
    + addProductToSale(Integer transactionId, String productCode, int amount)
    + deleteProductFromSale(Integer transactionId, String productCode, int amount)
    + applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate)
    + applyDiscountRateToSale(Integer transactionId, double discountRate)
    + computePointsForSale(Integer transactionId)
    + endSaleTransaction(Integer transactionId)
    + deleteSaleTransaction(Integer transactionId)
    + getSaleTransaction(Integer transactionId)
    ' + getSaleTransactionByNumber(Integer SaleTransactionNumber)
    + startReturnTransaction(Integer transactionId)
    + returnProduct(Integer returnId, String productCode, int amount)
    + endReturnTransaction(Integer returnId, boolean commit)
    + deleteReturnTransaction(Integer returnId)
    + receiveCashPayment(Integer transactionId, double cash)
    + receiveCreditCardPayment(Integer transactionId, String creditCard) 
    + returnCashPayment(Integer returnId)
    + returnCreditCardPayment(Integer returnId, String creditCard)
    + createProductType(String desc, String code, double price, String note)
    + updateProduct(int id, String newDesc, String newCode, double newPrice, String newNote)
    + deleteProduct(int id)
    + getProductTypeByBarCode(String barCode)
    + getProductTypesByDescription(String description)
    + updateQuantity(Integer productId, int toBeAdded)
    + updatePosition(Integer productId, String newPos)
    + issueOrder(String productCode, int quantity, double pricePerUnit)
    + payOrderFor(String productCode, int quantity, double pricePerUnit)
    + payOrder(Integer orderId)
    + recordOrderArrival(Integer orderId)
    + getAllOrders()
    + recordBalanceUpdate(double toBeAdded)
    + getCreditsAndDebits(LocalDate from, LocalDate to)
    + computeBalance()
    + createUser(String username, String password, String role)
    + deleteUser(int id)
    + updateUserRights(int id, String role)
    + getAllUsers()
    + getUser(int id)
    - checkCreditCardNumber(String number)
    - updateCreditCardTxt(String creditCard,double sale)
    + returnProductRFID(Integer id, String RFID)
    + deleteProductFromSaleRFID(Integer id, String RFID)
    + recordORderArrivalRFID(Integer id, String RFIDFrom)
    + addProductToSaleRFID(Integer id, String RFID)
}
class User{
    - id: int 
    - username: String 
    - password: String 
    - role: Role 
}
enum Role{
    + CASHIER
    + SHOP_MANAGER
    + ADMIN
}
Role -- User
User - EZShop


class AccountBook { 
    - saleTransactions: Map<Integer, SaleTransaction> 
    - returns: Map<Integer, ReturnTransaction> 
    - orders: Map<Integer, Order> 
    - otherTransactions: Map<Integer,BalanceOperation>
    - balance: double
    '  methods
    + addSaleTransaction(SaleTransaction SaleTransaction)
    + addReturnTransaction(ReturnTransaction return)
    + addOrder(Order order)
    + removeSaleTransaction(SaleTransaction SaleTransaction)
    + removeReturnTransaction(ReturnTransaction return)
    + removeOrder(Order order)
    + addBalanceOperation(BalanceOperation bo)
    + getSaleTransaction(int id)
    + getReturnTransaction(int id)
    + getOrder(int id)
    + updateBalance(double amount)
    + updateBalanceOperation(int id, double newMoney)
    + getBalanceOperationByDate(LocalDate from,LocalDate to)
    + newId()
}
AccountBook - EZShop
class BalanceOperation {
 - id: int
 - description: String
 - amount: double 
 - date: LocalDate
}
AccountBook -- "*" BalanceOperation
Order -- AccountBook
SaleTransaction --|> BalanceOperation


class ProductType{
    - id: int 
    - barCode: String 
    - description: String 
    - sellPrice: double 
    - quantity: int 
    - discountRate: double 
    - notes: String 
    - position: Position 

    + updateQuantity(int qty)
}

EZShop - "*" ProductType

class SaleTransaction {
    - time: Time 
    - paymentType: String 
    - discountRate: double 
    - ticketEntrys: Map<String, TicketEntry> 
    - status: SaleStatus
    - card: LoyaltyCard 
    - productRFID: Map<String, Product>
    + addProduct(ProductType product, int quantity)
    + deleteProduct(ProductType product, int quantity)
    + addProductDiscount(ProductType product, double discount)
    + checkout() 
    + addProductRFID(Product p)
    + deleteProductRFID(StringRFID)

}

enum SaleStatus{
    + CLOSED
    + PAYED
    + STARTED
}
SaleStatus -- SaleTransaction 

class LoyaltyCard {
    ' - ID: int 
    - points: int 
    - cardCode: String 
    + pointsUpdate(int)
    + createCardCode(int i) 
}

class Customer {
    - id: int
    - customerName: String 
    - card: LoyaltyCard 
    + checkCardCode(String newCustomerCard)
    + updateCustomerPoints(int toBeAdded)
}

LoyaltyCard "0..1" - Customer

LoyaltyCard "*"-- EZShop
Customer "*"-- EZShop
SaleTransaction "*" -- "0..1" LoyaltyCard



class Position {
    -  aisleID: int
    -  rackID: int
    -  levelID: int
}

ProductType - "0..1" Position


class Order {
  -  supplier: String
  -  pricePerUnit:double
  -  quantity: int
  - product: ProductType
  - status: OrderStatus 
  ' - String status
}
enum OrderStatus{
    ' + ISSUED
    + ORDERED
    + PAYED
    + COMPLETED
}
OrderStatus -- Order
Order "*" - ProductType

class ReturnTransaction {
  - returnProduct: Map<ProductType,Integer>
  - saleTransaction: SaleTransaction
  - status: ReturnStatus
  - productRFID: Map<String, Product>
  + addReturnProduct(ProductType product, int quantity)
  + addProductRFID(Product p)
  + deleteProductRFID(StringRFID)

}

enum ReturnStatus{
    + STARTED
    + CLOSED
}
ReturnStatus -- ReturnTransaction
ReturnTransaction --|> BalanceOperation
ReturnTransaction "*" - SaleTransaction
ReturnTransaction "*" - ProductType


class TicketEntry {
  - product: ProductType
  - amount: int
  - discountRate: double
 
}

TicketEntry -- ProductType
TicketEntry -- SaleTransaction

class Product{
    - RFID: String
    - product: ProductType
    + calculateRFID(String start, int step)
}
ProductType -- Product
EZShop -- Product
@enduml
```
# Verification traceability matrix

| Class| FR1 |FR3 |FR4 |FR5 |FR6 |FR7 |FR8 |
|--|--|--|--|--|--|--|--|
|EZShop                 |X|X|X|X|X|X|X|
|User                   |X| | | | | | | 
|AccountBook            | | |X| | |X|X|
|BalanceOperation       | | |X| |X|X|X|
|Order                  | | |X| | | | |
|SaleTransaction        | | | | |X|X| |
|ProductType            | |X|X| |X| | |
|ReturnTransaction      | | | | |X| | |
|Customer               | | | |X| | | |
|LoyaltyCard            | | | |X| | | |
|Position               | | |X| | | | |


# Verification sequence diagrams 


### Sequence diagram related to scenario 1.1
```plantuml
@startuml

actor ShopManager as admin
participant EZShop as EZS
participant ProductType as pt

admin ->EZS: createProductType()
EZS ->pt: new
pt -->EZS: return product
EZS ->admin: return productID

@enduml
```

### Sequence diagram related to scenario 2.1
```plantuml
@startuml
actor Administrator as admin
participant EZShop as EZS
participant User as u


admin ->EZS: createUser()
EZS ->u: new
u -->EZS: return user
EZS ->admin: return userID

@enduml
```
### Sequence diagram related to scenario 3.1
```plantuml
@startuml
actor "Shop Manager" as sm
participant EZShop as ezs
participant Order as o
participant AccountBook as ab
' API call
sm -> ezs: issueOrder()
' order creation
ezs -> o: new
o->ezs: return order
' order insertion into map
ezs -> ab: addOrder()
ab->ezs: return orderId
ezs --> sm: return orderId
@enduml
```

### Sequence diagram related to scenario 5.1
```plantuml
@startuml
actor "User" as au
participant EZShop as ezs
participant User as u
' API call
au -> ezs: login()
' role check
ezs -> u:getRole()
u --> ezs:return Role
ezs --> au: return true
@enduml
```


### Sequence diagram related to scenario 6.2
```plantuml
@startuml

actor "Cashier" as c
participant EZShop as ezs
participant SaleTransaction as st
participant ProductType as pt
participant AccoountBook as ab
' start new sale transaction
c -> ezs: startSaleTransaction()
ezs -> c:return transactionID
c -> ezs:addProductToSale()
' get product by barCode
ezs -> ab:getSaleTransaction()
ab -> ezs:return saleTransaction
ezs -> ezs:getProductByBarCode()
'add product to saleTransaction
ezs -> st:addProduct()
'update quantity product
ezs -> pt:updateQuantity()
pt -> ezs:return True
' cashier applies a discount
c -> pt:getDiscountRate()
pt -> c:return discount rate
' apply discount rate to product(succesfull)
c -> st:applyDiscountRateToProduct()
ezs -> ab:getSaleTransaction()
ab -> ezs:return saleTransaction
ezs ->c:return True
' cashier closes a transaction(succesfull)
c -> ezs: endSaleTransaction()
ezs -> c: return True
'cashier asks for payment type
group Succesfull Payment(usecase7)
    c -> ezs:payment management
    ezs -> c: return double or boolean
end
'update of balance(succesfull)
c -> ezs:recordBalanceUpdate()
ezs -> ab:UpdateBalance()
ab -> ezs:return True
ezs -> c:return true


@enduml
```
### Sequence diagram related to scenario 6.4
```plantuml
@startuml
actor "Cashier" as c
participant EZShop as ezs
participant SaleTransaction as st
participant ProductType as pt
participant LoyalityCard as l 
participant AccountBook as a 

c -> ezs: startSaleTransaction()
ezs-->c: returntransactionID
c -> ezs:addProductToSale()
ezs->a: getSaleTransaction
a-->ezs: returnsaleTransaction
ezs -> ezs:getProductByBarCode()
ezs -> st:addProduct()
ezs -> pt:updateQuantity()
pt --> ezs:return true
c -> ezs: endSaleTransaction()
ezs->c: return true
group credit card payment (Scenario 7-1)
c -> ezs: receiveCreditcardPayment()
ezs --> c: return true
end
c->ezs:modifyPointsOnCard()
ezs->l:pointsUpdate()
ezs --> c: return true
c->ezs:recordBalanceUpdate()
ezs->a:UpdateBalance()
a-->ezs: return true
ezs-->c:return true

@enduml
```

### Sequence diagram related to scenario 6.5
```plantuml
@startuml
actor "Cashier" as c
participant EZShop as ezs
participant SaleTransaction as st
participant ProductType as pt
participant AccoountBook as ab
' start new sale transaction
c -> ezs: startSaleTransaction()
ezs -> c:return transactionID
c -> ezs:addProductToSale()
' get product by barCode
ezs -> ab:getSaleTransaction()
ab -> ezs:return saleTransaction
ezs -> ezs:getProductByBarCode()
'add product to saleTransaction
ezs -> st:addProduct()
'update quantity product
ezs -> pt:updateQuantity()
pt -> ezs:return True
' cashier applies a discount
c -> pt:getDiscountRate()
pt -> c:return discount rate
' apply discount rate to product(succesfull)
c -> st:applyDiscountRateToProduct()
ezs -> ab:getSaleTransaction()
ab -> ezs:return saleTransaction
ezs ->c:return True
' cashier closes a transaction(succesfull)
c -> ezs: endSaleTransaction()
ezs -> c: return True
'cashier asks for payment type
group Unsuccesfull Payment(usecase7)
    c -> ezs:payment management
    ezs -> c: return double or boolean
end
'Customer cancels the payment
c -> ezs:deleteSaleTransaction()
ezs -> ab:removeSaleTransaction()
'update quantity product
ab -> pt:updateQuantity()
pt -> ab:return True
ab -> ezs: return True
ezs -> c: return True

@enduml
```


### Sequence diagram related to scenario 7.2
```plantuml
@startuml
actor Cashier as c
participant EZShop as EZS
participant AccountBook as ab

c ->EZS: receiveCreditCardPayment()

EZS -> ab:getSaleTransaction()
ab -->EZS:return SaleTransaction

EZS ->EZS:checkCreditCardNumber()
EZS-->c:return false 

@enduml
```

### Sequence diagram related to scenario 8.1
```plantuml
@startuml
actor "Cashier" as c
participant EZShop as ezs
participant ReturnTransaction as rt
participant AccountBook as ab
participant ProductType as pt
' API call
c -> ezs: startReturnTransaction()

' get associated sale 
ezs -> ab: getSaleTransaction()
ab --> ezs: return SaleTransaction
' init transaction
ezs -> rt: new
' insert into account book
ezs -> ab: addReturnTransaction()
ab --> ezs: return returnId
' end
ezs --> c: return returnID

' add products to return 
c -> ezs: returnProduct()
' add product to return t
ezs -> rt: addReturnProduct()
' update quantity available
ezs -> pt: updateQuantity()
' end add product
ezs -> c: return true

' credit card return
c -> ezs: returnCreditCardPayment()
' get transaction and update balance
ezs -> ab: getReturnTransaction()
ab --> ezs: return ReturnTransaction
ezs-> ezs: checkCreditCardNumber()
ezs -> c: return returnTransaction.getAmount()

'update of balance(succesfull)
c -> ezs:recordBalanceUpdate()
ezs -> ab:UpdateBalance()
ab -> ezs:return True
ezs -> c:return true

' close return transaction
c -> ezs: endReturnTransaction()
' get and close transaction
ezs -> ab: getReturnTransaction()
ab --> ezs: return ReturnTransaction
ezs -> rt: setStatus()
' end close return 
ezs --> c: return true
@enduml
```

### Sequence diagram related to scenario 9.1
```plantuml
@startuml
actor "Shop Manager" as sm
participant EZShop as ezs

participant AccountBook as ab

sm -> ezs:getCreditsAndDebits()
' get all operation
ezs -> ab: getSaleTransactions()
ab --> ezs: return Map<Integer, SaleTransaction> 
ezs -> ab: getReturns()
ab --> ezs: return Map<Integer, ReturnTransaction> 
ezs -> ab: getOrders()
ab --> ezs: return Map<Integer, Order> 

ezs -> ab: getOtherTransactions()
ab --> ezs: return List<BalanceOperation>

ezs->sm: return List<BalanceOperation>

@enduml
```
