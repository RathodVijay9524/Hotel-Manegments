# 🚚📅 DELIVERY TRACKING & TABLE RESERVATION GUIDE

## 📋 Overview

Advanced features added to the Hotel Management System:
1. **Delivery Tracking** - Real-time order delivery tracking with GPS
2. **Table Reservation** - Online table booking system

---

## 🚚 DELIVERY TRACKING SYSTEM

### **Features**
- ✅ Delivery agent management
- ✅ Real-time GPS location tracking
- ✅ Auto-assignment of delivery agents
- ✅ Delivery status updates
- ✅ Estimated delivery time
- ✅ Distance calculation
- ✅ Agent rating system
- ✅ Delivery history

### **Database Tables**
1. `hotel_delivery_agents` - Delivery personnel
2. `hotel_delivery_tracking` - Order delivery tracking

---

## 📝 DELIVERY AGENT MANAGEMENT

### **Create Delivery Agent**

```powershell
POST /api/hotel/delivery/agents

$agent = @{
    name = "John Doe"
    phone = "9876543210"
    email = "john@delivery.com"
    vehicleType = "Bike"
    vehicleNumber = "MH12AB1234"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/delivery/agents" `
  -Method POST -Body $agent -ContentType "application/json" -Headers $headers
```

### **Get All Agents**

```powershell
GET /api/hotel/delivery/agents

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/delivery/agents" `
  -Method GET -Headers $headers
```

### **Get Available Agents**

```powershell
GET /api/hotel/delivery/agents/available

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/delivery/agents/available" `
  -Method GET -Headers $headers
```

### **Update Agent Status**

```powershell
PATCH /api/hotel/delivery/agents/{id}/status

$status = @{
    isAvailable = $true
    isOnline = $true
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/delivery/agents/1/status" `
  -Method PATCH -Body $status -ContentType "application/json" -Headers $headers
```

### **Update Agent Location (GPS)**

```powershell
PATCH /api/hotel/delivery/agents/{id}/location

$location = @{
    latitude = 19.0760
    longitude = 72.8777
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/delivery/agents/1/location" `
  -Method PATCH -Body $location -ContentType "application/json" -Headers $headers
```

---

## 🗺️ DELIVERY TRACKING

### **Create Delivery Tracking**

```powershell
POST /api/hotel/delivery/tracking

$tracking = @{
    orderId = 1
    pickupLatitude = 19.0760
    pickupLongitude = 72.8777
    deliveryLatitude = 19.1197
    deliveryLongitude = 72.9081
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/delivery/tracking" `
  -Method POST -Body $tracking -ContentType "application/json" -Headers $headers
```

### **Assign Agent to Delivery**

```powershell
POST /api/hotel/delivery/tracking/{id}/assign/{agentId}

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/delivery/tracking/1/assign/1" `
  -Method POST -Headers $headers
```

### **Auto-Assign Agent**

```powershell
POST /api/hotel/delivery/tracking/{id}/auto-assign

# System automatically assigns the best available agent
Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/delivery/tracking/1/auto-assign" `
  -Method POST -Headers $headers
```

### **Update Delivery Status**

```powershell
PATCH /api/hotel/delivery/tracking/{id}/status

$status = @{
    status = "PICKED_UP"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/delivery/tracking/1/status" `
  -Method PATCH -Body $status -ContentType "application/json" -Headers $headers
```

**Available Status Values:**
- `PENDING` - Waiting for agent
- `ASSIGNED` - Agent assigned
- `PICKED_UP` - Food picked up
- `IN_TRANSIT` - On the way
- `ARRIVED` - Reached customer
- `DELIVERED` - Successfully delivered
- `CANCELLED` - Cancelled
- `FAILED` - Failed

### **Update Current Location (Real-time GPS)**

```powershell
PATCH /api/hotel/delivery/tracking/{id}/location

$location = @{
    latitude = 19.0896
    longitude = 72.8656
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/delivery/tracking/1/location" `
  -Method PATCH -Body $location -ContentType "application/json" -Headers $headers
```

### **Track Order Delivery**

```powershell
GET /api/hotel/delivery/tracking/order/{orderId}

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/delivery/tracking/order/1" `
  -Method GET -Headers $headers
```

### **Get Active Deliveries**

```powershell
GET /api/hotel/delivery/tracking/active

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/delivery/tracking/active" `
  -Method GET -Headers $headers
```

### **Get Agent's Deliveries**

```powershell
GET /api/hotel/delivery/tracking/agent/{agentId}

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/delivery/tracking/agent/1" `
  -Method GET -Headers $headers
```

---

## 📅 TABLE RESERVATION SYSTEM

### **Features**
- ✅ Online table booking
- ✅ Availability checking
- ✅ Multiple guest support
- ✅ Special requests
- ✅ Occasion tracking (Birthday, Anniversary, etc.)
- ✅ High chair & wheelchair accessibility
- ✅ Check-in/check-out
- ✅ Reservation history
- ✅ No-show tracking

### **Database Table**
- `hotel_table_reservations` - Table bookings

---

## 🪑 RESTAURANT TABLE MANAGEMENT

### **Create Table**

```powershell
POST /api/hotel/tables

$table = @{
    tableNumber = "T01"
    tableName = "Window Table 1"
    capacity = 4
    location = "Ground Floor"
    isAvailable = $true
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/tables" `
  -Method POST -Body $table -ContentType "application/json" -Headers $headers
```

### **Get All Tables**

```powershell
GET /api/hotel/tables

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/tables" `
  -Method GET -Headers $headers
```

### **Get Available Tables**

```powershell
GET /api/hotel/tables/available

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/tables/available" `
  -Method GET -Headers $headers
```

---

## 📝 TABLE RESERVATIONS

### **Create Reservation**

```powershell
POST /api/hotel/reservations

$reservation = @{
    userId = 18
    customerName = "Karina"
    customerPhone = "9876543210"
    customerEmail = "karina@gmail.com"
    tableId = 1
    numberOfGuests = 4
    reservationDateTime = "2025-10-05T19:30:00"
    durationMinutes = 120
    specialRequests = "Window seat preferred"
    occasion = "Birthday"
    requiresHighChair = $false
    requiresWheelchairAccess = $false
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reservations" `
  -Method POST -Body $reservation -ContentType "application/json" -Headers $headers
```

### **Get Reservation by ID**

```powershell
GET /api/hotel/reservations/{id}

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reservations/1" `
  -Method GET -Headers $headers
```

### **Get Reservation by Number**

```powershell
GET /api/hotel/reservations/number/{reservationNumber}

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reservations/number/RES-20251001120000" `
  -Method GET -Headers $headers
```

### **Get User's Reservations**

```powershell
GET /api/hotel/reservations/user/{userId}

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reservations/user/18" `
  -Method GET -Headers $headers
```

### **Get Upcoming Reservations**

```powershell
GET /api/hotel/reservations/upcoming

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reservations/upcoming" `
  -Method GET -Headers $headers
```

### **Get Reservations by Date**

```powershell
GET /api/hotel/reservations/date/{date}

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reservations/date/2025-10-05T00:00:00" `
  -Method GET -Headers $headers
```

### **Get Reservations by Status**

```powershell
GET /api/hotel/reservations/status/{status}

# Get confirmed reservations
Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reservations/status/CONFIRMED" `
  -Method GET -Headers $headers
```

**Available Status Values:**
- `PENDING` - Awaiting confirmation
- `CONFIRMED` - Confirmed
- `CHECKED_IN` - Customer arrived
- `COMPLETED` - Completed
- `CANCELLED` - Cancelled
- `NO_SHOW` - Customer didn't show up

### **Confirm Reservation**

```powershell
POST /api/hotel/reservations/{id}/confirm

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reservations/1/confirm" `
  -Method POST -Headers $headers
```

### **Check-In Customer**

```powershell
POST /api/hotel/reservations/{id}/check-in

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reservations/1/check-in" `
  -Method POST -Headers $headers
```

### **Check-Out Customer**

```powershell
POST /api/hotel/reservations/{id}/check-out

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reservations/1/check-out" `
  -Method POST -Headers $headers
```

### **Cancel Reservation**

```powershell
POST /api/hotel/reservations/{id}/cancel

$cancel = @{
    reason = "Customer changed plans"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reservations/1/cancel" `
  -Method POST -Body $cancel -ContentType "application/json" -Headers $headers
```

### **Check Table Availability**

```powershell
GET /api/hotel/reservations/tables/{tableId}/check-availability?reservationTime=2025-10-05T19:30:00&durationMinutes=120

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reservations/tables/1/check-availability?reservationTime=2025-10-05T19:30:00&durationMinutes=120" `
  -Method GET -Headers $headers
```

### **Find Available Tables**

```powershell
GET /api/hotel/reservations/tables/available?reservationTime=2025-10-05T19:30:00&numberOfGuests=4&durationMinutes=120

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reservations/tables/available?reservationTime=2025-10-05T19:30:00&numberOfGuests=4&durationMinutes=120" `
  -Method GET -Headers $headers
```

---

## 🎯 COMPLETE WORKFLOW EXAMPLES

### **Delivery Workflow**

```powershell
Write-Host "=== DELIVERY TRACKING WORKFLOW ===" -ForegroundColor Cyan

# 1. Create delivery agent
$agent = @{
    name = "Delivery John"
    phone = "9998887777"
    email = "john@delivery.com"
    vehicleType = "Bike"
    vehicleNumber = "MH01XY1234"
} | ConvertTo-Json

$agentRes = Invoke-WebRequest -Uri "$baseUrl/api/hotel/delivery/agents" `
  -Method POST -Body $agent -ContentType "application/json" -Headers $headers
$agentData = $agentRes.Content | ConvertFrom-Json
Write-Host "✅ Agent created: $($agentData.name)"

# 2. Mark agent online
$status = @{ isAvailable = $true; isOnline = $true } | ConvertTo-Json
Invoke-WebRequest -Uri "$baseUrl/api/hotel/delivery/agents/$($agentData.id)/status" `
  -Method PATCH -Body $status -ContentType "application/json" -Headers $headers
Write-Host "✅ Agent is now online"

# 3. Create delivery tracking for an order
$tracking = @{
    orderId = 1
    pickupLatitude = 19.0760
    pickupLongitude = 72.8777
    deliveryLatitude = 19.1197
    deliveryLongitude = 72.9081
} | ConvertTo-Json

$trackRes = Invoke-WebRequest -Uri "$baseUrl/api/hotel/delivery/tracking" `
  -Method POST -Body $tracking -ContentType "application/json" -Headers $headers
$trackData = $trackRes.Content | ConvertFrom-Json
Write-Host "✅ Delivery tracking created"

# 4. Auto-assign agent
Invoke-WebRequest -Uri "$baseUrl/api/hotel/delivery/tracking/$($trackData.id)/auto-assign" `
  -Method POST -Headers $headers
Write-Host "✅ Agent auto-assigned"

# 5. Update status to PICKED_UP
$pickedUp = @{ status = "PICKED_UP" } | ConvertTo-Json
Invoke-WebRequest -Uri "$baseUrl/api/hotel/delivery/tracking/$($trackData.id)/status" `
  -Method PATCH -Body $pickedUp -ContentType "application/json" -Headers $headers
Write-Host "✅ Food picked up from restaurant"

# 6. Update location (simulate GPS tracking)
$location = @{ latitude = 19.0896; longitude = 72.8656 } | ConvertTo-Json
Invoke-WebRequest -Uri "$baseUrl/api/hotel/delivery/tracking/$($trackData.id)/location" `
  -Method PATCH -Body $location -ContentType "application/json" -Headers $headers
Write-Host "✅ Location updated"

# 7. Mark as delivered
$delivered = @{ status = "DELIVERED" } | ConvertTo-Json
Invoke-WebRequest -Uri "$baseUrl/api/hotel/delivery/tracking/$($trackData.id)/status" `
  -Method PATCH -Body $delivered -ContentType "application/json" -Headers $headers
Write-Host "✅ Delivery completed!"
```

### **Reservation Workflow**

```powershell
Write-Host "=== TABLE RESERVATION WORKFLOW ===" -ForegroundColor Cyan

# 1. Create restaurant table
$table = @{
    tableNumber = "T01"
    tableName = "VIP Table 1"
    capacity = 4
    location = "Ground Floor - Window Side"
    isAvailable = $true
} | ConvertTo-Json

$tableRes = Invoke-WebRequest -Uri "$baseUrl/api/hotel/tables" `
  -Method POST -Body $table -ContentType "application/json" -Headers $headers
$tableData = $tableRes.Content | ConvertFrom-Json
Write-Host "✅ Table created: $($tableData.tableNumber)"

# 2. Check availability
$checkUrl = "$baseUrl/api/hotel/reservations/tables/$($tableData.id)/check-availability?reservationTime=2025-10-05T19:30:00&durationMinutes=120"
$availRes = Invoke-WebRequest -Uri $checkUrl -Method GET -Headers $headers
$isAvailable = ($availRes.Content | ConvertFrom-Json).available
Write-Host "✅ Table availability: $isAvailable"

# 3. Create reservation
$reservation = @{
    userId = 18
    customerName = "Karina"
    customerPhone = "9876543210"
    customerEmail = "karina@gmail.com"
    tableId = $tableData.id
    numberOfGuests = 4
    reservationDateTime = "2025-10-05T19:30:00"
    durationMinutes = 120
    specialRequests = "Birthday celebration - need cake plates"
    occasion = "Birthday"
    requiresHighChair = $false
    requiresWheelchairAccess = $false
} | ConvertTo-Json

$resRes = Invoke-WebRequest -Uri "$baseUrl/api/hotel/reservations" `
  -Method POST -Body $reservation -ContentType "application/json" -Headers $headers
$resData = $resRes.Content | ConvertFrom-Json
Write-Host "✅ Reservation created: $($resData.reservationNumber)"

# 4. Confirm reservation
Invoke-WebRequest -Uri "$baseUrl/api/hotel/reservations/$($resData.id)/confirm" `
  -Method POST -Headers $headers
Write-Host "✅ Reservation confirmed"

# 5. Check-in customer (when they arrive)
Invoke-WebRequest -Uri "$baseUrl/api/hotel/reservations/$($resData.id)/check-in" `
  -Method POST -Headers $headers
Write-Host "✅ Customer checked in"

# 6. Check-out customer (when they leave)
Invoke-WebRequest -Uri "$baseUrl/api/hotel/reservations/$($resData.id)/check-out" `
  -Method POST -Headers $headers
Write-Host "✅ Customer checked out. Table available again!"
```

---

## 📊 API ENDPOINTS SUMMARY

### **Delivery System (13 endpoints)**
```
POST   /api/hotel/delivery/agents                      - Create agent
GET    /api/hotel/delivery/agents                      - Get all agents
GET    /api/hotel/delivery/agents/available            - Get available agents
PATCH  /api/hotel/delivery/agents/{id}/status          - Update agent status
PATCH  /api/hotel/delivery/agents/{id}/location        - Update agent location

POST   /api/hotel/delivery/tracking                    - Create tracking
POST   /api/hotel/delivery/tracking/{id}/assign/{aid}  - Assign agent
POST   /api/hotel/delivery/tracking/{id}/auto-assign   - Auto-assign agent
PATCH  /api/hotel/delivery/tracking/{id}/status        - Update delivery status
PATCH  /api/hotel/delivery/tracking/{id}/location      - Update GPS location
GET    /api/hotel/delivery/tracking/order/{orderId}    - Track by order
GET    /api/hotel/delivery/tracking/active             - Get active deliveries
GET    /api/hotel/delivery/tracking/agent/{agentId}    - Get agent deliveries
```

### **Table Management (7 endpoints)**
```
POST   /api/hotel/tables                               - Create table
GET    /api/hotel/tables                               - Get all tables
GET    /api/hotel/tables/available                     - Get available tables
GET    /api/hotel/tables/{id}                          - Get table by ID
GET    /api/hotel/tables/number/{number}               - Get by table number
GET    /api/hotel/tables/location/{location}           - Get by location
PUT    /api/hotel/tables/{id}                          - Update table
DELETE /api/hotel/tables/{id}                          - Delete table
```

### **Reservation System (14 endpoints)**
```
POST   /api/hotel/reservations                         - Create reservation
GET    /api/hotel/reservations/{id}                    - Get by ID
GET    /api/hotel/reservations/number/{number}         - Get by number
GET    /api/hotel/reservations/user/{userId}           - Get user reservations
GET    /api/hotel/reservations/upcoming                - Get upcoming
GET    /api/hotel/reservations/date/{date}             - Get by date
GET    /api/hotel/reservations/status/{status}         - Get by status
POST   /api/hotel/reservations/{id}/confirm            - Confirm
POST   /api/hotel/reservations/{id}/check-in           - Check-in
POST   /api/hotel/reservations/{id}/check-out          - Check-out
POST   /api/hotel/reservations/{id}/cancel             - Cancel
PATCH  /api/hotel/reservations/{id}/status             - Update status
GET    /api/hotel/reservations/tables/available        - Find available tables
GET    /api/hotel/reservations/tables/{id}/check       - Check availability
```

**Total New Endpoints**: 34+

---

## 🎨 ADVANCED FEATURES

### **Real-time GPS Tracking**
- Agent location updates
- Customer can see delivery person approaching
- Live map integration ready
- Distance calculation

### **Smart Agent Assignment**
- Auto-assigns to least busy agent
- Considers agent rating
- Considers agent location (future)
- Load balancing

### **Table Conflict Prevention**
- Automatically checks overlapping reservations
- Prevents double-booking
- Time-slot based availability

### **Special Requirements**
- High chair for kids
- Wheelchair accessibility
- Dietary restrictions
- Occasion celebrations

---

## 🚀 NEXT STEPS

### **Phase 1: Integration with Frontend**
- Build delivery tracking map
- Real-time WebSocket updates
- Push notifications
- SMS/Email confirmations

### **Phase 2: Enhanced Features**
- Multi-language support
- Payment integration for reservations
- Loyalty points
- Review delivery experience
- Recurring reservations
- Group bookings

### **Phase 3: MCP Tool Conversion**
```
User: "Where is my delivery?"
Bot: [Calls track_delivery() MCP tool]
Bot: "Your delivery is 2 km away. Agent: John on bike MH01XY1234
     ETA: 8 minutes"

User: "I want to book a table for 4 people tomorrow at 7 PM"
Bot: [Calls create_reservation() MCP tool]
Bot: "Table reserved! Reservation #RES-20251002190000
     Table: T01 (Window Side)
     Date: Oct 2, 2025 at 7:00 PM
     Guests: 4"
```

---

## ✅ SUMMARY

**New Components Added:**
- 3 new entities (DeliveryAgent, DeliveryTracking, TableReservation)
- 3 new repositories
- 2 new services
- 3 new controllers
- 6 new DTOs
- 34+ new API endpoints
- ~2000+ lines of code

**Total System Now:**
- 10 entities
- 10 repositories
- 6 services
- 7 controllers
- 73+ API endpoints
- 5,500+ lines of code

**Status**: 🟢 Ready for testing!
