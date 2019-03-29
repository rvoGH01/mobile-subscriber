package com.vodafone.mobile.controller;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.vodafone.mobile.model.MobileSubscriber;
import com.vodafone.mobile.repository.MobileSubscriberRepository;
import com.vodafone.mobile.service.MobileSubscriberService;

@RunWith(SpringRunner.class)
@WebMvcTest(MobileSubscriberController.class)
public class MobileSubscriberControllerTest {
    private final MobileSubscriber expected = newMobileSubscriber(1, "380964894066", "0001", "A0001", MobileSubscriber.ServiceType.MOBILE_POSTPAID);

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MobileSubscriberRepository repository;
    @Autowired
    private MobileSubscriberService service;

    @TestConfiguration
    static class MobileSubscriberServiceTestContextConfiguration {
        @Bean
        @Autowired
        public MobileSubscriberService mobileSubscriberService(MobileSubscriberRepository repository) {
            return new MobileSubscriberService(repository);
        }
    }


    // find all tests

    @Test
    public void findAllSuccessTestOne() throws Exception {
        List<MobileSubscriber> allMobileSubscribers = getMobileSubscribers();

        when(repository.findAll()).thenReturn(allMobileSubscribers);

        mockMvc.perform(get("/api/mobile-subscribers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(allMobileSubscribers.size())));

        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void findAllSuccessTestTwo() throws Exception {
        final String query = "msisdn~*66";

        when(repository.findAll(any(Specification.class))).thenReturn(Collections.singletonList(expected));

        mockMvc.perform(get("/api/mobile-subscribers")
                .param("query", query))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].msisdn").value(expected.getMsisdn()))
                .andExpect(jsonPath("$[0].customerIdOwner").value(expected.getCustomerIdOwner()))
                .andExpect(jsonPath("$[0].customerIdUser").value(expected.getCustomerIdUser()))
                .andExpect(jsonPath("$[0].serviceType").value(expected.getServiceType().name()));

        verify(repository, times(1)).findAll(any(Specification.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void findAllInternalServerErrorTest() throws Exception {
        doThrow(RuntimeException.class).when(repository).findAll();

        mockMvc.perform(
                get("/api/mobile-subscribers")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());

        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }

    // create tests

    @Test
    public void createSuccessTest() throws Exception {
        when(repository.findByMsisdn(any(String.class))).thenReturn(Optional.empty());
        when(repository.save(any(MobileSubscriber.class))).thenReturn(expected);

        this.mockMvc.perform(
                post("/api/mobile-subscribers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(expected))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.msisdn").value(expected.getMsisdn()))
                .andExpect(jsonPath("$.customerIdOwner").value(expected.getCustomerIdOwner()))
                .andExpect(jsonPath("$.customerIdUser").value(expected.getCustomerIdUser()))
                .andExpect(jsonPath("$.serviceType").value(expected.getServiceType().name()));

        verify(repository, times(1)).findByMsisdn(expected.getMsisdn());
        verify(repository, times(1)).save(any(MobileSubscriber.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void createBadRequestTest() throws Exception {
        MobileSubscriber unexpected = newMobileSubscriber(8, "380660200331", "221", "C221", MobileSubscriber.ServiceType.MOBILE_POSTPAID);

        when(repository.findByMsisdn(any(String.class))).thenReturn(Optional.of(unexpected));

        this.mockMvc.perform(
                post("/api/mobile-subscribers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(unexpected))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(repository, times(1)).findByMsisdn(unexpected.getMsisdn());
        verify(repository, times(0)).save(any(MobileSubscriber.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void createInternalServerErrorTest() throws Exception {
        when(repository.findByMsisdn(any(String.class))).thenReturn(Optional.empty());
        doThrow(RuntimeException.class).when(repository).save(any(MobileSubscriber.class));

        this.mockMvc.perform(
                post("/api/mobile-subscribers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(expected))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(repository, times(1)).findByMsisdn(expected.getMsisdn());
        verify(repository, times(1)).save(any(MobileSubscriber.class));
        verifyNoMoreInteractions(repository);
    }

    // update tests

    @Test
    public void updateSuccessTest() throws Exception {
        when(repository.findById(expected.getId())).thenReturn(Optional.of(expected));
        when(repository.save(any(MobileSubscriber.class))).thenReturn(expected);

        this.mockMvc.perform(
                put("/api/mobile-subscribers/{id}", expected.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(expected))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.msisdn").value(expected.getMsisdn()))
                .andExpect(jsonPath("$.customerIdOwner").value(expected.getCustomerIdOwner()))
                .andExpect(jsonPath("$.customerIdUser").value(expected.getCustomerIdUser()))
                .andExpect(jsonPath("$.serviceType").value(expected.getServiceType().name()));

        verify(repository, times(1)).findById(expected.getId());
        verify(repository, times(1)).save(any(MobileSubscriber.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void updateNotFoundTest() throws Exception {
        when(repository.findById(any(Integer.class))).thenReturn(Optional.empty());

        this.mockMvc.perform(
                put("/api/mobile-subscribers/{id}", expected.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(expected))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(repository, times(1)).findById(expected.getId());
        verify(repository, times(0)).save(any(MobileSubscriber.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void updateInternalServerErrorTest() throws Exception {
        when(repository.findById(expected.getId())).thenReturn(Optional.of(expected));
        doThrow(RuntimeException.class).when(repository).save(any(MobileSubscriber.class));

        this.mockMvc.perform(
                put("/api/mobile-subscribers/{id}", expected.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(expected))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(repository, times(1)).findById(expected.getId());
        verify(repository, times(1)).save(any(MobileSubscriber.class));
        verifyNoMoreInteractions(repository);
    }

    // delete by ID tests

    @Test
    public void deleteByIdSuccessTest() throws Exception {
        when(repository.findById(expected.getId())).thenReturn(Optional.of(expected));
        doNothing().when(repository).deleteById(expected.getId());

        mockMvc.perform(
                delete("/api/mobile-subscribers/{id}", expected.getId()))
                .andExpect(status().isOk());

        verify(repository, times(1)).findById(expected.getId());
        verify(repository, times(1)).deleteById(expected.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void deleteByIdNotFoundTest() throws Exception {
        when(repository.findById(expected.getId())).thenReturn(Optional.empty());

        mockMvc.perform(
                delete("/api/mobile-subscribers/{id}", expected.getId()))
                .andExpect(status().isNotFound());

        verify(repository, times(1)).findById(expected.getId());
        verify(repository, times(0)).deleteById(expected.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void deleteByIdInternalServerErrorTest() throws Exception {
        when(repository.findById(expected.getId())).thenReturn(Optional.of(expected));
        doThrow(RuntimeException.class).when(repository).deleteById(expected.getId());

        mockMvc.perform(
                delete("/api/mobile-subscribers/{id}", expected.getId()))
                .andExpect(status().isInternalServerError());

        verify(repository, times(1)).findById(expected.getId());
        verify(repository, times(1)).deleteById(expected.getId());
        verifyNoMoreInteractions(repository);
    }

    // delete by MSISDN tests

    @Test
    public void deleteByMsisdnSuccessTest() throws Exception {
        when(repository.findByMsisdn(expected.getMsisdn())).thenReturn(Optional.of(expected));
        doNothing().when(repository).deleteByMsisdn(expected.getMsisdn());

        mockMvc.perform(
                delete("/api/mobile-subscribers")
                    .param("msisdn", expected.getMsisdn()))
                .andExpect(status().isOk());

        verify(repository, times(1)).findByMsisdn(expected.getMsisdn());
        verify(repository, times(1)).deleteByMsisdn(expected.getMsisdn());
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void deleteByMsisdnNotFoundTest() throws Exception {
        when(repository.findByMsisdn(expected.getMsisdn())).thenReturn(Optional.empty());

        mockMvc.perform(
                delete("/api/mobile-subscribers")
                        .param("msisdn", expected.getMsisdn()))
                .andExpect(status().isNotFound());

        verify(repository, times(1)).findByMsisdn(expected.getMsisdn());
        verify(repository, times(0)).deleteByMsisdn(expected.getMsisdn());
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void deleteByMsisdnInternalServerErrorTest() throws Exception {
        when(repository.findByMsisdn(expected.getMsisdn())).thenReturn(Optional.of(expected));
        doThrow(RuntimeException.class).when(repository).deleteByMsisdn(expected.getMsisdn());

        mockMvc.perform(
                delete("/api/mobile-subscribers")
                        .param("msisdn", expected.getMsisdn()))
                .andExpect(status().isInternalServerError());

        verify(repository, times(1)).findByMsisdn(expected.getMsisdn());
        verify(repository, times(1)).deleteByMsisdn(expected.getMsisdn());
        verifyNoMoreInteractions(repository);
    }

    // helpers

    private String asJsonString(MobileSubscriber ms) throws JsonProcessingException {
        return mapper.writeValueAsString(ms);
    }

    private List<MobileSubscriber> getMobileSubscribers() {
        List<MobileSubscriber> list = Lists.newArrayList(
                newMobileSubscriber(1, "380964894066", "0001", "A0001", MobileSubscriber.ServiceType.MOBILE_POSTPAID),
                newMobileSubscriber(2, "380670000101", "0002", "A0002", MobileSubscriber.ServiceType.MOBILE_PREPAID),
                newMobileSubscriber(3, "380670000102", "0003", "A0003", MobileSubscriber.ServiceType.MOBILE_POSTPAID),
                newMobileSubscriber(4, "380680000102", "0004", "A0004", MobileSubscriber.ServiceType.MOBILE_POSTPAID),
                newMobileSubscriber(5, "380680000101", "0005", "A0005", MobileSubscriber.ServiceType.MOBILE_PREPAID),
                newMobileSubscriber(6, "380690100101", "0006", "A0006", MobileSubscriber.ServiceType.MOBILE_POSTPAID),
                newMobileSubscriber(7, "380680200101", "0007", "A0007", MobileSubscriber.ServiceType.MOBILE_PREPAID)
        );
        return list;
    }

    private MobileSubscriber newMobileSubscriber(Integer id, String msisdn, String customerIdOwner,
            String customerIdUser, MobileSubscriber.ServiceType serviceType) {
        MobileSubscriber ms = new MobileSubscriber();
        ms.setId(id);
        ms.setMsisdn(msisdn);
        ms.setCustomerIdOwner(customerIdOwner);
        ms.setCustomerIdUser(customerIdUser);
        ms.setServiceType(serviceType);
        ms.setServiceStartDate(System.currentTimeMillis());
        return ms;
    }
}
