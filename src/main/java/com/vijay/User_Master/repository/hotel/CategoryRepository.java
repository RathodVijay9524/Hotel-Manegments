package com.vijay.User_Master.repository.hotel;

import com.vijay.User_Master.entity.hotel.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findByIsActiveTrueOrderByDisplayOrderAsc();
    
    List<Category> findAllByOrderByDisplayOrderAsc();
}
