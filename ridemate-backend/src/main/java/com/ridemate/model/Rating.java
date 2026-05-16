package com.ridemate.model;

import com.ridemate.model.enums.RatingParameter;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "rater_id", nullable = false)
    private User rater;

    @ManyToOne
    @JoinColumn(name = "ratee_id", nullable = false)
    private User ratee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RatingParameter parameter;

    @Column(nullable = false)
    private Integer stars; // 1-5

    @Column(length = 1000)
    private String review;  // Text review/comment

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
