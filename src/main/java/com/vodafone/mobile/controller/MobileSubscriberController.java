package com.vodafone.mobile.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.vodafone.mobile.model.MobileSubscriber;
import com.vodafone.mobile.service.MobileSubscriberService;

@RestController
@RequestMapping("/api/mobile-subscribers")
public class MobileSubscriberController {
    private final static Logger LOG = LoggerFactory.getLogger(MobileSubscriberController.class);

    private final MobileSubscriberService service;

    @Autowired
    public MobileSubscriberController(MobileSubscriberService service) {
        this.service = service;
    }

    @GetMapping
    public List<MobileSubscriber> findAll(@RequestParam(required = false) String query) {
        LOG.debug("GET /api/mobile-subscribers, query: '{}'", query);
        try {
            return service.findAll(query);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Mobile subscribers retrieval failed", ex);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MobileSubscriber create(@RequestBody MobileSubscriber mobileSubscriber) {
        LOG.debug("POST /api/mobile-subscribers, mobileSubscriber: '{}'", mobileSubscriber);
        Optional<MobileSubscriber> available = service.findByMsisdn(mobileSubscriber.getMsisdn());
        if (available.isPresent()) {
            LOG.warn("Mobile subscriber with msisdn='{}' already exists.", mobileSubscriber.getMsisdn());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mobile subscriber exists already");
        }
        try {
            return service.save(mobileSubscriber);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Mobile subscriber update failed", ex);
        }
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MobileSubscriber update(@PathVariable Integer id, @RequestBody MobileSubscriber mobileSubscriber) {
        LOG.debug("PUT /api/mobile-subscribers/{}, mobileSubscriber: '{}'", id, mobileSubscriber);
        Optional<MobileSubscriber> available = service.findById(id);
        if (!available.isPresent()) {
            LOG.warn("There is no mobile subscriber with id='{}' to update.", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mobile subscriber not found");
        }
        mobileSubscriber.setId(id);
        try {
            return service.update(mobileSubscriber);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Mobile subscriber UPDATE failed", ex);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable("id") Integer id) {
        LOG.debug("DELETE /api/mobile-subscribers/{}", id);
        Optional<MobileSubscriber> available = service.findById(id);
        if (!available.isPresent()) {
            LOG.warn("There is no mobile subscriber with id='{}' to delete.", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mobile subscriber not found");
        }
        try {
            service.deleteById(id);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Mobile subscriber DELETE failed", ex);
        }
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteByMsisdn(@RequestParam(required = true) String msisdn) {
        LOG.debug("DELETE /api/mobile-subscribers?msisdn={}", msisdn);
        Optional<MobileSubscriber> available = service.findByMsisdn(msisdn);
        if (!available.isPresent()) {
            LOG.warn("There is no mobile subscriber with msisdn='{}' to delete.", msisdn);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mobile subscriber not found");
        }
        try {
            service.deleteByMsisdn(msisdn);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Mobile subscriber DELETE failed", ex);
        }
    }
}