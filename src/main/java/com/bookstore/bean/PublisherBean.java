package com.bookstore.bean;

import com.bookstore.model.Publisher;
import com.bookstore.service.PublisherService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
@Data
@NoArgsConstructor
public class PublisherBean implements Serializable {

    @Autowired
    private transient PublisherService publisherService;

    private List<Publisher> publishers;
    private Publisher publisher = new Publisher();
    private Long editId;

    @PostConstruct
    public void init() {
        System.out.println("Loading all publishers");
        publishers = publisherService.findAllPublishers();
    }

    public String save() {
        publisherService.savePublisher(publisher);
        return "/publishers.xhtml?faces-redirect=true";
    }

    public String delete(Long id) {
        System.out.println("Delete publisher with id: " + id);
        publisherService.deletePublisherById(id);
        return "/publishers.xhtml?faces-redirect=true";
    }

    public void loadPublisher() {
        System.out.println("Load publisher");
        if(editId != null) {
            publisher = publisherService.findPublisherById(editId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Publisher ID: " + editId));
        }
    }
}