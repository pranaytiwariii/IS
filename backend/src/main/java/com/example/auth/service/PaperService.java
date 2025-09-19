package com.example.auth.service;

import com.example.auth.dto.PaperRequest;
import com.example.auth.entity.Paper;
import com.example.auth.entity.Tag;
import com.example.auth.entity.User;
import com.example.auth.repository.PaperRepository;
import com.example.auth.repository.TagRepository;
import com.example.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PaperService {

    private static final Logger logger = LoggerFactory.getLogger(PaperService.class);

    @Autowired
    private PaperRepository paperRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    public Paper createPaper(PaperRequest paperRequest, String authorUsername) {
        logger.info("Creating paper '{}' for author: {}", paperRequest.getTitle(), authorUsername);
        
        Optional<User> userOptional = userRepository.findByUsername(authorUsername);
        if (userOptional.isEmpty()) {
            logger.error("Author not found: {}", authorUsername);
            throw new RuntimeException("Author not found");
        }

        User author = userOptional.get();
        Paper paper = new Paper(paperRequest.getTitle(), paperRequest.getAbstractText(), 
                               paperRequest.getContent(), author);

        // Handle tags (Many-to-Many relationship)
        if (paperRequest.getTags() != null && !paperRequest.getTags().isEmpty()) {
            logger.debug("Processing {} tags for paper: {}", paperRequest.getTags().size(), paperRequest.getTitle());
            Set<Tag> tags = new HashSet<>();
            for (String tagName : paperRequest.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        logger.debug("Creating new tag: {}", tagName);
                        Tag newTag = new Tag(tagName);
                        return tagRepository.save(newTag);
                    });
                tags.add(tag);
            }
            paper.setTags(tags);
        }

        Paper savedPaper = paperRepository.save(paper);
        logger.info("Paper created successfully with ID: {}, title: '{}'", savedPaper.getId(), savedPaper.getTitle());
        return savedPaper;
    }

    public List<Paper> searchPapers(String keyword) {
        logger.debug("Searching papers with keyword: '{}'", keyword != null ? keyword : "no keyword");
        
        List<Paper> papers;
        if (keyword == null || keyword.trim().isEmpty()) {
            papers = paperRepository.findPublishedPapersOrderByDate();
            logger.debug("Retrieved {} published papers (no keyword search)", papers.size());
        } else {
            papers = paperRepository.searchByKeyword(keyword);
            logger.debug("Found {} papers matching keyword: '{}'", papers.size(), keyword);
        }
        return papers;
    }

    public List<Paper> getPublishedPapers() {
        logger.debug("Retrieving all published papers");
        List<Paper> papers = paperRepository.findPublishedPapersOrderByDate();
        logger.debug("Retrieved {} published papers", papers.size());
        return papers;
    }

    public List<Paper> getUnpublishedPapers() {
        logger.debug("Retrieving all unpublished papers");
        List<Paper> papers = paperRepository.findUnpublishedPapers();
        logger.debug("Retrieved {} unpublished papers", papers.size());
        return papers;
    }

    public List<Paper> getPapersByAuthor(String authorUsername) {
        logger.debug("Retrieving papers by author: {}", authorUsername);
        
        Optional<User> userOptional = userRepository.findByUsername(authorUsername);
        if (userOptional.isEmpty()) {
            logger.error("Author not found: {}", authorUsername);
            throw new RuntimeException("Author not found");
        }
        
        List<Paper> papers = paperRepository.findByAuthor(userOptional.get());
        logger.debug("Found {} papers by author: {}", papers.size(), authorUsername);
        return papers;
    }

    public Paper publishPaper(Long paperId, String committeeUsername) {
        logger.info("Publishing paper ID: {} by committee member: {}", paperId, committeeUsername);
        
        Optional<Paper> paperOptional = paperRepository.findById(paperId);
        Optional<User> committeeOptional = userRepository.findByUsername(committeeUsername);

        if (paperOptional.isEmpty()) {
            logger.error("Paper not found with ID: {}", paperId);
            throw new RuntimeException("Paper not found");
        }
        if (committeeOptional.isEmpty()) {
            logger.error("Committee member not found: {}", committeeUsername);
            throw new RuntimeException("Committee member not found");
        }

        Paper paper = paperOptional.get();
        User committee = committeeOptional.get();

        paper.setPublishedByCommittee(committee);
        paper.setPublicationDate(LocalDateTime.now());

        Paper publishedPaper = paperRepository.save(paper);
        logger.info("Paper published successfully - ID: {}, title: '{}', published by: {}", 
                   publishedPaper.getId(), publishedPaper.getTitle(), committeeUsername);
        return publishedPaper;
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
