package com.github.community.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class MyUtilTest {

    @Test
    public void testGetFileSuffix(){
        Assertions.assertNull(MyUtil.getFileSuffix("abc"));
        Assertions.assertEquals(MyUtil.getFileSuffix("abc.png"),"png");
    }
}