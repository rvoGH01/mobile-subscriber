package com.vodafone.mobile.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.vodafone.mobile.model.MobileSubscriber;

@Repository
public interface MobileSubscriberRepository extends JpaRepository<MobileSubscriber, Integer>, JpaSpecificationExecutor<MobileSubscriber> {
    Optional<MobileSubscriber> findByMsisdn(String msisdn);
    void deleteByMsisdn(String msisdn);
}