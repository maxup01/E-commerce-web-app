package org.example.backend.dao.entity.transaction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

//Entity for storing causes of returns
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnCause {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String cause;

    @OneToMany(mappedBy = "returnCause", fetch = FetchType.LAZY)
    private List<ReturnTransaction> returnTransactions;

    public ReturnCause(String cause) {
        this.cause = cause;
        this.returnTransactions = new ArrayList<>();
    }
}
