package it.polito.ezshop;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.InvalidCreditCardException;
import org.junit.Test;

import static org.junit.Assert.assertThrows;

public class CheckCreditCardTest {

    @Test
    public void testCheckCreditCardNumber() throws InvalidCreditCardException {
        EZShop ezShop = new EZShop();

        //Check Throws with null card
        assertThrows(InvalidCreditCardException.class, () -> {
            ezShop.checkCreditCardNumber(null);
        });
        //Check Throws with card that doesn't exist in the system
        assertThrows(InvalidCreditCardException.class, () -> {
            ezShop.checkCreditCardNumber("1234567812345679");
        });

        //Check Throws with card that does not pass Lunh Algorithm
        ezShop.CreditCardsMap.put("1234567812345678", 20.0);
        assertThrows(InvalidCreditCardException.class, () -> {
            ezShop.checkCreditCardNumber("1234567812345678");
        });

        //Check Throws with card that pass the Lunh Algorithm but with wrong length
        assertThrows(InvalidCreditCardException.class, () -> {
            ezShop.checkCreditCardNumber("49927398716");
        });

        //Check null credit card

        //Check no Throws - IMPLICIT TESTING
        ezShop.CreditCardsMap.put("1234567812345670",0.0);
        ezShop.checkCreditCardNumber("1234567812345670");
    }

}
