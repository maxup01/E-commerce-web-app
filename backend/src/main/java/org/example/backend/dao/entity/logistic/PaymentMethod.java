package org.example.backend.dao.entity.logistic;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.dao.entity.transaction.Order;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private boolean enabled;

    @OneToMany(mappedBy = "paymentMethod", fetch = FetchType.LAZY)
    private List<Order> orders;

    public PaymentMethod(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }
}
