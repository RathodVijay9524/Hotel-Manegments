package com.vijay.User_Master.controller.hotel;

import com.vijay.User_Master.entity.hotel.RestaurantTable;
import com.vijay.User_Master.repository.hotel.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotel/tables")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RestaurantTableController {
    
    private final RestaurantTableRepository tableRepository;
    
    @PostMapping
    public ResponseEntity<RestaurantTable> createTable(@RequestBody RestaurantTable table) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tableRepository.save(table));
    }
    
    @GetMapping
    public ResponseEntity<List<RestaurantTable>> getAllTables() {
        return ResponseEntity.ok(tableRepository.findAll());
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<RestaurantTable>> getAvailableTables() {
        return ResponseEntity.ok(tableRepository.findByIsAvailableTrue());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantTable> getTableById(@PathVariable Long id) {
        return tableRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/number/{tableNumber}")
    public ResponseEntity<RestaurantTable> getTableByNumber(@PathVariable String tableNumber) {
        return tableRepository.findByTableNumber(tableNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/location/{location}")
    public ResponseEntity<List<RestaurantTable>> getTablesByLocation(@PathVariable String location) {
        return ResponseEntity.ok(tableRepository.findByLocation(location));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantTable> updateTable(@PathVariable Long id, @RequestBody RestaurantTable tableDetails) {
        return tableRepository.findById(id)
                .map(table -> {
                    table.setTableNumber(tableDetails.getTableNumber());
                    table.setTableName(tableDetails.getTableName());
                    table.setCapacity(tableDetails.getCapacity());
                    table.setLocation(tableDetails.getLocation());
                    table.setQrCode(tableDetails.getQrCode());
                    table.setIsAvailable(tableDetails.getIsAvailable());
                    return ResponseEntity.ok(tableRepository.save(table));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        if (tableRepository.existsById(id)) {
            tableRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
