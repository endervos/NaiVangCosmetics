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

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        categories.forEach(cat -> {
            if (cat.getParent() != null) {
                cat.getParent().getName();
            }

            cat.getItems().size();
        });

        return categories;
    }

    @Transactional(readOnly = true)
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
                    category.getItems().size();

                    if (category.getParent() != null) {
                        category.getParent().getName();
                    }

                    return category;
                });
    }

    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Category> findBySlug(String slug) {
        return categoryRepository.findBySlug(slug);
    }

    @Transactional(readOnly = true)
    public List<Category> searchByName(String keyword) {
        return categoryRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Transactional
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public Category createCategory(String name, String slug, Integer parentId) {
        Category category = new Category();
        category.setName(name);
        category.setSlug(slug);

        // Set parent nếu có
        if (parentId != null) {
            Category parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục cha với ID: " + parentId));
            category.setParent(parent);
        }

        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Integer id, String name, String slug, Integer parentId) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));

        category.setName(name);
        category.setSlug(slug);

        if (parentId != null) {
            if (parentId.equals(id)) {
                throw new RuntimeException("Danh mục không thể là cha của chính nó!");
            }

            if (isDescendant(id, parentId)) {
                throw new RuntimeException("Danh mục cha không thể là con của danh mục hiện tại!");
            }

            Category parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục cha với ID: " + parentId));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));
        if (!category.getItems().isEmpty()) {
            throw new RuntimeException("Không thể xóa danh mục có sản phẩm!");
        }
        if (!category.getChildren().isEmpty()) {
            throw new RuntimeException("Không thể xóa danh mục có danh mục con!");
        }

        categoryRepository.delete(category);
    }

    private boolean isDescendant(Integer categoryId, Integer potentialAncestorId) {
        Optional<Category> potentialAncestor = categoryRepository.findById(potentialAncestorId);

        if (potentialAncestor.isEmpty()) {
            return false;
        }

        Category current = potentialAncestor.get();

        while (current != null) {
            if (current.getCategoryId().equals(categoryId)) {
                return true;
            }
            current = current.getParent();
        }

        return false;
    }

    @Transactional(readOnly = true)
    public long countCategories() {
        return categoryRepository.count();
    }

    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        return categoryRepository.existsById(id);
    }
}