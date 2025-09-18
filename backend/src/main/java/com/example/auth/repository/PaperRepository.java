package com.example.auth.repository;

import com.example.auth.entity.Paper;
import com.example.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaperRepository extends JpaRepository<Paper, Long> {
    List<Paper> findByAuthor(User author);
    List<Paper> findByPublishedByCommittee(User committee);
    
    @Query("SELECT p FROM Paper p WHERE p.title LIKE %:keyword% OR p.abstractText LIKE %:keyword%")
    List<Paper> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT p FROM Paper p WHERE p.publicationDate IS NOT NULL ORDER BY p.publicationDate DESC")
    List<Paper> findPublishedPapersOrderByDate();
    
    @Query("SELECT p FROM Paper p WHERE p.publicationDate IS NULL")
    List<Paper> findUnpublishedPapers();
}
