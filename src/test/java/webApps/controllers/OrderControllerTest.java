package webApps.controllers;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class OrderControllerTest {

    @Test
    public void testCheckIfRightFormat() throws Exception {
        OrderController orderController=null;
        Boolean result = orderController.checkIfRightFormat("1","2014-02-02","2013-03-03","200","2","true");
        assertEquals(result,true);
        result = orderController.checkIfRightFormat("1","2014-02-0A","2013-03-03","200","2","true");
        assertEquals(result,false);
        result = orderController.checkIfRightFormat("1","2014-02-02","2013-03-0A","200","2","true");
        assertEquals(result,false);
        result = orderController.checkIfRightFormat("1","2014-02-02","2013-03-03","A","2","true");
        assertEquals(result,false);
        result = orderController.checkIfRightFormat("1","2014-02-02","2013-03-03","200","A","A");
        assertEquals(result,false);
    }
    
}