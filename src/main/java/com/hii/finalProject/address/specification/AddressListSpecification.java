package com.hii.finalProject.address.specification;

import com.hii.finalProject.address.entity.Address;
import com.hii.finalProject.users.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class AddressListSpecification {
    public static Specification<Address> byAddressLine(String addressLine){
        return ((root, query, cb) -> {
            if(addressLine == null || addressLine.isEmpty()){
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("addressLine")),"%" + addressLine.toLowerCase() + "%");
        });
    }

    public static Specification<Address> byUserId(Long userId) {
        return ((root, query, cb) -> {
            if (userId == null) {
                return cb.conjunction();
            }
            Join<Address, User> userJoin = root.join("user", JoinType.LEFT);
            return cb.equal(userJoin.get("id"), userId);
        });
    }

    public static Specification<Address> notDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get("deletedAt"));
    }
}
