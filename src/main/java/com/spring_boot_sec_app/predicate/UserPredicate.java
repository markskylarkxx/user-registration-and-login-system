package com.spring_boot_sec_app.predicate;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.spring_boot_sec_app.model.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring_boot_sec_app.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.function.Predicate;

@Data

public class UserPredicate  {

        // search for fields;
   public BooleanExpression searchByName(String searchTerm){
        if (searchTerm == null || searchTerm.isEmpty()){
            return  QUser.user.isNotNull();
        }

        else {
            return  QUser.user.name.containsIgnoreCase(searchTerm);
        }
    }

    }
