package com.hii.finalProject.users.specification;

import com.hii.finalProject.address.entity.Address;
import com.hii.finalProject.users.entity.Role;
import com.hii.finalProject.users.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> byEmail(String email){
        return ((root, query, cb) -> {
            if(email == null || email.isEmpty()){
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("email")),"%" + email.toLowerCase() + "%");
        });
    }

    public static Specification<User  > byRole(Role role) {
        return ((root, query, cb) -> {
            if(role == null){
                return cb.conjunction();
            }
            return cb.equal(root.get("role"), role);
        });
    }

    public static Specification<User> notDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get("deletedAt"));
    }
}
