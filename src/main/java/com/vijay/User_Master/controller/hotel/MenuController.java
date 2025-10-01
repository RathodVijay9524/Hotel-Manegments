package com.vijay.User_Master.controller.hotel;

import com.vijay.User_Master.dto.hotel.CategoryDTO;
import com.vijay.User_Master.dto.hotel.MenuItemDTO;
import com.vijay.User_Master.service.hotel.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotel/menu")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MenuController {
    
    private final MenuService menuService;
    
    // ==================== CATEGORY ENDPOINTS ====================
    
    @PostMapping("/categories")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.createCategory(dto));
    }
    
    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        return ResponseEntity.ok(menuService.updateCategory(id, dto));
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(menuService.getAllCategories());
    }
    
    @GetMapping("/categories/active")
    public ResponseEntity<List<CategoryDTO>> getActiveCategories() {
        return ResponseEntity.ok(menuService.getActiveCategories());
    }
    
    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.getCategoryById(id));
    }
    
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        menuService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
    
    // ==================== MENU ITEM ENDPOINTS ====================
    
    @PostMapping("/items")
    public ResponseEntity<MenuItemDTO> createMenuItem(@RequestBody MenuItemDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.createMenuItem(dto));
    }
    
    @PutMapping("/items/{id}")
    public ResponseEntity<MenuItemDTO> updateMenuItem(@PathVariable Long id, @RequestBody MenuItemDTO dto) {
        return ResponseEntity.ok(menuService.updateMenuItem(id, dto));
    }
    
    @GetMapping("/items")
    public ResponseEntity<List<MenuItemDTO>> getAllMenuItems() {
        return ResponseEntity.ok(menuService.getAllMenuItems());
    }
    
    @GetMapping("/items/category/{categoryId}")
    public ResponseEntity<List<MenuItemDTO>> getMenuItemsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(menuService.getMenuItemsByCategory(categoryId));
    }
    
    @GetMapping("/items/featured")
    public ResponseEntity<List<MenuItemDTO>> getFeaturedMenuItems() {
        return ResponseEntity.ok(menuService.getFeaturedMenuItems());
    }
    
    @GetMapping("/items/vegetarian")
    public ResponseEntity<List<MenuItemDTO>> getVegetarianMenuItems() {
        return ResponseEntity.ok(menuService.getVegetarianMenuItems());
    }
    
    @GetMapping("/items/search")
    public ResponseEntity<Page<MenuItemDTO>> searchMenuItems(
            @RequestParam String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(menuService.searchMenuItems(keyword, pageable));
    }
    
    @GetMapping("/items/{id}")
    public ResponseEntity<MenuItemDTO> getMenuItemById(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.getMenuItemById(id));
    }
    
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}
