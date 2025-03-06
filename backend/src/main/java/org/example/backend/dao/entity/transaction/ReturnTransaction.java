package org.example.backend.dao.entity.transaction;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.dao.entity.logistic.Address;
import org.example.backend.dao.entity.logistic.DeliveryProvider;
import org.example.backend.dao.entity.user.User;
import org.example.backend.enumerated.ReturnCause;
import org.example.backend.enumerated.TransactionStatus;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnCause returnCause;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST})
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address deliveryAddress;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "delivery_provider_id", referencedColumnName = "id")
    private DeliveryProvider deliveryProvider;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "returnTransaction")
    private List<ReturnedProduct> returnedProducts;

    public ReturnTransaction(Date transactionDate, User user, Address deliveryAddress, DeliveryProvider deliveryProvider,
                             ReturnCause returnCause, List<ReturnedProduct> returnedProducts) {



        super(transactionDate, TransactionStatus.RETURN_ACCEPTED,
                user.getFirstName() + user.getLastName(), user.getEmail(), 0.00);

        Double moneyAmount = returnedProducts.stream().mapToDouble(returnedProduct -> {
            return returnedProduct.getQuantity() * returnedProduct.getPricePerUnit();
        }).sum();

        super.setCost(moneyAmount);
        this.user = user;
        this.deliveryAddress = deliveryAddress;
        this.deliveryProvider = deliveryProvider;
        this.returnCause = returnCause;
        this.returnedProducts = returnedProducts;
    }
}
