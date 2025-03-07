package org.example.backend.dao.entity.logistic;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.dao.entity.transaction.OrderTransaction;
import org.example.backend.dao.entity.transaction.ReturnTransaction;

import java.util.ArrayList;
import java.util.List;

//Entity for storing address data
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String province;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String address;

    @OneToMany(mappedBy = "deliveryAddress", fetch = FetchType.LAZY)
    private List<OrderTransaction> orderTransactions;

    @OneToMany(mappedBy = "deliveryAddress", fetch = FetchType.LAZY)
    private List<ReturnTransaction> returnTransactions;

    public Address(String country, String province, String city, String address) {
        this.country = country;
        this.province = province;
        this.city = city;
        this.address = address;
        this.orderTransactions = new ArrayList<>();
        this.returnTransactions = new ArrayList<>();
    }
}
