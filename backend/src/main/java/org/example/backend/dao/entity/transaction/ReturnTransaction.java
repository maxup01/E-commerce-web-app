package org.example.backend.dao.entity.transaction;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.dao.entity.logistic.Address;
import org.example.backend.dao.entity.logistic.DeliveryProvider;
import org.example.backend.dao.entity.user.User;

import java.util.Date;
import java.util.List;

//Entity for storing data about returns
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnTransaction extends Transaction {

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST})
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address deliveryAddress;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "delivery_provider_id", referencedColumnName = "id")
    private DeliveryProvider deliveryProvider;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "retrurn_cause_id", referencedColumnName = "id")
    private ReturnCause returnCause;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "returnTransaction")
    private List<ReturnedProduct> returnedProducts;

    public ReturnTransaction(Date transactionDate, User user, Address deliveryAddress, DeliveryProvider deliveryProvider, ReturnCause returnCause, List<ReturnedProduct> returnedProducts) {
        super(transactionDate);
        this.user = user;
        this.deliveryAddress = deliveryAddress;
        this.deliveryProvider = deliveryProvider;
        this.returnCause = returnCause;
        this.returnedProducts = returnedProducts;
    }
}
