package com.vijay.User_Master.service.hotel;

import com.vijay.User_Master.dto.hotel.CategoryDTO;
import com.vijay.User_Master.dto.hotel.MenuItemDTO;
import com.vijay.User_Master.entity.hotel.Category;
import com.vijay.User_Master.entity.hotel.MenuItem;
import com.vijay.User_Master.repository.hotel.CategoryRepository;
import com.vijay.User_Master.repository.hotel.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {
    
    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;
    
    // ==================== CATEGORY OPERATIONS ====================
    
    @Transactional
    public CategoryDTO createCategory(CategoryDTO dto) {
        log.info("Creating new category: {}", dto.getName());
        Category category = Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0)
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();
        
        category = categoryRepository.save(category);
        return mapToCategoryDTO(category);
    }
    
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO dto) {
        log.info("Updating category: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setImageUrl(dto.getImageUrl());
        category.setDisplayOrder(dto.getDisplayOrder());
        category.setIsActive(dto.getIsActive());
        
        category = categoryRepository.save(category);
        return mapToCategoryDTO(category);
    }
    
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(this::mapToCategoryDTO)
                .collect(Collectors.toList());
    }
    
    public List<CategoryDTO> getActiveCategories() {
        return categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(this::mapToCategoryDTO)
                .collect(Collectors.toList());
    }
    
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return mapToCategoryDTO(category);
    }
    
    @Transactional
    public void deleteCategory(Long id) {
        log.info("Deleting category: {}", id);
        categoryRepository.deleteById(id);
    }
    
    // ==================== MENU ITEM OPERATIONS ====================
    
    @Transactional
    public MenuItemDTO createMenuItem(MenuItemDTO dto) {
        log.info("Creating new menu item: {}", dto.getName());
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + dto.getCategoryId()));
        
        MenuItem menuItem = MenuItem.builder()
                .category(category)
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .imageUrl(dto.getImageUrl())
                .isVegetarian(dto.getIsVegetarian() != null ? dto.getIsVegetarian() : false)
                .isVegan(dto.getIsVegan() != null ? dto.getIsVegan() : false)
                .isSpicy(dto.getIsSpicy() != null ? dto.getIsSpicy() : false)
                .preparationTime(dto.getPreparationTime() != null ? dto.getPreparationTime() : 15)
                .calories(dto.getCalories())
                .isAvailable(dto.getIsAvailable() != null ? dto.getIsAvailable() : true)
                .isFeatured(dto.getIsFeatured() != null ? dto.getIsFeatured() : false)
                .build();
        
        menuItem = menuItemRepository.save(menuItem);
        return mapToMenuItemDTO(menuItem);
    }
    
    @Transactional
    public MenuItemDTO updateMenuItem(Long id, MenuItemDTO dto) {
        log.info("Updating menu item: {}", id);
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));
        
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            menuItem.setCategory(category);
        }
        
        menuItem.setName(dto.getName());
        menuItem.setDescription(dto.getDescription());
        menuItem.setPrice(dto.getPrice());
        menuItem.setImageUrl(dto.getImageUrl());
        menuItem.setIsVegetarian(dto.getIsVegetarian());
        menuItem.setIsVegan(dto.getIsVegan());
        menuItem.setIsSpicy(dto.getIsSpicy());
        menuItem.setPreparationTime(dto.getPreparationTime());
        menuItem.setCalories(dto.getCalories());
        menuItem.setIsAvailable(dto.getIsAvailable());
        menuItem.setIsFeatured(dto.getIsFeatured());
        
        menuItem = menuItemRepository.save(menuItem);
        return mapToMenuItemDTO(menuItem);
    }
    
    public List<MenuItemDTO> getAllMenuItems() {
        return menuItemRepository.findByIsAvailableTrue().stream()
                .map(this::mapToMenuItemDTO)
                .collect(Collectors.toList());
    }
    
    public List<MenuItemDTO> getMenuItemsByCategory(Long categoryId) {
        return menuItemRepository.findByCategoryIdAndIsAvailableTrue(categoryId).stream()
                .map(this::mapToMenuItemDTO)
                .collect(Collectors.toList());
    }
    
    public List<MenuItemDTO> getFeaturedMenuItems() {
        return menuItemRepository.findByIsFeaturedTrueAndIsAvailableTrue().stream()
                .map(this::mapToMenuItemDTO)
                .collect(Collectors.toList());
    }
    
    public List<MenuItemDTO> getVegetarianMenuItems() {
        return menuItemRepository.findByIsVegetarianTrueAndIsAvailableTrue().stream()
                .map(this::mapToMenuItemDTO)
                .collect(Collectors.toList());
    }
    
    public Page<MenuItemDTO> searchMenuItems(String keyword, Pageable pageable) {
        return menuItemRepository.searchMenuItems(keyword, pageable)
                .map(this::mapToMenuItemDTO);
    }
    
    public MenuItemDTO getMenuItemById(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));
        return mapToMenuItemDTO(menuItem);
    }
    
    @Transactional
    public void deleteMenuItem(Long id) {
        log.info("Deleting menu item: {}", id);
        menuItemRepository.deleteById(id);
    }
    
    // ==================== MAPPERS ====================
    
    private CategoryDTO mapToCategoryDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .displayOrder(category.getDisplayOrder())
                .isActive(category.getIsActive())
                .build();
    }
    
    private MenuItemDTO mapToMenuItemDTO(MenuItem menuItem) {
        return MenuItemDTO.builder()
                .id(menuItem.getId())
                .categoryId(menuItem.getCategory().getId())
                .categoryName(menuItem.getCategory().getName())
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .price(menuItem.getPrice())
                .imageUrl(menuItem.getImageUrl())
                .isVegetarian(menuItem.getIsVegetarian())
                .isVegan(menuItem.getIsVegan())
                .isSpicy(menuItem.getIsSpicy())
                .preparationTime(menuItem.getPreparationTime())
                .calories(menuItem.getCalories())
                .isAvailable(menuItem.getIsAvailable())
                .isFeatured(menuItem.getIsFeatured())
                .rating(menuItem.getRating())
                .totalOrders(menuItem.getTotalOrders())
                .build();
    }
}
