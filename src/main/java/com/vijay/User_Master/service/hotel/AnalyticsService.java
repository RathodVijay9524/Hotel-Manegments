package com.vijay.User_Master.service.hotel;

import com.vijay.User_Master.dto.hotel.analytics.*;
import com.vijay.User_Master.entity.hotel.*;
import com.vijay.User_Master.repository.hotel.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {
    
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final MenuItemRepository menuItemRepository;
    private final DeliveryAgentRepository deliveryAgentRepository;
    private final DeliveryTrackingRepository deliveryTrackingRepository;
    private final TableReservationRepository reservationRepository;
    private final RestaurantTableRepository tableRepository;
    private final OrderItemRepository orderItemRepository;
    
    // ==================== DASHBOARD OVERVIEW ====================
    
    public DashboardOverviewDTO getDashboardOverview() {
        log.info("Generating dashboard overview");
        
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        LocalDateTime yesterdayStart = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime yesterdayEnd = LocalDate.now().minusDays(1).atTime(LocalTime.MAX);
        
        // Today's metrics
        Long todayOrders = orderRepository.countByCreatedAtBetween(todayStart, todayEnd);
        BigDecimal todayRevenue = calculateRevenueBetween(todayStart, todayEnd);
        Long todayReservations = (long) reservationRepository.findReservationsBetweenDates(todayStart, todayEnd).size();
        Long activeDeliveries = (long) deliveryTrackingRepository.findAllActiveDeliveries().size();
        
        // Yesterday's metrics for growth calculation
        Long yesterdayOrders = orderRepository.countByCreatedAtBetween(yesterdayStart, yesterdayEnd);
        BigDecimal yesterdayRevenue = calculateRevenueBetween(yesterdayStart, yesterdayEnd);
        Long yesterdayReservations = (long) reservationRepository.findReservationsBetweenDates(yesterdayStart, yesterdayEnd).size();
        
        // Overall metrics
        Long totalOrders = orderRepository.count();
        BigDecimal totalRevenue = calculateTotalRevenue();
        Long totalReservations = reservationRepository.count();
        
        // Growth percentages
        Double orderGrowth = calculateGrowthPercent(yesterdayOrders, todayOrders);
        Double revenueGrowth = calculateGrowthPercent(yesterdayRevenue, todayRevenue);
        Double reservationGrowth = calculateGrowthPercent(yesterdayReservations, todayReservations);
        
        // Current status
        Long onlineAgents = (long) deliveryAgentRepository.findByIsOnlineTrue().size();
        List<RestaurantTable> allTables = tableRepository.findAll();
        Long occupiedTables = allTables.stream().filter(t -> !t.getIsAvailable()).count();
        Long availableTables = (long) tableRepository.findByIsAvailableTrue().size();
        
        return DashboardOverviewDTO.builder()
                .todayOrders(todayOrders)
                .todayRevenue(todayRevenue)
                .todayReservations(todayReservations)
                .activeDeliveries(activeDeliveries)
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .totalReservations(totalReservations)
                .orderGrowthPercent(orderGrowth)
                .revenueGrowthPercent(revenueGrowth)
                .reservationGrowthPercent(reservationGrowth)
                .onlineDeliveryAgents(onlineAgents)
                .occupiedTables(occupiedTables)
                .availableTables(availableTables)
                .build();
    }
    
    // ==================== ORDER ANALYTICS ====================
    
    public OrderAnalyticsDTO getOrderAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating order analytics from {} to {}", startDate, endDate);
        
        List<Order> orders = orderRepository.findByCreatedAtBetween(startDate, endDate);
        
        // Summary metrics
        Long totalOrders = (long) orders.size();
        BigDecimal totalRevenue = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgOrderValue = totalOrders > 0 ? 
                totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        Long pendingOrders = orders.stream().filter(o -> o.getStatus() == Order.OrderStatus.PENDING).count();
        Long completedOrders = orders.stream().filter(o -> o.getStatus() == Order.OrderStatus.COMPLETED).count();
        
        // Order type distribution
        Long dineIn = orders.stream().filter(o -> o.getOrderType() == Order.OrderType.DINE_IN).count();
        Long takeaway = orders.stream().filter(o -> o.getOrderType() == Order.OrderType.TAKEAWAY).count();
        Long delivery = orders.stream().filter(o -> o.getOrderType() == Order.OrderType.DELIVERY).count();
        
        // Revenue by payment method (from payments)
        Map<String, BigDecimal> revenueByPayment = paymentRepository.findAll().stream()
                .filter(p -> p.getOrder().getCreatedAt().isAfter(startDate) && p.getOrder().getCreatedAt().isBefore(endDate))
                .collect(Collectors.groupingBy(
                        p -> p.getPaymentMethod().toString(),
                        Collectors.reducing(BigDecimal.ZERO, Payment::getAmount, BigDecimal::add)
                ));
        
        // Orders by hour
        Map<Integer, Long> ordersByHour = orders.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getCreatedAt().getHour(),
                        Collectors.counting()
                ));
        
        // Top selling items
        List<PopularItemDTO> topItems = getTopSellingItems(startDate, endDate, 10);
        
        // Daily revenue trend (last 7 days)
        List<DailyRevenueDTO> dailyTrend = getDailyRevenueTrend(7);
        
        return OrderAnalyticsDTO.builder()
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .averageOrderValue(avgOrderValue)
                .pendingOrders(pendingOrders)
                .completedOrders(completedOrders)
                .dineInOrders(dineIn)
                .takeawayOrders(takeaway)
                .deliveryOrders(delivery)
                .revenueByPaymentMethod(revenueByPayment)
                .ordersByHour(ordersByHour)
                .topSellingItems(topItems)
                .dailyRevenueTrend(dailyTrend)
                .build();
    }
    
    // ==================== DELIVERY ANALYTICS ====================
    
    public DeliveryAnalyticsDTO getDeliveryAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating delivery analytics from {} to {}", startDate, endDate);
        
        List<DeliveryTracking> deliveries = deliveryTrackingRepository.findAll().stream()
                .filter(d -> d.getCreatedAt().isAfter(startDate) && d.getCreatedAt().isBefore(endDate))
                .collect(Collectors.toList());
        
        Long totalDeliveries = (long) deliveries.size();
        Long successful = deliveries.stream().filter(d -> d.getStatus() == DeliveryTracking.DeliveryStatus.DELIVERED).count();
        Long failed = deliveries.stream().filter(d -> d.getStatus() == DeliveryTracking.DeliveryStatus.FAILED).count();
        Long active = (long) deliveryTrackingRepository.findAllActiveDeliveries().size();
        
        // Average delivery time (for completed deliveries)
        Double avgTime = deliveries.stream()
                .filter(d -> d.getPickedUpAt() != null && d.getDeliveredAt() != null)
                .mapToLong(d -> java.time.Duration.between(d.getPickedUpAt(), d.getDeliveredAt()).toMinutes())
                .average()
                .orElse(0.0);
        
        Double successRate = totalDeliveries > 0 ? (successful * 100.0 / totalDeliveries) : 0.0;
        
        Double avgRating = deliveries.stream()
                .filter(d -> d.getDeliveryRating() != null)
                .mapToDouble(DeliveryTracking::getDeliveryRating)
                .average()
                .orElse(0.0);
        
        // Agent metrics
        Long totalAgents = deliveryAgentRepository.count();
        Long onlineAgents = (long) deliveryAgentRepository.findByIsOnlineTrue().size();
        Long availableAgents = (long) deliveryAgentRepository.findByIsAvailableTrueAndIsOnlineTrue().size();
        Long busyAgents = onlineAgents - availableAgents;
        
        // Top agents
        List<AgentPerformanceDTO> topAgents = getTopPerformingAgents(5);
        
        // Status distribution
        Long pending = deliveries.stream().filter(d -> d.getStatus() == DeliveryTracking.DeliveryStatus.PENDING).count();
        Long assigned = deliveries.stream().filter(d -> d.getStatus() == DeliveryTracking.DeliveryStatus.ASSIGNED).count();
        Long pickedUp = deliveries.stream().filter(d -> d.getStatus() == DeliveryTracking.DeliveryStatus.PICKED_UP).count();
        Long inTransit = deliveries.stream().filter(d -> d.getStatus() == DeliveryTracking.DeliveryStatus.IN_TRANSIT).count();
        
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        Long deliveredToday = deliveries.stream()
                .filter(d -> d.getStatus() == DeliveryTracking.DeliveryStatus.DELIVERED)
                .filter(d -> d.getDeliveredAt() != null && d.getDeliveredAt().isAfter(todayStart))
                .count();
        
        return DeliveryAnalyticsDTO.builder()
                .totalDeliveries(totalDeliveries)
                .successfulDeliveries(successful)
                .failedDeliveries(failed)
                .activeDeliveries(active)
                .averageDeliveryTimeMinutes(avgTime)
                .successRate(successRate)
                .averageRating(avgRating)
                .totalAgents(totalAgents)
                .onlineAgents(onlineAgents)
                .availableAgents(availableAgents)
                .busyAgents(busyAgents)
                .topAgents(topAgents)
                .pendingDeliveries(pending)
                .assignedDeliveries(assigned)
                .pickedUpDeliveries(pickedUp)
                .inTransitDeliveries(inTransit)
                .deliveredToday(deliveredToday)
                .build();
    }
    
    // ==================== RESERVATION ANALYTICS ====================
    
    public ReservationAnalyticsDTO getReservationAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating reservation analytics from {} to {}", startDate, endDate);
        
        List<TableReservation> reservations = reservationRepository.findReservationsBetweenDates(startDate, endDate);
        
        Long totalReservations = (long) reservations.size();
        Long upcoming = (long) reservationRepository.findUpcomingReservations(LocalDateTime.now()).size();
        
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        Long today = (long) reservationRepository.findReservationsBetweenDates(todayStart, todayEnd).size();
        
        Long completed = reservations.stream().filter(r -> r.getStatus() == TableReservation.ReservationStatus.COMPLETED).count();
        Long cancelled = reservations.stream().filter(r -> r.getStatus() == TableReservation.ReservationStatus.CANCELLED).count();
        Long noShow = reservations.stream().filter(r -> r.getStatus() == TableReservation.ReservationStatus.NO_SHOW).count();
        
        // Table metrics
        Long totalTables = tableRepository.count();
        List<RestaurantTable> tables = tableRepository.findAll();
        Long occupied = tables.stream().filter(t -> !t.getIsAvailable()).count();
        Long available = (long) tableRepository.findByIsAvailableTrue().size();
        Double occupancyRate = totalTables > 0 ? (occupied * 100.0 / totalTables) : 0.0;
        
        // Reservations by hour
        Map<Integer, Long> byHour = reservations.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getReservationDateTime().getHour(),
                        Collectors.counting()
                ));
        
        // Reservations by day of week
        Map<String, Long> byDay = reservations.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getReservationDateTime().getDayOfWeek().toString(),
                        Collectors.counting()
                ));
        
        // Popular tables
        List<PopularTableDTO> popularTables = getMostBookedTables(5);
        
        // By occasion
        Map<String, Long> byOccasion = reservations.stream()
                .filter(r -> r.getOccasion() != null && !r.getOccasion().isEmpty())
                .collect(Collectors.groupingBy(
                        TableReservation::getOccasion,
                        Collectors.counting()
                ));
        
        // Averages
        Double avgGuests = reservations.stream()
                .mapToInt(TableReservation::getNumberOfGuests)
                .average()
                .orElse(0.0);
        
        Double avgDuration = reservations.stream()
                .mapToInt(TableReservation::getDurationMinutes)
                .average()
                .orElse(0.0);
        
        Double cancellationRate = totalReservations > 0 ? (cancelled * 100.0 / totalReservations) : 0.0;
        
        return ReservationAnalyticsDTO.builder()
                .totalReservations(totalReservations)
                .upcomingReservations(upcoming)
                .todayReservations(today)
                .completedReservations(completed)
                .cancelledReservations(cancelled)
                .noShowReservations(noShow)
                .totalTables(totalTables)
                .occupiedTables(occupied)
                .availableTables(available)
                .occupancyRate(occupancyRate)
                .reservationsByHour(byHour)
                .reservationsByDay(byDay)
                .mostBookedTables(popularTables)
                .reservationsByOccasion(byOccasion)
                .averageGuestsPerReservation(avgGuests)
                .averageDurationMinutes(avgDuration)
                .cancellationRate(cancellationRate)
                .build();
    }
    
    // ==================== HELPER METHODS ====================
    
    private BigDecimal calculateRevenueBetween(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByCreatedAtBetween(start, end).stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private BigDecimal calculateTotalRevenue() {
        return orderRepository.findAll().stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private Double calculateGrowthPercent(Long previous, Long current) {
        if (previous == 0) return current > 0 ? 100.0 : 0.0;
        return ((current - previous) * 100.0) / previous;
    }
    
    private Double calculateGrowthPercent(BigDecimal previous, BigDecimal current) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        return current.subtract(previous)
                .multiply(BigDecimal.valueOf(100))
                .divide(previous, 2, RoundingMode.HALF_UP)
                .doubleValue();
    }
    
    private List<PopularItemDTO> getTopSellingItems(LocalDateTime start, LocalDateTime end, int limit) {
        List<OrderItem> orderItems = orderItemRepository.findAll().stream()
                .filter(oi -> oi.getOrder().getCreatedAt().isAfter(start) && 
                             oi.getOrder().getCreatedAt().isBefore(end))
                .collect(Collectors.toList());
        
        Map<Long, List<OrderItem>> itemGroups = orderItems.stream()
                .collect(Collectors.groupingBy(oi -> oi.getMenuItem().getId()));
        
        List<PopularItemDTO> popularItems = itemGroups.entrySet().stream()
                .map(entry -> {
                    MenuItem item = entry.getValue().get(0).getMenuItem();
                    List<OrderItem> items = entry.getValue();
                    
                    return PopularItemDTO.builder()
                            .itemId(item.getId())
                            .itemName(item.getName())
                            .categoryName(item.getCategory().getName())
                            .orderCount((long) items.size())
                            .totalQuantitySold(items.stream().mapToLong(OrderItem::getQuantity).sum())
                            .revenue(items.stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add))
                            .averageRating(item.getRating() != null ? item.getRating().doubleValue() : 0.0)
                            .build();
                })
                .collect(Collectors.toList());
        
        return popularItems.stream()
                .sorted(Comparator.comparing(PopularItemDTO::getTotalQuantitySold).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    private List<DailyRevenueDTO> getDailyRevenueTrend(int days) {
        List<DailyRevenueDTO> trend = new ArrayList<>();
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);
            
            List<Order> dayOrders = orderRepository.findByCreatedAtBetween(start, end);
            BigDecimal revenue = dayOrders.stream()
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            trend.add(DailyRevenueDTO.builder()
                    .date(date)
                    .orderCount((long) dayOrders.size())
                    .revenue(revenue)
                    .build());
        }
        
        return trend;
    }
    
    private List<AgentPerformanceDTO> getTopPerformingAgents(int limit) {
        return deliveryAgentRepository.findTopRatedAgents().stream()
                .limit(limit)
                .map(agent -> {
                    List<DeliveryTracking> agentDeliveries = deliveryTrackingRepository.findByAgentId(agent.getId());
                    Long successful = agentDeliveries.stream()
                            .filter(d -> d.getStatus() == DeliveryTracking.DeliveryStatus.DELIVERED)
                            .count();
                    Long failed = agentDeliveries.stream()
                            .filter(d -> d.getStatus() == DeliveryTracking.DeliveryStatus.FAILED)
                            .count();
                    
                    Double successRate = agentDeliveries.size() > 0 ? 
                            (successful * 100.0 / agentDeliveries.size()) : 0.0;
                    
                    Double avgTime = agentDeliveries.stream()
                            .filter(d -> d.getPickedUpAt() != null && d.getDeliveredAt() != null)
                            .mapToLong(d -> java.time.Duration.between(d.getPickedUpAt(), d.getDeliveredAt()).toMinutes())
                            .average()
                            .orElse(0.0);
                    
                    return AgentPerformanceDTO.builder()
                            .agentId(agent.getId())
                            .agentName(agent.getName())
                            .phone(agent.getPhone())
                            .vehicleType(agent.getVehicleType())
                            .totalDeliveries(agent.getTotalDeliveries())
                            .successfulDeliveries(successful.intValue())
                            .failedDeliveries(failed.intValue())
                            .rating(agent.getRating())
                            .successRate(successRate)
                            .averageDeliveryTimeMinutes(avgTime)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    private List<PopularTableDTO> getMostBookedTables(int limit) {
        List<TableReservation> allReservations = reservationRepository.findAll();
        
        Map<Long, List<TableReservation>> tableGroups = allReservations.stream()
                .filter(r -> r.getTable() != null)
                .collect(Collectors.groupingBy(r -> r.getTable().getId()));
        
        return tableGroups.entrySet().stream()
                .map(entry -> {
                    RestaurantTable table = entry.getValue().get(0).getTable();
                    List<TableReservation> bookings = entry.getValue();
                    
                    Double avgGuests = bookings.stream()
                            .mapToInt(TableReservation::getNumberOfGuests)
                            .average()
                            .orElse(0.0);
                    
                    return PopularTableDTO.builder()
                            .tableId(table.getId())
                            .tableNumber(table.getTableNumber())
                            .tableName(table.getTableName())
                            .location(table.getLocation())
                            .capacity(table.getCapacity())
                            .bookingCount((long) bookings.size())
                            .averageGuestsPerBooking(avgGuests)
                            .build();
                })
                .sorted(Comparator.comparing(PopularTableDTO::getBookingCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
