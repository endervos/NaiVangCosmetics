package demoweb.demo.service;

import org.springframework.transaction.annotation.Transactional;
import demoweb.demo.entity.Category;
import demoweb.demo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIsNull();
    }

    @Transactional(readOnly = true)
    public List<Category> getRootCategoriesWithChildren() {
        return categoryRepository.findRootCategoriesWithChildren();
    }

    @Transactional(readOnly = true)
    public List<Category> getChildrenByParentId(Integer parentId) {
        return categoryRepository.findByParent_CategoryId(parentId);
    }

    @Transactional(readOnly = true)
    public Optional<Category> findByCategoryId(Integer id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    // ⚡ ép Hibernate load toàn bộ item thật
                    category.getItems().size();
                    return category;
                });
    }

    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }


}
