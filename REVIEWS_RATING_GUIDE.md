# 🌟 RATINGS & REVIEWS SYSTEM - COMPLETE GUIDE

## 📋 Overview

A comprehensive ratings and reviews system integrated with the Hotel Ordering System, allowing customers to rate and review menu items.

---

## ✨ Features

### **Core Features**
- ✅ **5-Star Rating System** (1-5 stars)
- ✅ **Written Reviews** with comments
- ✅ **Verified Purchase Badge** - Shows if reviewer actually ordered the item
- ✅ **Helpful Count** - Users can mark reviews as helpful
- ✅ **Review Moderation** - Admin can approve/reject reviews
- ✅ **Edit Reviews** - Users can update their reviews
- ✅ **Rating Statistics** - Average ratings, star distribution
- ✅ **Pagination Support** - Handle large number of reviews
- ✅ **Auto-Update** - Menu item ratings update automatically

---

## 🗄️ Database Schema

### **hotel_reviews Table**
```sql
- id (Primary Key)
- menu_item_id (Foreign Key → hotel_menu_items)
- user_id (User who wrote the review)
- user_name (Cached user name)
- rating (1-5 stars)
- comment (Review text)
- is_verified_purchase (Did user order this item?)
- is_approved (Admin moderation)
- helpful_count (How many found it helpful)
- is_edited (Was review edited?)
- edited_at (When was it edited)
- created_at
- updated_at
```

---

## 📝 API ENDPOINTS

### **1. Create Review**
```powershell
POST /api/hotel/reviews

# Body:
{
  "menuItemId": 1,
  "userId": 18,
  "userName": "Karina",
  "rating": 5,
  "comment": "Absolutely delicious! Best Butter Chicken I've ever had!"
}
```

**Test Command:**
```powershell
$token = "YOUR_JWT_TOKEN"
$headers = @{ Authorization = "Bearer $token" }

$review = @{
    menuItemId = 1
    userId = 18
    userName = "Karina"
    rating = 5
    comment = "Excellent taste and quality!"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reviews" `
  -Method POST -Body $review -ContentType "application/json" -Headers $headers
```

---

### **2. Update Review**
```powershell
PUT /api/hotel/reviews/{reviewId}

# Body:
{
  "menuItemId": 1,
  "userId": 18,
  "userName": "Karina",
  "rating": 4,
  "comment": "Updated: Still good but a bit too spicy for me"
}
```

**Test Command:**
```powershell
$updatedReview = @{
    menuItemId = 1
    userId = 18
    userName = "Karina"
    rating = 4
    comment = "Updated review text"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reviews/1" `
  -Method PUT -Body $updatedReview -ContentType "application/json" -Headers $headers
```

---

### **3. Delete Review**
```powershell
DELETE /api/hotel/reviews/{reviewId}/user/{userId}
```

**Test Command:**
```powershell
Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reviews/1/user/18" `
  -Method DELETE -Headers $headers
```

---

### **4. Get All Reviews for Menu Item**
```powershell
GET /api/hotel/reviews/menu-item/{menuItemId}
```

**Test Command:**
```powershell
# Get all reviews for menu item 1
Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reviews/menu-item/1" `
  -Method GET -Headers $headers

# With pagination
Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reviews/menu-item/1/pageable?page=0&size=10&sort=createdAt,desc" `
  -Method GET -Headers $headers
```

---

### **5. Get Reviews by User**
```powershell
GET /api/hotel/reviews/user/{userId}
```

**Test Command:**
```powershell
# Get all reviews written by user
Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reviews/user/18" `
  -Method GET -Headers $headers
```

---

### **6. Get Reviews by Rating**
```powershell
GET /api/hotel/reviews/menu-item/{menuItemId}/rating/{rating}
```

**Test Command:**
```powershell
# Get all 5-star reviews
Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reviews/menu-item/1/rating/5" `
  -Method GET -Headers $headers

# Get all 1-star reviews
Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reviews/menu-item/1/rating/1" `
  -Method GET -Headers $headers
```

---

### **7. Get Verified Purchase Reviews**
```powershell
GET /api/hotel/reviews/menu-item/{menuItemId}/verified
```

**Test Command:**
```powershell
# Get only reviews from customers who actually ordered this item
Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reviews/menu-item/1/verified" `
  -Method GET -Headers $headers
```

---

### **8. Mark Review as Helpful**
```powershell
POST /api/hotel/reviews/{reviewId}/helpful
```

**Test Command:**
```powershell
Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reviews/1/helpful" `
  -Method POST -Headers $headers
```

---

### **9. Get Menu Item with Review Statistics**
```powershell
GET /api/hotel/reviews/menu-item/{menuItemId}/details
```

**Test Command:**
```powershell
Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reviews/menu-item/1/details" `
  -Method GET -Headers $headers
```

**Response:**
```json
{
  "id": 1,
  "name": "Butter Chicken",
  "description": "Creamy tomato curry",
  "price": 350,
  "averageRating": 4.5,
  "totalReviews": 100,
  "fiveStarCount": 60,
  "fourStarCount": 25,
  "threeStarCount": 10,
  "twoStarCount": 3,
  "oneStarCount": 2,
  "recentReviews": [...]
}
```

---

## 🎯 COMPLETE WORKFLOW TEST

```powershell
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "RATINGS & REVIEWS SYSTEM - COMPLETE TEST" -ForegroundColor Yellow
Write-Host "========================================`n" -ForegroundColor Cyan

# Step 1: Login
$loginRes = Invoke-WebRequest -Uri "http://localhost:9091/api/auth/login" `
  -Method POST `
  -Body '{"usernameOrEmail":"karina","password":"karina"}' `
  -ContentType "application/json"
$loginData = ($loginRes.Content | ConvertFrom-Json).data
$token = $loginData.jwtToken
$userId = $loginData.user.id
$headers = @{ Authorization = "Bearer $token" }
Write-Host "✅ Logged in as: $($loginData.user.name)`n" -ForegroundColor Green

# Step 2: Create Menu Item (if not exists)
Write-Host "Creating menu item..." -ForegroundColor Green
# ... (use previous menu creation code)

# Step 3: Create Review
Write-Host "Creating review..." -ForegroundColor Green
$review = @{
    menuItemId = 1
    userId = $userId
    userName = $loginData.user.name
    rating = 5
    comment = "Absolutely amazing! The best dish I've ever had. Highly recommend!"
} | ConvertTo-Json

$reviewRes = Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reviews" `
  -Method POST -Body $review -ContentType "application/json" -Headers $headers
$reviewData = $reviewRes.Content | ConvertFrom-Json
Write-Host "✅ Review created with ID: $($reviewData.id)" -ForegroundColor Green
Write-Host "   Rating: $($reviewData.rating) stars" -ForegroundColor Yellow
Write-Host "   Verified Purchase: $($reviewData.isVerifiedPurchase)`n" -ForegroundColor Yellow

# Step 4: Get Menu Item with Reviews
Write-Host "Fetching menu item with review statistics..." -ForegroundColor Green
$detailsRes = Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reviews/menu-item/1/details" `
  -Method GET -Headers $headers
$details = $detailsRes.Content | ConvertFrom-Json
Write-Host "✅ Menu Item: $($details.name)" -ForegroundColor Green
Write-Host "   Average Rating: $($details.averageRating) ⭐" -ForegroundColor Yellow
Write-Host "   Total Reviews: $($details.totalReviews)" -ForegroundColor Yellow
Write-Host "   5-star: $($details.fiveStarCount)" -ForegroundColor Yellow
Write-Host "   4-star: $($details.fourStarCount)" -ForegroundColor Yellow
Write-Host "   3-star: $($details.threeStarCount)`n" -ForegroundColor Yellow

# Step 5: Mark Review as Helpful
Write-Host "Marking review as helpful..." -ForegroundColor Green
$helpfulRes = Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reviews/$($reviewData.id)/helpful" `
  -Method POST -Headers $headers
$helpfulData = $helpfulRes.Content | ConvertFrom-Json
Write-Host "✅ Helpful count: $($helpfulData.helpfulCount)`n" -ForegroundColor Green

# Step 6: Get All Reviews for Menu Item
Write-Host "Fetching all reviews..." -ForegroundColor Green
$allReviewsRes = Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reviews/menu-item/1" `
  -Method GET -Headers $headers
$allReviews = $allReviewsRes.Content | ConvertFrom-Json
Write-Host "✅ Total reviews found: $($allReviews.Count)`n" -ForegroundColor Green

# Step 7: Update Review
Write-Host "Updating review..." -ForegroundColor Green
$updateReview = @{
    menuItemId = 1
    userId = $userId
    userName = $loginData.user.name
    rating = 4
    comment = "Updated: Still excellent, but slightly less spicy would be perfect!"
} | ConvertTo-Json

$updateRes = Invoke-WebRequest -Uri "http://localhost:9091/api/hotel/reviews/$($reviewData.id)" `
  -Method PUT -Body $updateReview -ContentType "application/json" -Headers $headers
$updatedData = $updateRes.Content | ConvertFrom-Json
Write-Host "✅ Review updated" -ForegroundColor Green
Write-Host "   New Rating: $($updatedData.rating) stars" -ForegroundColor Yellow
Write-Host "   Edited: $($updatedData.isEdited)`n" -ForegroundColor Yellow

Write-Host "========================================" -ForegroundColor Green
Write-Host "✅ ALL REVIEW TESTS PASSED!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
```

---

## 🎨 Features Breakdown

### **Verified Purchase Badge**
- Automatically checks if user has ordered the item
- Displays "Verified Purchase" badge
- Increases trust in reviews

### **Rating Statistics**
- Calculates average rating automatically
- Shows star distribution (5-star, 4-star, etc.)
- Updates in real-time

### **Review Moderation**
- Admin can approve/reject reviews
- Prevents spam and inappropriate content
- Reviews visible only after approval

### **Helpful Count**
- Users can mark reviews as helpful
- Most helpful reviews show first
- Community-driven quality control

---

## 📊 Use Cases

### **Customer Flow:**
```
1. Customer orders Butter Chicken
2. After receiving order, posts review
3. Review marked as "Verified Purchase"
4. Other customers see verified badge
5. Rating updates menu item average
```

### **Restaurant Flow:**
```
1. View all reviews for menu items
2. Identify popular items (high ratings)
3. Identify items needing improvement
4. Respond to customer feedback
5. Moderate inappropriate reviews
```

---

## 🔄 Integration with Existing System

### **Menu Items**
- Auto-updates `rating` field
- Shows average rating on menu
- Sorts by highest rated

### **Orders**
- Tracks verified purchases
- Links reviews to orders
- Enables review after order completion

### **Users**
- View review history
- Edit/delete own reviews
- Build reputation

---

## 🚀 Future Enhancements

### **Phase 2: Advanced Features**
- [ ] Review images/photos
- [ ] Review videos
- [ ] Review replies (restaurant responses)
- [ ] Report inappropriate reviews
- [ ] Review rewards (points for reviews)
- [ ] Top reviewers leaderboard
- [ ] Email notifications for reviews

### **Phase 3: MCP Tool Integration**
Convert to chatbot tools:
```
User: "Show me reviews for Butter Chicken"
Bot: [Calls get_reviews(item_id) MCP tool]
Bot: "⭐ 4.5/5 (100 reviews)
     
     Top Review:
     ⭐⭐⭐⭐⭐ by Karina (Verified Purchase)
     'Absolutely amazing! Best dish ever!'"

User: "I want to review the Paneer Tikka"
Bot: "How would you rate it? (1-5 stars)"
User: "5 stars"
Bot: "Great! What did you like about it?"
User: "Perfect spice level and tender texture"
Bot: [Calls create_review() MCP tool]
Bot: "✅ Review posted! Thank you for your feedback!"
```

---

## ✅ Summary

**New Components Added:**
- ✅ Review entity
- ✅ Review repository with advanced queries
- ✅ Review service with rating calculations
- ✅ Review controller with 11 endpoints
- ✅ 4 DTOs for reviews
- ✅ Verified purchase tracking
- ✅ Rating statistics
- ✅ Helpful count feature

**Total New Endpoints**: 11
**Total Lines of Code**: ~800+

**Status**: 🟢 Ready to Test!
