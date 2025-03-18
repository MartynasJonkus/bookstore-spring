package com.bookstore.service;

import com.bookstore.mappers.PublisherMapper;
import com.bookstore.model.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PublisherService {

    private final PublisherMapper publisherMapper;

    @Autowired
    public PublisherService(PublisherMapper publisherMapper) {
        this.publisherMapper = publisherMapper;
    }

    public List<Publisher> findAllPublishers() {
        return publisherMapper.findAll();
    }

    public Optional<Publisher> findPublisherById(Long id) {
        return Optional.ofNullable(publisherMapper.findById(id));
    }

    @Transactional
    public Publisher savePublisher(Publisher publisher) {
        if (publisher.getId() != null) {
            publisherMapper.update(publisher);
        } else {
            publisherMapper.insert(publisher);
        }
        return publisher;
    }

    @Transactional
    public void deletePublisherById(Long id) {
        publisherMapper.deleteById(id);
    }
}
