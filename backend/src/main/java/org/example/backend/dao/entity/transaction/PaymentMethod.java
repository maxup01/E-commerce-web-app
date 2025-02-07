package org.example.backend.dao.entity.transaction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//Entity for storing payment method data
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
    private List<OrderTransaction> orderTransactions;

    public PaymentMethod(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }
}
