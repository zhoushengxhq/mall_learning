package com.mall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mall.common.ServerResponse;
import com.mall.dao.CategoryMapper;
import com.mall.pojo.Category;
import com.mall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse addCategory(String categoryName, Integer parentId){
        if (parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMassage("添加品类参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);//这个分类是可用的
        int rowCount = categoryMapper.insert(category);
        if (rowCount > 0){
            return ServerResponse.createBySuccess("添加品类成功");
        }
        return ServerResponse.createByErrorMassage("添加品类失败");
    }

    public ServerResponse updateCategoryName(Integer categoryId, String categoryName){
        if (categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMassage("更新品类参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount > 0){
            return ServerResponse.createBySuccess("更新品类名字成功");
        }
        return ServerResponse.createByErrorMassage("更新品类名字失败");
    }

    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)){
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 递归查询本节点id及子节点的id
     * @param categoryId
     * @return
     */
    public ServerResponse<List<Integer>> selectCategoryAndChildrenId(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        findChildrenCategory(categorySet, categoryId);
        List<Integer> categoryIdList = Lists.newArrayList();
        if (categoryId != null){
            for (Category categoryItem : categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    //递归算法，算出子节点
    private Set<Category> findChildrenCategory(Set<Category> categorySet, Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null){
            categorySet.add(category);
        }
        //查找子节点，递归算法一定要有一个退出条件
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category categoryItem : categoryList){
            findChildrenCategory(categorySet, categoryItem.getId());
        }
        return categorySet;
    }
}
