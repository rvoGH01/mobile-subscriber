package com.vodafone.mobile.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vodafone.mobile.model.MobileSubscriber;
import com.vodafone.mobile.repository.MobileSubscriberRepository;
import com.vodafone.mobile.repository.specification.MobileSubscriberSpecificationsBuilder;

@Service
public class MobileSubscriberService {
    private final MobileSubscriberRepository repository;

    private Lock writeLock = new ReentrantReadWriteLock().writeLock();

    @Autowired
    public MobileSubscriberService(MobileSubscriberRepository repository) {
        this.repository = repository;
    }

    public List<MobileSubscriber> findAll(String query) {
        if (StringUtils.isBlank(query)) {
            return repository.findAll();
        } else {
            Specification<MobileSubscriber> spec = new MobileSubscriberSpecificationsBuilder().build(query);
            return null == spec ? repository.findAll() : repository.findAll(spec);
        }
    }

    public Optional<MobileSubscriber> findByMsisdn(String msisdn) {
        return repository.findByMsisdn(msisdn);
    }

    public Optional<MobileSubscriber> findById(Integer id) {
        return repository.findById(id);
    }

    public MobileSubscriber update(MobileSubscriber mobileSubscriber) {
        return save(mobileSubscriber);
    }

    public MobileSubscriber save(MobileSubscriber mobileSubscriber) {
        mobileSubscriber.setServiceStartDate(System.currentTimeMillis());
        try {
            writeLock.lock();
            return repository.save(mobileSubscriber);
        } finally {
            writeLock.unlock();
        }

    }

    @Transactional
    public void deleteById(Integer id) {
        try {
            writeLock.lock();
            repository.deleteById(id);
        } finally {
            writeLock.unlock();
        }
    }

    @Transactional
    public void deleteByMsisdn(String msisdn) {
        try {
            writeLock.lock();
            repository.deleteByMsisdn(msisdn);
        } finally {
            writeLock.unlock();
        }
    }
}