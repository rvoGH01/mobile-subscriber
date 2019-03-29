package com.vodafone.mobile.repository.specification;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.vodafone.mobile.model.MobileSubscriber;

public class MobileSubscriberSpecificationsBuilder {
    private static final String LIKE_OPERATION = "~";

    /**
     * Query examples:
     * - msisdn~067*
     * - msisdn~*430*
     */
    public Specification<MobileSubscriber> build(String query) {
        if (StringUtils.isBlank(query)) {
            return null;
        }
        final String[] args = query.split(LIKE_OPERATION);
        if (args.length != 2) {
            return null;
        }

        return (Specification<MobileSubscriber>) (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.like(root.<String> get(args[0]), args[1].replace("*", "%"));
    }
}