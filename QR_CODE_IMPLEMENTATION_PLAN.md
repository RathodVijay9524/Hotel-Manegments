# 🎯 QR CODE ORDERING - IMPLEMENTATION PLAN

**Date:** October 1, 2025, 6:07 PM  
**Status:** Ready to Implement!

---

## 📊 CURRENT STATUS

**✅ FOUNDATION COMPLETE:**
- Multi-tenant architecture ✅
- Business Context Filter ✅
- Orders system ✅
- Menu system ✅
- Data isolation ✅

**🚀 READY FOR:** QR Code Ordering!

---

## 🎯 WHAT WE'RE BUILDING

**Customer Experience:**
1. Customer sits at table in Karina's hotel
2. Scans QR code on table
3. Opens menu (Karina's menu only)
4. Orders food (no login needed)
5. Order goes to Karina's kitchen
6. Customer pays and leaves

**Business Benefit:**
- 40% more orders
- Reduced staff workload
- Contactless & hygienic
- Better customer experience

---

## 🏗️ IMPLEMENTATION STEPS

### **PHASE 1: Backend Entities (30 mins)**

#### **Step 1: Create QRCode Entity (10 mins)**
```java
@Entity
@Table(name = "qr_codes")
public class QRCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "business_id", nullable = false)
    private Long businessId;  // Links to hotel owner
    
    @Column(name = "table_id", nullable = false)
    private Long tableId;     // Links to restaurant table
    
    @Column(unique = true, nullable = false)
    private String qrToken;   // Unique token for security
    
    @Column(nullable = false)
    private String qrCodeUrl; // URL that opens when scanned
    
    @Lob
    private String qrCodeImage; // Base64 QR code image
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @CreatedDate
    private LocalDateTime createdAt;
}
```

#### **Step 2: Create GuestSession Entity (10 mins)**
```java
@Entity
@Table(name = "guest_sessions")
public class GuestSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "business_id", nullable = false)
    private Long businessId;  // From QR code
    
    @Column(name = "table_id", nullable = false)
    private Long tableId;     // From QR code
    
    @Column(unique = true, nullable = false)
    private String sessionToken; // For guest authentication
    
    private String guestName;
    private String guestPhone;
    
    @Enumerated(EnumType.STRING)
    private SessionStatus status; // ACTIVE, COMPLETED, EXPIRED
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    private LocalDateTime expiresAt;
    
    public enum SessionStatus {
        ACTIVE, COMPLETED, EXPIRED
    }
}
```

#### **Step 3: Create Repositories (10 mins)**
```java
// QRCodeRepository
public interface QRCodeRepository extends JpaRepository<QRCode, Long> {
    Optional<QRCode> findByQrToken(String qrToken);
    List<QRCode> findByBusinessId(Long businessId);
    Optional<QRCode> findByBusinessIdAndTableId(Long businessId, Long tableId);
    List<QRCode> findByBusinessIdAndIsActiveTrue(Long businessId);
}

// GuestSessionRepository
public interface GuestSessionRepository extends JpaRepository<GuestSession, Long> {
    Optional<GuestSession> findBySessionToken(String sessionToken);
    List<GuestSession> findByBusinessId(Long businessId);
    List<GuestSession> findByBusinessIdAndStatus(Long businessId, GuestSession.SessionStatus status);
    List<GuestSession> findByTableIdAndStatus(Long tableId, GuestSession.SessionStatus status);
}
```

---

### **PHASE 2: Backend Services (45 mins)**

#### **Step 4: Create QRCodeService (20 mins)**
```java
@Service
@RequiredArgsConstructor
public class QRCodeService {
    
    private final QRCodeRepository qrCodeRepository;
    private final RestaurantTableRepository tableRepository;
    private final BusinessContextFilter businessContext;
    
    // Generate QR code for a table
    public QRCodeDTO generateQRCodeForTable(Long tableId) {
        Long businessId = businessContext.getCurrentBusinessId();
        
        // Check if QR already exists
        Optional<QRCode> existing = qrCodeRepository
            .findByBusinessIdAndTableId(businessId, tableId);
        
        if (existing.isPresent()) {
            return mapToDTO(existing.get());
        }
        
        // Generate unique token
        String qrToken = generateUniqueToken();
        
        // Create QR code URL
        String qrUrl = String.format(
            "https://yourhotel.com/menu?token=%s", 
            qrToken
        );
        
        // Generate QR code image (Base64)
        String qrImage = generateQRCodeImage(qrUrl);
        
        QRCode qrCode = QRCode.builder()
            .businessId(businessId)
            .tableId(tableId)
            .qrToken(qrToken)
            .qrCodeUrl(qrUrl)
            .qrCodeImage(qrImage)
            .isActive(true)
            .build();
        
        qrCode = qrCodeRepository.save(qrCode);
        return mapToDTO(qrCode);
    }
    
    // Get all QR codes for current business
    public List<QRCodeDTO> getAllQRCodes() {
        Long businessId = businessContext.getCurrentBusinessId();
        return qrCodeRepository.findByBusinessId(businessId)
            .stream().map(this::mapToDTO).collect(Collectors.toList());
    }
    
    private String generateUniqueToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    private String generateQRCodeImage(String url) {
        // Use ZXing library to generate QR code
        // Returns Base64 encoded image
        // Implementation details...
    }
}
```

#### **Step 5: Create GuestOrderService (25 mins)**
```java
@Service
@RequiredArgsConstructor
public class GuestOrderService {
    
    private final QRCodeRepository qrCodeRepository;
    private final GuestSessionRepository sessionRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    
    // Scan QR code and create guest session
    public GuestSessionDTO scanQRCode(String qrToken) {
        // Validate QR code
        QRCode qrCode = qrCodeRepository.findByQrToken(qrToken)
            .orElseThrow(() -> new RuntimeException("Invalid QR code"));
        
        if (!qrCode.getIsActive()) {
            throw new RuntimeException("QR code is inactive");
        }
        
        // Create guest session
        String sessionToken = generateSessionToken();
        
        GuestSession session = GuestSession.builder()
            .businessId(qrCode.getBusinessId())
            .tableId(qrCode.getTableId())
            .sessionToken(sessionToken)
            .status(GuestSession.SessionStatus.ACTIVE)
            .expiresAt(LocalDateTime.now().plusHours(3))
            .build();
        
        session = sessionRepository.save(session);
        
        return GuestSessionDTO.builder()
            .sessionToken(sessionToken)
            .businessId(session.getBusinessId())
            .tableId(session.getTableId())
            .expiresAt(session.getExpiresAt())
            .build();
    }
    
    // Get menu for guest (filtered by business)
    public List<MenuItemDTO> getMenuForGuest(String sessionToken) {
        GuestSession session = validateSession(sessionToken);
        
        // Get menu items for this business
        return menuItemRepository
            .findByBusinessIdAndIsAvailableTrue(session.getBusinessId())
            .stream().map(this::mapToDTO).collect(Collectors.toList());
    }
    
    // Place order as guest
    public OrderDTO placeGuestOrder(String sessionToken, GuestOrderRequest request) {
        GuestSession session = validateSession(sessionToken);
        
        // Create order with business_id from session
        Order order = Order.builder()
            .businessId(session.getBusinessId())  // From guest session!
            .orderNumber(generateOrderNumber())
            .customerName(request.getGuestName())
            .customerPhone(request.getGuestPhone())
            .tableId(session.getTableId())
            .orderType(Order.OrderType.DINE_IN)
            .status(Order.OrderStatus.PENDING)
            .build();
        
        // Add order items...
        // Calculate totals...
        
        order = orderRepository.save(order);
        
        // Update session with guest info
        session.setGuestName(request.getGuestName());
        session.setGuestPhone(request.getGuestPhone());
        sessionRepository.save(session);
        
        return mapToOrderDTO(order);
    }
    
    private GuestSession validateSession(String sessionToken) {
        GuestSession session = sessionRepository.findBySessionToken(sessionToken)
            .orElseThrow(() -> new RuntimeException("Invalid session"));
        
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Session expired");
        }
        
        return session;
    }
}
```

---

### **PHASE 3: Public API Endpoints (30 mins)**

#### **Step 6: Create GuestOrderController (30 mins)**
```java
@RestController
@RequestMapping("/api/public/guest")
@RequiredArgsConstructor
public class GuestOrderController {
    
    private final GuestOrderService guestOrderService;
    
    // Scan QR code
    @GetMapping("/scan")
    public ResponseEntity<GuestSessionDTO> scanQRCode(@RequestParam String token) {
        GuestSessionDTO session = guestOrderService.scanQRCode(token);
        return ResponseEntity.ok(session);
    }
    
    // Get menu (no authentication needed)
    @GetMapping("/menu")
    public ResponseEntity<MenuResponse> getMenu(
        @RequestHeader("X-Guest-Session") String sessionToken
    ) {
        List<MenuItemDTO> menu = guestOrderService.getMenuForGuest(sessionToken);
        return ResponseEntity.ok(new MenuResponse(menu));
    }
    
    // Place order (no authentication needed)
    @PostMapping("/orders")
    public ResponseEntity<OrderDTO> placeOrder(
        @RequestHeader("X-Guest-Session") String sessionToken,
        @RequestBody GuestOrderRequest request
    ) {
        OrderDTO order = guestOrderService.placeGuestOrder(sessionToken, request);
        return ResponseEntity.ok(order);
    }
    
    // Get order status
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDTO> getOrderStatus(
        @PathVariable Long orderId,
        @RequestHeader("X-Guest-Session") String sessionToken
    ) {
        OrderDTO order = guestOrderService.getOrderStatus(orderId, sessionToken);
        return ResponseEntity.ok(order);
    }
}
```

---

### **PHASE 4: Owner Dashboard (30 mins)**

#### **Step 7: QR Code Management Controller**
```java
@RestController
@RequestMapping("/api/hotel/qr-codes")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
public class QRCodeController {
    
    private final QRCodeService qrCodeService;
    
    // Generate QR code for table
    @PostMapping("/generate/{tableId}")
    public ResponseEntity<QRCodeDTO> generateQRCode(@PathVariable Long tableId) {
        QRCodeDTO qrCode = qrCodeService.generateQRCodeForTable(tableId);
        return ResponseEntity.ok(qrCode);
    }
    
    // Get all QR codes
    @GetMapping
    public ResponseEntity<List<QRCodeDTO>> getAllQRCodes() {
        List<QRCodeDTO> qrCodes = qrCodeService.getAllQRCodes();
        return ResponseEntity.ok(qrCodes);
    }
    
    // Download QR code image
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadQRCode(@PathVariable Long id) {
        byte[] qrImage = qrCodeService.getQRCodeImage(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentDispositionFormData("attachment", "qr-code.png");
        return ResponseEntity.ok().headers(headers).body(qrImage);
    }
}
```

---

### **PHASE 5: Frontend (Optional - 2 hours)**

Basic guest ordering interface:
- QR code scan redirects to menu page
- Menu display (filtered by business)
- Add to cart functionality
- Checkout and place order
- Order status tracking

---

## 📊 IMPLEMENTATION TIMELINE

| Phase | Task | Time | Status |
|-------|------|------|--------|
| 1 | Create Entities | 30 mins | ⏳ |
| 2 | Create Services | 45 mins | ⏳ |
| 3 | Create Public APIs | 30 mins | ⏳ |
| 4 | Owner Dashboard | 30 mins | ⏳ |
| 5 | Frontend (Optional) | 2 hours | ⏳ |

**Total Backend:** ~2 hours  
**Total with Frontend:** ~4 hours

---

## 🎯 WHAT YOU GET

**For Customers:**
- Scan QR code at table
- See menu (only for that hotel)
- Order food without app/login
- Track order status
- Contactless experience

**For Business Owners (Karina):**
- Generate QR codes for tables
- Download/print QR codes
- Track guest orders
- Automatic business_id assignment
- Orders go to their kitchen only

**Technical:**
- Perfect business isolation
- Guest sessions scoped to business
- Menu filtered by business
- Orders automatically routed correctly

---

## 🚀 LET'S START!

**Shall we begin with Phase 1?**

I'll create:
1. QRCode entity
2. GuestSession entity
3. Repositories
4. Services
5. Controllers

**Ready to proceed?** 🎯
