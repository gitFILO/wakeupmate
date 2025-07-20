package com.example.wakeupmate.place.domain;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.example.wakeupmate.global.domain.BaseEntity;
import com.example.wakeupmate.user.domain.User;
import jakarta.persistence.*;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE place SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status = 'USABLE'")
public class Place extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, precision = 16, scale = 13)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 16, scale = 13)
    private BigDecimal longitude;

    @Column(nullable = false)
    private Boolean isHome = false;

    @Column(length = 500)
    private String address;

    @Column(length = 1000)
    private String description;

    public Place(
            final String name,
            final User user,
            final BigDecimal latitude,
            final BigDecimal longitude,
            final Boolean isHome,
            final String address,
            final String description
    ) {
        this.name = name;
        this.user = user;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isHome = isHome != null ? isHome : false;
        this.address = address;
        this.description = description;
    }

    public void update(
            final String name,
            final BigDecimal latitude,
            final BigDecimal longitude,
            final Boolean isHome,
            final String address,
            final String description
    ) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isHome = isHome != null ? isHome : false;
        this.address = address;
        this.description = description;
    }
}
