package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidPricePerUnitException;
import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidProductDescriptionException;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Connect {
    private static Connection conn = null;
    private static final String url = "jdbc:sqlite:db/ezshop.db";

    public static void connect() {
        try {
            openConnection();
            createTables();
            System.out.println("Connection to SQLite has been established.");
            Statement stmt = conn.createStatement();
            /*//It does not work! The fist time I manually added the balance
            String insert2 = "INSERT INTO Balance(id, balance) values (1,0)";
            try {
                stmt.execute(insert2);
            }catch(Exception e) {}*/
            String query = "SELECT * FROM USER";
            ResultSet result = stmt.executeQuery(query);
            while(result.next()) {
                System.out.println(result.getString("username"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } 
    }
    public static void closeConnection() {
    	try {
        	if(conn!=null) {
        		conn.close();  
        		conn = null;
        	}
    	}catch(Exception e){e.printStackTrace();}
    }
    public static void openConnection() {
    	try {
        // create a connection to the database
        conn = DriverManager.getConnection(url); 
    	}catch(Exception e) {e.printStackTrace();}
    }
    public static void createTables() {
        String tableUser = "CREATE TABLE IF NOT EXISTS User("
                + "id INTEGER NOT NULL PRIMARY KEY,"
                + "username text NOT NULL,"
                + "password text NOT NULL,"
                + "role INTEGER NOT NULL)";
        String loyaltyCard = "CREATE TABLE IF NOT EXISTS LoyaltyCard("
                + "points INTEGER NOT NULL,"
                + "number text NOT NULL PRIMARY KEY)";
        String customerTable = "CREATE TABLE IF NOT EXISTS Customer("
                + "id INTEGER NOT NULL PRIMARY KEY,"
                + "customerName text NOT NULL,"
                + "cardId text NOT NULL,"
                + "FOREIGN KEY (cardId) references LoyaltyCard(number))";
        String productTypes = "CREATE TABLE IF NOT EXISTS ProductTypes("
                + "id INTEGER NOT NULL PRIMARY KEY,"
                + "barCode text NOT NULL,"
                + "description text NOT NULL,"
                + "sellPrice number NOT NULL,"
                + "quantity integer not null,"
                //+ "discountRate number not null,"
                + "notes text,"
                + "position text"
                + ")";
        String soldProduct = "CREATE TABLE IF NOT EXISTS SoldProducts("
                + "id INTEGER NOT NULL,"
                + "productId integer NOT NULL,"
                + "quantity integer NOT NULL,"
                + "discountRate number,"
                + "PRIMARY KEY(id, productId),"
                + "FOREIGN KEY (productId) references ProductTypes(id))";
        String saleTransaction = "CREATE TABLE IF NOT EXISTS SaleTransactions("
                + "id INTEGER NOT NULL PRIMARY KEY,"
                + "description text NOT NULL,"
                + "amount number NOT NULL,"
                + "date date NOT NULL,"
                + "time time not null,"
                + "paymentType text,"
                + "discountRate number,"
                + "status integer not null,"
                + "cardId text, "
                + "soldProducts integer not null,"
                + "FOREIGN KEY (soldProducts) references SoldProducts(id),"
                + "FOREIGN KEY (cardId) references LoyaltyCard(number))";
        String orders = "CREATE TABLE IF NOT EXISTS Orders("
                + "id INTEGER NOT NULL PRIMARY KEY,"
                //+ "description text NOT NULL,"
                //+ "amount number NOT NULL,"
                + "date date NOT NULL,"
                + "supplier text,"
                + "status integer not null,"
                + "productId integer not null,"
                + "unitPrice number not null,"
                + "quantity integer not null,"
                + "FOREIGN KEY (productId) references ProductType(id))";
        String returnedProduct = "CREATE TABLE IF NOT EXISTS ReturnedProducts("
                + "id INTEGER NOT NULL,"
                + "productId integer NOT NULL,"
                + "quantity integer NOT NULL,"
                + "PRIMARY KEY(id, productId),"
                + "FOREIGN KEY (productId) references ProductTypes(id))";
        String returnTransaction = "CREATE TABLE IF NOT EXISTS ReturnTransactions("
                + "id INTEGER NOT NULL PRIMARY KEY,"
                + "description text NOT NULL,"
                + "amount number NOT NULL,"
                + "date date NOT NULL,"
                + "status integer not null,"
                + "saleId integer not null, "
                + "returnedProductsId integer not null,"
                + "FOREIGN KEY (returnedProductsId) references ReturnedProducts(id),"
                + "FOREIGN KEY (saleId) references SaleTransactions(id))";
        String balanceOperation = "CREATE TABLE IF NOT EXISTS BalanceOperations("
                + "id INTEGER NOT NULL PRIMARY KEY,"
                + "description text NOT NULL,"
                + "amount number NOT NULL,"
                + "date date NOT NULL,"
                + "type text NOT NULL)";
        String tableRFID = "CREATE TABLE IF NOT EXISTS ProductRFID("
        		+ "RFID text not null primary key,"
        		+ "productId integer NOT NULL,"
        		+ "FOREIGN KEY (productId) references ProductTypes(id))";
        String soldRFID = "CREATE TABLE IF NOT EXISTS SoldRFID("
        		+ "saleId integer not null,"
        		+ "RFID text NOT NULL,"
        		+ "PRIMARY KEY(saleId, RFID),"
        		+ "foreign key(saleId) references SaleTransactions(id),"
        		+ "FOREIGN KEY (RFID) references ProductRFID(RFID))";

        /*String balance = "CREATE TABLE IF NOT EXISTS Balance("
                + "id INTEGER NOT NULL PRIMARY KEY,"
                + "balance NUMBER NOT NULL)";*/

        //CREDITCARDTABLE???//

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(tableUser);
            stmt.executeUpdate(loyaltyCard);
            stmt.executeUpdate(customerTable);
            stmt.executeUpdate(productTypes);
            stmt.executeUpdate(soldProduct);
            stmt.executeUpdate(saleTransaction);
            stmt.executeUpdate(orders);
            stmt.executeUpdate(returnedProduct);
            stmt.executeUpdate(returnTransaction);
            //stmt.executeUpdate(balance);
            stmt.executeUpdate(balanceOperation);
            stmt.executeUpdate(tableRFID);
            stmt.executeUpdate(soldRFID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // USER(COMPLETED)
    public static Map<Integer,User> getUsers() {

        Map<Integer, User> users = new HashMap<>();
        try (Statement stmt = conn.createStatement()) {
            String sql = "Select * from User";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                int id = rs.getInt("id");
                users.put(id,  new UserClass(id, rs.getString("username"), rs.getString("password"), RoleEnum.values()[rs.getInt("role")]));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    public static boolean addUsers(int id,String username,String password, String role) {
        String sql = "insert into User( id, username, password, role)"
                + "values("+id+","
                + "'"+username+"',"
                + "'"+password+"',"
                + RoleEnum.valueOf(role).ordinal()+")";
        try{
    		Statement st = conn.createStatement();
            st.execute(sql);
        st.close();
    	}catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static boolean updateUserRights(int id, String role) {
        String sql = "update User"
                + " set "
                + "role = "+RoleEnum.valueOf(role).ordinal()+
                " where id = "+id;
        try{
    		Statement st = conn.createStatement();
            st.execute(sql);
        st.close();
    	}catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static boolean deleteUser(int id) {
        String sql = "delete from User"
                + " where id = "+id;
        try{
    		Statement st = conn.createStatement();
            st.execute(sql);
        st.close();
    	}catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // PRODUCT TYPES(COMPLETED)
    public static Map<Integer,ProductType> getProduct(){
        Map<Integer, ProductType> products = new HashMap<>();
        try (Statement stmt = conn.createStatement()) {
            String sql = "select * from ProductTypes";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                String barcode = rs.getString("barCode");
                double sellPrice = rs.getDouble("sellPrice");
                int qty = rs.getInt("quantity");
                // double discount = rs.getDouble("discountRate");
                String notes = rs.getString("notes");
                String position = rs.getString("position");
                ProductTypeClass pt = new ProductTypeClass(id, description, barcode, sellPrice, notes);
                pt.setLocation(position);
                pt.setQuantity(qty);
                products.put(id,  pt);
            }
        } catch (SQLException | InvalidProductDescriptionException | InvalidProductCodeException | InvalidPricePerUnitException e) {
            e.printStackTrace();
        }
        return products;
    }
    public static boolean addProduct(int nextId,String productCode,String description,double pricePerUnit,String note){
        String sql = "insert into ProductTypes( id, barcode, description, sellPrice, quantity, notes, position)"
                + "values("+nextId+","
                + "'"+productCode+"',"
                + "'"+description+"',"
                + pricePerUnit+","
                +"0,"
                +(note==null?"NULL,":"'"+note+"',")+
                "NULL)";
        try{
    		Statement st = conn.createStatement();
            st.execute(sql);
        st.close();
    	}catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static boolean updateProduct(int id,String newCode,String newDescription,double newPrice,String newNote){
        String sql = "update ProductTypes "
                + "set "
                + "barcode = '"+newCode+"',"
                + "description = '"+newDescription+"',"
                + "sellPrice="+newPrice+","
                +"notes="+(newNote==null?" NULL ":"'"+newNote+"' ")+
                " where id = "+id;
        try{
    		Statement st = conn.createStatement();
            st.execute(sql);
        st.close();
    	}catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean updateProductPosition(int productId,Position p){
        String sql = "UPDATE ProductTypes SET position = '"+p.toString()+"' where id = "+productId;
        try{
    		Statement st = conn.createStatement();
            st.execute(sql);
        st.close();
    	}catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean updateProductQuantity(int productId,int quantity){
        String sql = "update ProductTypes "
                + "set "
                + "quantity = "+quantity
                +" where id = "+productId;
        try{
    		Statement st = conn.createStatement();
            st.execute(sql);
        st.close();
    	}catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean removeProduct(int id){
        // db update
        String sql = "delete from ProductTypes where id = "+id;
        try{
    		Statement st = conn.createStatement();
            st.execute(sql);
        st.close();
    	}catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //LOYALTY CARD(COMPLETED)
    public static Map<String,LoyaltyCard> getLoyaltyCard(){

        Map<String,LoyaltyCard> cards = new HashMap<>();
        try (Statement stmt = conn.createStatement()) {
            String sql = "select * from LoyaltyCard";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                String number = rs.getString("number");
                int points = rs.getInt("points");
                cards.put(number, new LoyaltyCardClass(number, points));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards;
    }

    public static boolean addLoyaltyCard(String number){
    	String sql = "insert into loyaltyCard(number, points)"
        		+ "values('"+number+"',"
        		+"0)";
        try{
    		Statement st = conn.createStatement();
            st.execute(sql);
        st.close();
    	}catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean updateLoyaltyCard(String customerCard,int points){
    	String sql = "update loyaltyCard "
        		+ "set "
        		+ "points = "+points
        		+" where number = '"+customerCard+"'";
    	try{
    		Statement st = conn.createStatement();
            st.execute(sql);
        st.close();
    	}catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //CUSTOMERS(COMPLETED)
    public static Map<Integer, Customer> getCustomer(Map<String, LoyaltyCard> cards){

        Map<Integer, Customer> customers = new HashMap<>();
        //Map<String, LoyaltyCard> cards = getLoyaltyCard();
        try (Statement stmt = conn.createStatement()) {
            String sql = "select * from customer";
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                int id = rs.getInt("id");
                String customerName = rs.getString("customerName");
                String cardId = rs.getString("cardId");
                //Integer points = rs.getInt("points");
                LoyaltyCard usrCard = cards.get(cardId);
                CustomerClass c = new CustomerClass(id, customerName,cardId,usrCard==null?0:usrCard.getPoints()/*points*/);
                c.setCustomerCard(cardId);
                customers.put(id,  c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }
    public static boolean addCustomer(int id,String customerName){
        String cardId = "";
    	String sql = "insert into Customer(id, customerName, cardId)"
         		+ "values("+id+","
         		+"'"+customerName+"',"
         		+"'"+cardId+"')";
        try{
    		Statement st = conn.createStatement();
            st.execute(sql);
        st.close();
    	}catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static boolean updateCustomer(int id,String newCustomerName,String newCustomerCard){
    	String sql = "update Customer "
        		+ "set "
        		+ "customerName = '"+ newCustomerName +"',"
        		+ "cardId = '"+ newCustomerCard +"'"
        		+ "where id ="+id;
        try{
    		Statement st = conn.createStatement();
            st.execute(sql);
        st.close();
    	}catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static boolean removeCustomer(int id){

        String sql = "delete from Customer"
                + " where id = "+id;
        try{
    		Statement st = conn.createStatement();
            st.execute(sql);
        st.close();
    	}catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Map<LoyaltyCard, Customer> getAttachedCard(Map<String, LoyaltyCard> cards, Map<Integer, Customer> customers){
    	Map<LoyaltyCard, Customer> res = new HashMap<>();
    	for(Customer c: customers.values()) {
    		String cardCode = c.getCustomerCard();
    		if(cardCode!=null && !cardCode.equals("")) {
    			LoyaltyCard card = cards.get(cardCode);
    			if(card!=null) {
    				res.put(card, c);
    			}
    		}
    	}
    	return res;
    }
    public static Map<Integer, Order> getOrder(Map<Integer, ProductType> products){
        // ORDER
        Map<Integer, Order> orders = new HashMap<>();
        //Map<Integer, ProductType> products = getProduct();

        try (Statement stmt = conn.createStatement()) {
            String sql = "SELECT * FROM orders";
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                int id = rs.getInt("id");
                Date date = Date.valueOf(rs.getString("date"));
                String supplier = rs.getString("supplier");
                int status = rs.getInt("status");
                int productId = rs.getInt("productId");
                double unitPrice = rs.getDouble("unitPrice");
                int quantity = rs.getInt("quantity");
                OrderStatus oStatus = OrderStatus.values()[status];
                String prodCode = products.get(productId).getBarCode();
                OrderClass o = new OrderClass(id, date.toLocalDate(), supplier, prodCode, unitPrice, quantity, oStatus);
                orders.put(id, (Order) o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }
    
    public static boolean addOrder(int nextId, double pricePerUnit, int quantity, OrderStatus status, int productId ) {
    	// insert into db
    	String sql = "INSERT INTO Orders(id, date, status, productId, unitPrice, quantity) "
        		+ "VALUES ("+nextId
        		//+", 'ORDER', "
        		//+ (pricePerUnit * quantity) +", "
        		+ ", DATE('now'), "
        		+ status.ordinal()+", "
        		+ productId+", "
        		+ pricePerUnit+", "
        		+ quantity+")";    	
    	try{
    		Statement st = conn.createStatement();
    		st.execute(sql);
    	st.close();
    	}catch (Exception e) {
			e.printStackTrace();
			return false;
    	}
    	return true;
    }
    public static boolean updateOrderStatus(int id, OrderStatus status) {
    	String sql = "UPDATE Orders SET status = "+status.ordinal() + " WHERE id = "+id;
    	try{
    		Statement st = conn.createStatement();
    		st.execute(sql);
    		st.close();
    	}catch (Exception e) {
			e.printStackTrace();
			return false;
    	}
    	return true;
    }
    public static void removeOrder(int id) {
    	String sql = "DELETE FROM Orders WHERE id = "+id;
    	try{
    		Statement st = conn.createStatement();
    		st.execute(sql);
    	st.close();
    	}catch (Exception e) {
			e.printStackTrace();
    	}
    }
    //SALE TRANSACTION

    //SALE TRANSACTION		
    public static Map<Integer, SaleTransaction> getSaleTransaction(Map<Integer, ProductType> products, Map<String, LoyaltyCard> cards, Map<String, Product> productRFID){
        HashMap<Integer, SaleTransaction> sales = new HashMap<>();
        //Map<Integer, ProductType> products = getProduct();

        try (Statement stmt = conn.createStatement()) {
            String sql = "SELECT * FROM SaleTransactions";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                double amount = rs.getDouble("amount");
                Date date = Date.valueOf(rs.getString("date"));
                Time time = Time.valueOf(rs.getString("time"));
                int status = rs.getInt("status");
                String paymentType = rs.getString("paymentType");
                String cardId = rs.getString("cardId");
                double discountRate = rs.getDouble("discountRate");
                Map<String, TicketEntryClass> entries = new HashMap<>();
                String sql2 = "select * from SoldProducts where id="+id;
                try (Statement stmt1 = conn.createStatement()){
                	ResultSet rs1 = stmt1.executeQuery(sql2);
                	while(rs1.next()) {
                		int productId = rs1.getInt("productId");
                		int qty = rs1.getInt("quantity");
                		double discount = rs1.getDouble("discountRate");
                		ProductType pt = products.get(productId);
                		TicketEntryClass te = new TicketEntryClass(pt, qty, discount);
                		entries.put(pt.getBarCode(), te);
                	}
                }catch(Exception e) {
                	e.printStackTrace();
                }
                Map<String, Product> soldRFID = new HashMap<>();
                String sql3 = "SELECT RFID FROM SoldRFID WHERE saleId="+id;
                try(Statement st3 = conn.createStatement()){
                	ResultSet rs3 = st3.executeQuery(sql3);
                	while(rs3.next()) {
                		String rfid = rs3.getString("RFID");
                		Product p = productRFID.get(rfid);
                		if(p==null)
                			continue;
                		soldRFID.put(rfid, p);
                	}
                }
                SaleTransactionClass s = new SaleTransactionClass(id, description, amount, date.toLocalDate(),  "CREDIT", paymentType, time, SaleStatus.values()[status], cards.get(cardId), entries, discountRate);
                s.setProductRFID(soldRFID);
                sales.put(id,  s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sales;

    }

    public static boolean addSaleTransaction(SaleTransactionClass sale, int id, String description, double amount,
                                             String paymentType, double discountRate, LoyaltyCard lt) {
        String sql = "INSERT INTO SaleTransactions(id, description, amount, date, time, paymentType, discountRate, status, cardId, soldProducts) "
                +"VALUES ("+id
                +", '"+description
                +"', "+amount
                +", DATE('now')"
                +", TIME('now')"   					//is it ok?
                +", '"+paymentType+"'"
                +", "+discountRate
                +", "+SaleStatus.CLOSED.ordinal()
                +", "+(lt==null?"NULL":"'"+lt.getCardCode()+"'")
                +", "+id+")";
        try{
    		Statement st = conn.createStatement();
            st.execute(sql);
        st.close();
    	}catch (Exception e) {
            e.printStackTrace();
            System.out.println(sql);
            return false;
        }
        for(int i=0; i<sale.getEntries().size(); i++) {
            TicketEntryClass tec=(TicketEntryClass) sale.getEntries().get(i);
            String sql2 = "INSERT INTO SoldProducts(id, productId, quantity, discountRate) "
                    +"VALUES ("+sale.getBalanceId()
                    +", "+tec.getProductType().getId()
                    +", "+tec.getAmount()
                    +", "+tec.getDiscountRate()+")";
            try{
    		Statement st = conn.createStatement();
                st.execute(sql2);
            st.close();
    	}catch (Exception e) {
    		e.printStackTrace();
    		return false;
    	}
        }
        Map<String, Product> soldRFID = sale.getProductRFID();
        if(soldRFID !=null && soldRFID.size()>0) {
        	for(String s: soldRFID.keySet()) {
        		String sql3 = "INSERT INTO SoldRFID(RFID, saleId) VALUES('"+s+"', "+sale.getBalanceId()+")";
        		try{
        			Statement st = conn.createStatement();
        			st.execute(sql3);
        			st.close();
        		}catch (Exception e) {
        			e.printStackTrace();
        			return false;
        		}
        	}
        }
        return true;
    }

    public static boolean removeSaleTransaction(int id) {
        String sql = "DELETE from SaleTransactions"
                + " WHERE id = "+id;
        try{
    		Statement st = conn.createStatement();
            st.execute(sql);
            st.executeUpdate("delete from SoldProducts where id = "+id);
            st.executeUpdate("delete from SoldRFID where saleId = "+id);
        st.close();
    	}catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean updateSaleTransactionStatus(int id, SaleStatus status,String paymentType) {
        String sql = "UPDATE SaleTransactions SET status = " + status.ordinal() +" ,paymentType = '"+ paymentType + "' WHERE id = " + id;
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // RETURN TRANSACTION
    public static Map<Integer, ReturnTransaction> getReturnTransaction(Map<Integer, ProductType> products, Map<Integer, SaleTransaction> sales){
        HashMap<Integer, ReturnTransaction> returns = new HashMap<>();
        //Map<Integer, SaleTransaction> sales = getSaleTransaction();
        //Map<Integer, ProductType> products = getProduct();

        try (Statement stmt = conn.createStatement()) {
            String sql = "select * from ReturnTransactions";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                double amount = rs.getDouble("amount");
                Date date = Date.valueOf(rs.getString("date"));
                int status = rs.getInt("status");
                int saleId = rs.getInt("saleId");
                ReturnStatus rstatus = ReturnStatus.values()[status];
                //Is it ok??
                SaleTransaction s = sales.get(saleId);
                //
                Map<ProductType, Integer> returnedProducts = new HashMap<>();
                String getReturnedProd = "select * from ReturnedProducts where id = "+id;
                try(Statement stmt1 = conn.createStatement()){
                	ResultSet rs1 = stmt1.executeQuery(getReturnedProd);
                	while(rs1.next()) {
                		int productId = rs1.getInt("productId");
                		int qty = rs1.getInt("quantity");
                		ProductType pt = products.get(productId);
                		returnedProducts.put(pt, qty);
                	}
                }catch (Exception e) {
                	e.printStackTrace();
                }
                ReturnTransactionClass rt = new ReturnTransactionClass(id, description, amount, date.toLocalDate(), "DEBIT", returnedProducts, s, rstatus);
                returns.put(id,  rt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return returns;
    }

    //    public static boolean addReturnTransaction(int nextId, double amount, ReturnStatus status, Integer saleId, Integer returnedProductsId) {
//        // insert into db
//        String sql = "INSERT INTO ReturnTransaction(id, description, amount, date, status, saleId, returnedProductsId) "
//                + "VALUES ("+nextId
//                +", 'DEBIT', "
//                + amount +", "
//                + "DATE('now'), "
//                + status.ordinal()+", "
//                + saleId+", "
//                + returnedProductsId+")";
//        try{
//    		Statement st = conn.createStatement();
//            st.execute(sql);
//        st.close();
//    	}catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
//
//    public static boolean updateReturnTransactionStatus(int id, ReturnStatus status) {
//        String sql = "UPDATE ReturnTransaction SET status = " + status.ordinal() + " WHERE id = " + id;
//        try (Statement st = conn.createStatement()) {
//            st.execute(sql);
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }

    public static boolean addReturnTransaction(ReturnTransactionClass ret, int id, String description, double amount,
                                               ReturnStatus status, Integer saleId) {
        Integer i=ret.getBalanceId();
        String sql = "INSERT INTO ReturnTransactions(id, description, amount, date, status, saleId, returnedProductsId) "
                + "VALUES ("+id
                +", '"+description+"'"
                +", "+amount
                +",DATE('now'), "
                + ReturnStatus.CLOSED.ordinal()+", "
                + saleId +","
                + id+")";
        try{
    		Statement st = conn.createStatement();
            st.execute(sql);
        st.close();
    	}catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        ret.getReturnedProduct().forEach((p,q)->{
            String sql2 = "INSERT INTO ReturnedProducts(id, productId, quantity) "
                    + "VALUES ("+ret.getBalanceId()
                    +", "+p.getId()
                    +", "+q +")";
            try{
    		Statement st = conn.createStatement();
                st.execute(sql2);
            st.close();
    	}catch (Exception e) {
                e.printStackTrace();
            }
            //here the sale transaction table should be uploaded
        });

        return true;
    }
    public static boolean updateReturnTransaction(int id, ReturnStatus status) {
    	String sql = "UPDATE ReturnTransactions SET status ="+status.ordinal() +" WHERE id = "+id;
        try{
    		Statement st = conn.createStatement();
            st.execute(sql);
        st.close();
    	}catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static boolean deleteReturnTransaction(int id) {
    	// delete transaction
    	String sql = "delete from ReturnTransactions where id = "+id;
    	try{
    		Statement st = conn.createStatement();
            st.execute(sql);
        st.close();
    	}catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    	// delete returned products
    	String sql2 = "delete from ReturnedProducts where id = "+id;
		try{
    		Statement st = conn.createStatement();
            st.execute(sql2);
        st.close();
    	}catch (Exception e) {
            e.printStackTrace();
        }
    	return true;
    }
    public static boolean deleteAll() {
    	try {
    		if(conn == null) {
    			final String url = "jdbc:sqlite:db/ezshop.db";
    			// create a connection to the database
    			conn = DriverManager.getConnection(url);
    			
    		}
    		// drop all tables
			Statement stmt = conn.createStatement();
			String sqlDrop = "delete from ProductTypes";
			stmt.executeUpdate(sqlDrop);
			sqlDrop = "delete from SoldProducts";
			stmt.executeUpdate(sqlDrop);
			sqlDrop ="delete from SaleTransactions";
			stmt.executeUpdate(sqlDrop);
			sqlDrop="delete from user";
			stmt.executeUpdate(sqlDrop);
			sqlDrop="delete from customer";
			stmt.executeUpdate(sqlDrop);
        	sqlDrop="delete from loyaltyCard";
			stmt.executeUpdate(sqlDrop);
			sqlDrop ="delete from orders";
			stmt.executeUpdate(sqlDrop);
			sqlDrop ="delete from returnedProducts";
			stmt.executeUpdate(sqlDrop);
			sqlDrop ="delete from ReturnTransactions";
			stmt.executeUpdate(sqlDrop);
			sqlDrop ="delete from BalanceOperations";
			stmt.executeUpdate(sqlDrop);
			sqlDrop ="delete from ProductRFID";
			stmt.executeUpdate(sqlDrop);
			sqlDrop ="delete from SoldRFID";
			stmt.executeUpdate(sqlDrop);
			System.out.println("All tables content deleted");
    	}catch(Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }

    //BALANCE
/*

    public static boolean balanceUpdate(double newBalance) {
        String sql = "UPDATE Balance SET balance ="+newBalance +" WHERE id = 1 ";
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static double getBalance(){
        String sql = "select balance from Balance WHERE id=1";
        double balance=0.0;
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            balance = rs.getDouble("balance");
            System.out.println(balance);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }
*/
//	public static Map<? extends Integer, ? extends BalanceOperation> getBalanceOperations() {
//
//		return null;
//	}

    public static Map<Integer, BalanceOperation> getBalanceOperations(){
        HashMap<Integer, BalanceOperation> balance = new HashMap<>();

        try (Statement stmt = conn.createStatement()) {
            String sql = "SELECT * FROM BalanceOperations";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                double amount = rs.getDouble("amount");
                Date date = Date.valueOf(rs.getString("date"));
                String type = rs.getString("type");
                BalanceOperationClass b = new BalanceOperationClass(id, description, amount, date.toLocalDate(), type);
                balance.put(id, b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }

    public static boolean addBalanceOperation(BalanceOperationClass b) {
        String sql = "INSERT INTO BalanceOperations(id, description, amount, date, type ) "
                + "VALUES (" + b.getBalanceId()
                + ", '" + b.getDescription() + "'"
                + ", " + b.getMoney()
                + ",DATE('now'), '"
                + b.getType() + "')";
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean removeBalanceOperation(int id){
        // db update
        String sql = "delete from BalanceOperations where id = "+id;
        try{
    		Statement st = conn.createStatement();
            st.execute(sql);
        st.close();
    	}catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Map<String, Product> getAllProductRFID(Map<Integer, ProductType> products){
    	String sql = "SELECT * from ProductRFID";
    	HashMap<String, Product> productRFID = new HashMap<>();
    	try{
    		Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()) {
            	String RFID = rs.getString("RFID");
            	int productId = rs.getInt("productId");
            	ProductTypeClass pt = (ProductTypeClass)products.get(productId);
            	Product p = new Product(RFID, pt);
            	productRFID.put(RFID, p);
            }
        st.close();
    	}catch (Exception e) {
            e.printStackTrace();
        }
    	return productRFID;
    }
    public static boolean addProductRFID(Product p) {
    	String sql = "INSERT INTO ProductRFID(RFID, productId) VALUES('"+p.getRFID()+"', "+p.getProductType().getId()+")";
    	try {
    		Statement st = conn.createStatement();
    		st.executeUpdate(sql);
    	}catch(Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    public static boolean deleteProductRFID(String RFID) {
    	String sql = "DELETE FROM ProductRFID WHERE RFID = '"+RFID+"'";
    	try {
    		Statement st = conn.createStatement();
    		st.execute(sql);
    	}catch(Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    
    public static boolean addSoldRFID(String RFID, int saleId) {
    	String sql = "INSERT INTO SoldRFID(saledId, RFID) VALUES("+saleId+", '"+RFID+"')";
    	try {
    		Statement st = conn.createStatement();
    		st.execute(sql);
    	}catch(Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }

    public static boolean removeSoldRFID(String RFID, int saleId) {
    	String sql = "DELETE FROM SoldRFID WHERE saleId = "+saleId+" AND RFID = '"+RFID+"'";
    	try {
    		Statement st = conn.createStatement();
    		st.execute(sql);
    	}catch(Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
}
