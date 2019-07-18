package com.thl.banking;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationTest {

    @Test
    public void CreateApplicationObjectTest() {
        Application application = new Application();
        Assert.assertNotNull(application);
    }

    @Test
    public void startApplicationTest() {
        String[] args = {};
        Application.main(args);
    }
}
