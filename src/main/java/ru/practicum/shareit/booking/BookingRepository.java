package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    boolean existsByItemIdAndBookerIdAndEndBefore(Long itemId, Long userId, LocalDateTime time);

    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(Long itemId, LocalDateTime now, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime now, BookingStatus status);

    @Query("""
            select b from Booking b
            where b.item.id in :itemIds
            and b.start < :now
            and b.status = :status
            and b.end = (
                select max(b2.end) from Booking b2
                where b2.item.id = b.item.id
                and b2.start < :now
                and b2.status = :status
            )
            """)
    List<Booking> findLastBookings(List<Long> itemIds, LocalDateTime now, BookingStatus status);


    @Query("""
            select b from Booking b
            where b.item.id in :itemIds
            and b.start > :now
            and b.status = :status
            and b.start = (
                select min(b2.start) from Booking b2
                where b2.item.id = b.item.id
                and b2.start > :now
                and b2.status = :status
            )
            """)
    List<Booking> findNextBookings(List<Long> itemIds, LocalDateTime now, BookingStatus status);

}
