package org.magnum.mobilecloud.video.controller;

import org.magnum.mobilecloud.video.core.Category;
import org.magnum.mobilecloud.video.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by cong on 11/24/14.
 */
@Controller
public class CategoryController {
    public static final String CATEGORY_SVC_PATH = "/categories";

    @Autowired
    CategoryRepository categoryRepository;

    @RequestMapping(value= CATEGORY_SVC_PATH, method= RequestMethod.POST)
    public @ResponseBody
    Category addCategory(@RequestBody Category c){
        Category result = categoryRepository.save(c);
        return result;
    }
    @RequestMapping(value= CATEGORY_SVC_PATH, method= RequestMethod.GET)
    @ResponseBody
    Iterable<Category> getCategoryList() {
        return categoryRepository.findAll();
    }
}
