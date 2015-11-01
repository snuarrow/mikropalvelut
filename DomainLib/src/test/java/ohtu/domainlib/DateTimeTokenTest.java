/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ohtu.domainlib;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author hexvaara
 */
public class DateTimeTokenTest
{
    
    public DateTimeTokenTest()
    {
    }
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
    }
    
    @After
    public void tearDown()
    {
    }
    
    @Test
    public void expired_true()
    {
        DateTimeToken auth = new DateTimeToken("2015","10","22","19","19","06","845b7bac-5be3-4764-b7cd-aa3b2795f566");
        
        assertTrue(auth.isExpired());
    }
    @Test
    public void expired_false()
    {
        DateTimeToken auth = DateTimeToken.generate(60);
        assertFalse(auth.isExpired());
    }
    
    
}
