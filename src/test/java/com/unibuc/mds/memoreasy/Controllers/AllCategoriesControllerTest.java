package com.unibuc.mds.memoreasy.Controllers;

import com.unibuc.mds.memoreasy.Models.Category;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AllCategoriesControllerTest {

    @Test
    void testCategoriesListManipulation() {
        ArrayList<Category> categories = new ArrayList<>();
        assertTrue(categories.isEmpty());

        categories.add(new Category(1, 1, "Test"));
        assertEquals(1, categories.size());

        categories.remove(0);
        assertTrue(categories.isEmpty());
    }
}