package com.bookstore.bean;

import com.bookstore.model.Publisher;
import com.bookstore.service.PublisherService;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
@Scope("view")
@Data
@NoArgsConstructor
public class PublisherBean implements Serializable{

    @Autowired
    private transient PublisherService publisherService;

    private List<Publisher> publishers;
    private Publisher publisher = new Publisher();
    private Long editId;

    @PostConstruct
    public void init() {
        publishers = publisherService.findAllPublishers();
    }

    public String save() {
        publisherService.savePublisher(publisher);
        return "/publishers.xhtml?faces-redirect=true";
    }

    public String delete(Long id) {
        publisherService.deletePublisherById(id);
        return "/publishers.xhtml?faces-redirect=true";
    }

    public void loadPublisher() {
        if(editId != null) {
            publisher = publisherService.findPublisherById(editId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Publisher ID: " + editId));
        }
    }
}