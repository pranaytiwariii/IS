package com.example.auth.service;

import com.example.auth.dto.PaperRequest;
import com.example.auth.entity.Paper;
import com.example.auth.entity.Tag;
import com.example.auth.entity.User;
import com.example.auth.repository.PaperRepository;
import com.example.auth.repository.TagRepository;
import com.example.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PaperService {

    @Autowired
    private PaperRepository paperRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    public Paper createPaper(PaperRequest paperRequest, String authorUsername) {
        Optional<User> userOptional = userRepository.findByUsername(authorUsername);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Author not found");
        }

        User author = userOptional.get();
        Paper paper = new Paper(paperRequest.getTitle(), paperRequest.getAbstractText(), 
                               paperRequest.getContent(), author);

        // Handle tags (Many-to-Many relationship)
        if (paperRequest.getTags() != null && !paperRequest.getTags().isEmpty()) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : paperRequest.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag(tagName);
                        return tagRepository.save(newTag);
                    });
                tags.add(tag);
            }
            paper.setTags(tags);
        }

        return paperRepository.save(paper);
    }

    public List<Paper> searchPapers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return paperRepository.findPublishedPapersOrderByDate();
        }
        return paperRepository.searchByKeyword(keyword);
    }

    public List<Paper> getPublishedPapers() {
        return paperRepository.findPublishedPapersOrderByDate();
    }

    public List<Paper> getUnpublishedPapers() {
        return paperRepository.findUnpublishedPapers();
    }

    public List<Paper> getPapersByAuthor(String authorUsername) {
        Optional<User> userOptional = userRepository.findByUsername(authorUsername);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Author not found");
        }
        return paperRepository.findByAuthor(userOptional.get());
    }

    public Paper publishPaper(Long paperId, String committeeUsername) {
        Optional<Paper> paperOptional = paperRepository.findById(paperId);
        Optional<User> committeeOptional = userRepository.findByUsername(committeeUsername);

        if (paperOptional.isEmpty()) {
            throw new RuntimeException("Paper not found");
        }
        if (committeeOptional.isEmpty()) {
            throw new RuntimeException("Committee member not found");
        }

        Paper paper = paperOptional.get();
        User committee = committeeOptional.get();

        paper.setPublishedByCommittee(committee);
        paper.setPublicationDate(LocalDateTime.now());

        return paperRepository.save(paper);
    }

    public List<Paper> getPapersPublishedByCommittee(String committeeUsername) {
        Optional<User> userOptional = userRepository.findByUsername(committeeUsername);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Committee member not found");
        }
        return paperRepository.findByPublishedByCommittee(userOptional.get());
    }

    public Optional<Paper> getPaperById(Long id) {
        return paperRepository.findById(id);
    }

    public List<Paper> getAllPapers() {
        return paperRepository.findAll();
    }
}
