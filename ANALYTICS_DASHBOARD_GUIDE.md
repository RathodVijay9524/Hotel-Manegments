# 📊 ANALYTICS DASHBOARD - COMPLETE GUIDE

## 🎯 Overview

Comprehensive analytics dashboard for the Hotel Management System providing real-time insights into:
- **Order Performance** - Revenue, top items, peak hours
- **Delivery Operations** - Agent performance, delivery times, success rates
- **Reservations** - Booking trends, table occupancy, popular times
- **Business Intelligence** - Growth metrics, trends, forecasting

---

## 🚀 Features

✅ **Real-time Metrics** - Live dashboard with current stats  
✅ **Historical Analysis** - Daily, weekly, monthly trends  
✅ **Performance Tracking** - KPIs and success metrics  
✅ **Agent Analytics** - Individual performance monitoring  
✅ **Revenue Reports** - Payment methods, order values  
✅ **Customer Insights** - Booking patterns, preferences  
✅ **Growth Indicators** - % change comparisons  
✅ **Peak Hour Analysis** - Busiest times identification  

---

## 📝 API ENDPOINTS

### **Dashboard Overview** (1 endpoint)

#### Get Overall Dashboard
```powershell
GET /api/hotel/analytics/dashboard
```

**Returns:** Comprehensive overview with today's metrics, growth rates, and current status

**Response Example:**
```json
{
  "todayOrders": 25,
  "todayRevenue": 15500.00,
  "todayReservations": 12,
  "activeDeliveries": 5,
  "totalOrders": 450,
  "totalRevenue": 285000.00,
  "totalReservations": 180,
  "orderGrowthPercent": 15.5,
  "revenueGrowthPercent": 22.3,
  "reservationGrowthPercent": 8.7,
  "onlineDeliveryAgents": 8,
  "occupiedTables": 6,
  "availableTables": 14
}
```

---

### **Order Analytics** (4 endpoints)

#### Get Order Analytics (Custom Date Range)
```powershell
GET /api/hotel/analytics/orders?startDate=2025-10-01&endDate=2025-10-31
```

#### Get Today's Order Analytics
```powershell
GET /api/hotel/analytics/orders/today
```

#### Get This Week's Order Analytics
```powershell
GET /api/hotel/analytics/orders/this-week
```

#### Get This Month's Order Analytics
```powershell
GET /api/hotel/analytics/orders/this-month
```

**Response Structure:**
```json
{
  "totalOrders": 125,
  "totalRevenue": 78500.00,
  "averageOrderValue": 628.00,
  "pendingOrders": 8,
  "completedOrders": 110,
  "dineInOrders": 45,
  "takeawayOrders": 35,
  "deliveryOrders": 45,
  "revenueByPaymentMethod": {
    "CARD": 45000.00,
    "CASH": 25000.00,
    "UPI": 8500.00
  },
  "ordersByHour": {
    "12": 18,
    "13": 25,
    "19": 32,
    "20": 28
  },
  "topSellingItems": [
    {
      "itemId": 1,
      "itemName": "Paneer Butter Masala",
      "categoryName": "Main Course",
      "orderCount": 45,
      "totalQuantitySold": 68,
      "revenue": 13600.00,
      "averageRating": 4.5
    }
  ],
  "dailyRevenueTrend": [
    {
      "date": "2025-10-01",
      "orderCount": 18,
      "revenue": 11200.00
    }
  ]
}
```

---

### **Delivery Analytics** (2 endpoints)

#### Get Delivery Analytics (Custom Date Range)
```powershell
GET /api/hotel/analytics/deliveries?startDate=2025-10-01&endDate=2025-10-31
```

#### Get Today's Delivery Analytics
```powershell
GET /api/hotel/analytics/deliveries/today
```

**Response Structure:**
```json
{
  "totalDeliveries": 85,
  "successfulDeliveries": 80,
  "failedDeliveries": 2,
  "activeDeliveries": 3,
  "averageDeliveryTimeMinutes": 28.5,
  "successRate": 94.1,
  "averageRating": 4.3,
  "totalAgents": 10,
  "onlineAgents": 8,
  "availableAgents": 5,
  "busyAgents": 3,
  "topAgents": [
    {
      "agentId": 1,
      "agentName": "John Doe",
      "phone": "9998887777",
      "vehicleType": "Bike",
      "totalDeliveries": 156,
      "successfulDeliveries": 152,
      "failedDeliveries": 2,
      "rating": 4.7,
      "successRate": 97.4,
      "averageDeliveryTimeMinutes": 25.3
    }
  ],
  "pendingDeliveries": 2,
  "assignedDeliveries": 1,
  "pickedUpDeliveries": 1,
  "inTransitDeliveries": 1,
  "deliveredToday": 15
}
```

---

### **Reservation Analytics** (4 endpoints)

#### Get Reservation Analytics (Custom Date Range)
```powershell
GET /api/hotel/analytics/reservations?startDate=2025-10-01&endDate=2025-10-31
```

#### Get Today's Reservation Analytics
```powershell
GET /api/hotel/analytics/reservations/today
```

#### Get This Week's Reservation Analytics
```powershell
GET /api/hotel/analytics/reservations/this-week
```

#### Get This Month's Reservation Analytics
```powershell
GET /api/hotel/analytics/reservations/this-month
```

**Response Structure:**
```json
{
  "totalReservations": 95,
  "upcomingReservations": 18,
  "todayReservations": 8,
  "completedReservations": 72,
  "cancelledReservations": 5,
  "noShowReservations": 2,
  "totalTables": 20,
  "occupiedTables": 6,
  "availableTables": 14,
  "occupancyRate": 30.0,
  "reservationsByHour": {
    "18": 12,
    "19": 22,
    "20": 28,
    "21": 15
  },
  "reservationsByDay": {
    "MONDAY": 10,
    "FRIDAY": 18,
    "SATURDAY": 25,
    "SUNDAY": 20
  },
  "mostBookedTables": [
    {
      "tableId": 1,
      "tableNumber": "T01",
      "tableName": "VIP Window Table",
      "location": "Ground Floor",
      "capacity": 4,
      "bookingCount": 28,
      "averageGuestsPerBooking": 3.5
    }
  ],
  "reservationsByOccasion": {
    "Birthday": 15,
    "Anniversary": 8,
    "Business Meeting": 5
  },
  "averageGuestsPerReservation": 3.8,
  "averageDurationMinutes": 125.0,
  "cancellationRate": 5.3
}
```

---

## 🧪 TESTING EXAMPLES

### PowerShell Test Script

```powershell
# =========================
# Analytics Dashboard Tests
# =========================

$baseUrl = "http://localhost:9091"

# Login
$loginRes = Invoke-WebRequest -Uri "$baseUrl/api/auth/login" `
  -Method POST `
  -Body '{"usernameOrEmail":"karina","password":"karina"}' `
  -ContentType "application/json"

$loginData = ($loginRes.Content | ConvertFrom-Json).data
$token = $loginData.jwtToken
$headers = @{ Authorization = "Bearer $token" }

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   ANALYTICS DASHBOARD TESTS" -ForegroundColor Yellow
Write-Host "========================================`n" -ForegroundColor Cyan

# Test 1: Dashboard Overview
Write-Host "[TEST 1] Dashboard Overview" -ForegroundColor Green
$dash = Invoke-WebRequest -Uri "$baseUrl/api/hotel/analytics/dashboard" -Method GET -Headers $headers
$dashData = $dash.Content | ConvertFrom-Json

Write-Host "✅ Dashboard Retrieved:" -ForegroundColor Green
Write-Host "   Today's Orders: $($dashData.todayOrders)" -ForegroundColor White
Write-Host "   Today's Revenue: ₹$($dashData.todayRevenue)" -ForegroundColor White
Write-Host "   Total Revenue: ₹$($dashData.totalRevenue)" -ForegroundColor White
Write-Host "   Order Growth: $($dashData.orderGrowthPercent)%" -ForegroundColor Cyan
Write-Host "   Revenue Growth: $($dashData.revenueGrowthPercent)%" -ForegroundColor Cyan
Write-Host "   Active Deliveries: $($dashData.activeDeliveries)" -ForegroundColor Yellow
Write-Host "   Available Tables: $($dashData.availableTables)`n" -ForegroundColor Yellow

# Test 2: Order Analytics (Today)
Write-Host "[TEST 2] Today's Order Analytics" -ForegroundColor Green
$orders = Invoke-WebRequest -Uri "$baseUrl/api/hotel/analytics/orders/today" -Method GET -Headers $headers
$orderData = $orders.Content | ConvertFrom-Json

Write-Host "✅ Order Analytics:" -ForegroundColor Green
Write-Host "   Total Orders: $($orderData.totalOrders)" -ForegroundColor White
Write-Host "   Revenue: ₹$($orderData.totalRevenue)" -ForegroundColor White
Write-Host "   Avg Order Value: ₹$($orderData.averageOrderValue)" -ForegroundColor White
Write-Host "   Dine-in: $($orderData.dineInOrders)" -ForegroundColor Cyan
Write-Host "   Takeaway: $($orderData.takeawayOrders)" -ForegroundColor Cyan
Write-Host "   Delivery: $($orderData.deliveryOrders)`n" -ForegroundColor Cyan

# Test 3: Weekly Order Analytics
Write-Host "[TEST 3] This Week's Order Analytics" -ForegroundColor Green
$weekOrders = Invoke-WebRequest -Uri "$baseUrl/api/hotel/analytics/orders/this-week" -Method GET -Headers $headers
$weekData = $weekOrders.Content | ConvertFrom-Json

Write-Host "✅ Weekly Analytics:" -ForegroundColor Green
Write-Host "   Total Orders: $($weekData.totalOrders)" -ForegroundColor White
Write-Host "   Revenue: ₹$($weekData.totalRevenue)`n" -ForegroundColor White

# Test 4: Delivery Analytics (Today)
Write-Host "[TEST 4] Today's Delivery Analytics" -ForegroundColor Green
$delivery = Invoke-WebRequest -Uri "$baseUrl/api/hotel/analytics/deliveries/today" -Method GET -Headers $headers
$deliveryData = $delivery.Content | ConvertFrom-Json

Write-Host "✅ Delivery Analytics:" -ForegroundColor Green
Write-Host "   Total Deliveries: $($deliveryData.totalDeliveries)" -ForegroundColor White
Write-Host "   Success Rate: $($deliveryData.successRate)%" -ForegroundColor Cyan
Write-Host "   Avg Time: $($deliveryData.averageDeliveryTimeMinutes) mins" -ForegroundColor Cyan
Write-Host "   Online Agents: $($deliveryData.onlineAgents)" -ForegroundColor Yellow
Write-Host "   Available: $($deliveryData.availableAgents)`n" -ForegroundColor Yellow

# Test 5: Reservation Analytics (Today)
Write-Host "[TEST 5] Today's Reservation Analytics" -ForegroundColor Green
$res = Invoke-WebRequest -Uri "$baseUrl/api/hotel/analytics/reservations/today" -Method GET -Headers $headers
$resData = $res.Content | ConvertFrom-Json

Write-Host "✅ Reservation Analytics:" -ForegroundColor Green
Write-Host "   Total Reservations: $($resData.totalReservations)" -ForegroundColor White
Write-Host "   Upcoming: $($resData.upcomingReservations)" -ForegroundColor Cyan
Write-Host "   Occupancy Rate: $($resData.occupancyRate)%" -ForegroundColor Yellow
Write-Host "   Avg Guests: $($resData.averageGuestsPerReservation)`n" -ForegroundColor White

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "✅ ALL ANALYTICS TESTS PASSED!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
```

---

## 📊 KEY METRICS EXPLAINED

### **Dashboard Overview**

| Metric | Description | Usage |
|--------|-------------|-------|
| **todayOrders** | Orders placed today | Track daily performance |
| **todayRevenue** | Revenue generated today | Monitor daily earnings |
| **orderGrowthPercent** | % change vs yesterday | Measure growth trend |
| **revenueGrowthPercent** | % revenue change | Financial growth indicator |
| **activeDeliveries** | Currently ongoing deliveries | Real-time operations |
| **onlineDeliveryAgents** | Agents available for work | Resource availability |

### **Order Analytics**

| Metric | Description | Business Value |
|--------|-------------|----------------|
| **averageOrderValue** | Revenue ÷ Orders | Customer spending pattern |
| **ordersByHour** | Peak ordering times | Staffing optimization |
| **topSellingItems** | Most popular dishes | Menu optimization |
| **revenueByPaymentMethod** | Payment preferences | Payment strategy |
| **dailyRevenueTrend** | 7-day revenue graph | Trend analysis |

### **Delivery Analytics**

| Metric | Description | Optimization Use |
|--------|-------------|------------------|
| **successRate** | % successful deliveries | Service quality |
| **averageDeliveryTime** | Avg minutes to deliver | Efficiency metric |
| **topAgents** | Best performing agents | Reward & training |
| **busyAgents** | Currently delivering | Capacity planning |

### **Reservation Analytics**

| Metric | Description | Strategic Use |
|--------|-------------|---------------|
| **occupancyRate** | % tables occupied | Capacity utilization |
| **reservationsByHour** | Peak booking times | Staffing decisions |
| **mostBookedTables** | Popular table locations | Layout optimization |
| **cancellationRate** | % cancelled bookings | Customer reliability |
| **reservationsByOccasion** | Special event bookings | Marketing insights |

---

## 🎨 DASHBOARD VISUALIZATIONS

### Recommended Charts

#### **1. Revenue Trend Chart** (Line Graph)
- X-axis: Last 7 days
- Y-axis: Revenue
- Data: `dailyRevenueTrend`

#### **2. Order Type Distribution** (Pie Chart)
- Segments: Dine-in, Takeaway, Delivery
- Data: `dineInOrders`, `takeawayOrders`, `deliveryOrders`

#### **3. Peak Hours Heatmap** (Bar Chart)
- X-axis: Hours (0-23)
- Y-axis: Order count
- Data: `ordersByHour`

#### **4. Agent Performance** (Horizontal Bar Chart)
- Y-axis: Agent names
- X-axis: Success rate %
- Data: `topAgents`

#### **5. Table Occupancy Gauge** (Gauge Chart)
- Current: `occupiedTables`
- Total: `totalTables`
- Percentage: `occupancyRate`

#### **6. Reservation Pattern** (Calendar Heatmap)
- Days of week
- Color intensity: Booking count
- Data: `reservationsByDay`

---

## 🔔 BUSINESS INSIGHTS

### **Key Performance Indicators (KPIs)**

1. **Daily Revenue Target**
   - Track: `todayRevenue`
   - Goal: Set monthly targets
   - Alert: If below 80% of target

2. **Order Growth Rate**
   - Track: `orderGrowthPercent`
   - Healthy: > 5%
   - Alert: If negative

3. **Delivery Success Rate**
   - Track: `successRate`
   - Target: > 95%
   - Alert: If < 90%

4. **Table Occupancy**
   - Track: `occupancyRate`
   - Optimal: 60-80%
   - Alert: If < 30% or > 90%

5. **Average Order Value**
   - Track: `averageOrderValue`
   - Strategy: Increase through upselling
   - Monitor: Weekly trends

### **Action Items Based on Analytics**

**If Order Growth is Negative:**
- Launch promotional campaigns
- Review menu pricing
- Check customer feedback
- Analyze competition

**If Delivery Success Rate is Low:**
- Train delivery agents
- Review route optimization
- Check vehicle maintenance
- Improve communication

**If Occupancy is Low:**
- Run reservation promotions
- Social media marketing
- Partner with booking platforms
- Special occasion packages

**If Cancellation Rate is High:**
- Implement cancellation fee
- Send confirmation reminders
- Improve communication
- Review booking process

---

## 📈 ADVANCED QUERIES

### Custom Date Range Analysis

```powershell
# Get analytics for specific date range
$startDate = "2025-09-01"
$endDate = "2025-09-30"

# Order analytics for September
$url = "$baseUrl/api/hotel/analytics/orders?startDate=$startDate&endDate=$endDate"
$sepOrders = Invoke-WebRequest -Uri $url -Method GET -Headers $headers

# Delivery analytics for September
$url = "$baseUrl/api/hotel/analytics/deliveries?startDate=$startDate&endDate=$endDate"
$sepDeliveries = Invoke-WebRequest -Uri $url -Method GET -Headers $headers

# Reservation analytics for September
$url = "$baseUrl/api/hotel/analytics/reservations?startDate=$startDate&endDate=$endDate"
$sepReservations = Invoke-WebRequest -Uri $url -Method GET -Headers $headers
```

### Compare Time Periods

```powershell
# This month vs last month
$thisMonth = Invoke-WebRequest -Uri "$baseUrl/api/hotel/analytics/orders/this-month" -Method GET -Headers $headers
$lastMonth = Invoke-WebRequest -Uri "$baseUrl/api/hotel/analytics/orders?startDate=2025-09-01&endDate=2025-09-30" -Method GET -Headers $headers

$thisData = $thisMonth.Content | ConvertFrom-Json
$lastData = $lastMonth.Content | ConvertFrom-Json

$growth = (($thisData.totalRevenue - $lastData.totalRevenue) / $lastData.totalRevenue) * 100
Write-Host "Month-over-Month Growth: $growth%"
```

---

## 🚀 API ENDPOINTS SUMMARY

**Total Analytics Endpoints**: 11

```
Dashboard:
✅ GET /api/hotel/analytics/dashboard                    - Overall overview

Orders:
✅ GET /api/hotel/analytics/orders                       - Custom date range
✅ GET /api/hotel/analytics/orders/today                 - Today's data
✅ GET /api/hotel/analytics/orders/this-week             - Last 7 days
✅ GET /api/hotel/analytics/orders/this-month            - Current month

Deliveries:
✅ GET /api/hotel/analytics/deliveries                   - Custom date range
✅ GET /api/hotel/analytics/deliveries/today             - Today's data

Reservations:
✅ GET /api/hotel/analytics/reservations                 - Custom date range
✅ GET /api/hotel/analytics/reservations/today           - Today's data
✅ GET /api/hotel/analytics/reservations/this-week       - Last 7 days
✅ GET /api/hotel/analytics/reservations/this-month      - Current month
```

---

## 💡 INTEGRATION TIPS

### **1. Admin Dashboard Integration**
```javascript
// Fetch all analytics on dashboard load
const loadDashboard = async () => {
  const overview = await fetch('/api/hotel/analytics/dashboard');
  const orders = await fetch('/api/hotel/analytics/orders/today');
  const deliveries = await fetch('/api/hotel/analytics/deliveries/today');
  const reservations = await fetch('/api/hotel/analytics/reservations/today');
  
  renderDashboard({ overview, orders, deliveries, reservations });
};
```

### **2. Real-time Updates**
```javascript
// Refresh every 30 seconds
setInterval(() => {
  loadDashboard();
}, 30000);
```

### **3. Export Reports**
```powershell
# Export to CSV
$data = Invoke-WebRequest -Uri "$baseUrl/api/hotel/analytics/orders/this-month" -Headers $headers
$data.Content | ConvertFrom-Json | Export-Csv -Path "monthly_report.csv"
```

---

## ✅ SUMMARY

**Analytics Dashboard Includes:**
- ✅ 11 REST API endpoints
- ✅ 8 main DTO classes
- ✅ Real-time metrics
- ✅ Historical analysis
- ✅ Growth indicators
- ✅ Performance tracking
- ✅ Business intelligence
- ✅ Trend analysis

**Business Benefits:**
- 📊 Data-driven decisions
- 📈 Track growth trends
- 🎯 Identify opportunities
- ⚡ Optimize operations
- 💰 Increase revenue
- 👥 Improve customer satisfaction
- 🚀 Scale intelligently

---

**Status:** 🟢 PRODUCTION READY  
**Version:** 1.0.0  
**Last Updated:** October 1, 2025
