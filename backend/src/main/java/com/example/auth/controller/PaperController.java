package com.example.auth.controller;

import com.example.auth.dto.MessageResponse;
import com.example.auth.dto.PaperRequest;
import com.example.auth.entity.Paper;
import com.example.auth.service.AuthService;
import com.example.auth.service.PaperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/papers")
@Tag(name = "Papers", description = "Paper management APIs")
public class PaperController {

    @Autowired
    private PaperService paperService;

    @Autowired
    private AuthService authService;

    @PostMapping("/create")
    @Operation(
        summary = "Create a new paper",
        description = "Create a new research paper (only authors can create papers)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Paper created successfully",
            content = @Content(schema = @Schema(implementation = MessageResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - User not authorized or invalid data",
            content = @Content(schema = @Schema(implementation = MessageResponse.class))
        )
    })
    public ResponseEntity<?> createPaper(
            @Parameter(description = "Paper details", required = true)
            @Valid @RequestBody PaperRequest paperRequest,
            @Parameter(description = "Author username", required = true)
            @RequestParam String authorUsername) {
        try {
            // Check if user exists and has AUTHOR role
            String userRole = authService.getUserRole(authorUsername);
            if (!"AUTHOR".equals(userRole)) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Only authors can create papers!"));
            }

            Paper paper = paperService.createPaper(paperRequest, authorUsername);
            return ResponseEntity.ok(new MessageResponse("Paper created successfully with ID: " + paper.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    @Operation(
        summary = "Search papers",
        description = "Search papers by keyword in title or abstract"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Search results retrieved successfully",
            content = @Content(schema = @Schema(implementation = Paper.class))
        )
    })
    public ResponseEntity<List<Paper>> searchPapers(
            @Parameter(description = "Search keyword", required = false)
            @RequestParam(required = false) String keyword) {
        List<Paper> papers = paperService.searchPapers(keyword);
        return ResponseEntity.ok(papers);
    }

    @GetMapping("/published")
    public ResponseEntity<List<Paper>> getPublishedPapers() {
        List<Paper> papers = paperService.getPublishedPapers();
        return ResponseEntity.ok(papers);
    }

    @GetMapping("/unpublished")
    public ResponseEntity<List<Paper>> getUnpublishedPapers() {
        List<Paper> papers = paperService.getUnpublishedPapers();
        return ResponseEntity.ok(papers);
    }

    @GetMapping("/author/{username}")
    public ResponseEntity<List<Paper>> getPapersByAuthor(@PathVariable String username) {
        try {
            List<Paper> papers = paperService.getPapersByAuthor(username);
            return ResponseEntity.ok(papers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/publish/{paperId}")
    public ResponseEntity<?> publishPaper(@PathVariable Long paperId,
                                         @RequestParam String committeeUsername) {
        try {
            // Check if user has COMMITTEE role
            String userRole = authService.getUserRole(committeeUsername);
            if (!"COMMITTEE".equals(userRole)) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Only committee members can publish papers!"));
            }

            Paper paper = paperService.publishPaper(paperId, committeeUsername);
            return ResponseEntity.ok(new MessageResponse("Paper published successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/committee/{username}")
    public ResponseEntity<List<Paper>> getPapersPublishedByCommittee(@PathVariable String username) {
        try {
            List<Paper> papers = paperService.getPapersPublishedByCommittee(username);
            return ResponseEntity.ok(papers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paper> getPaperById(@PathVariable Long id) {
        Optional<Paper> paper = paperService.getPaperById(id);
        if (paper.isPresent()) {
            return ResponseEntity.ok(paper.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/all")
    @Operation(
        summary = "Get all papers",
        description = "Retrieve all papers in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Papers retrieved successfully",
            content = @Content(schema = @Schema(implementation = Paper.class))
        )
    })
    public ResponseEntity<List<Paper>> getAllPapers() {
        List<Paper> papers = paperService.getAllPapers();
        return ResponseEntity.ok(papers);
    }

    // Default endpoint for /api/papers/ - returns all papers
    @GetMapping("/")
    public ResponseEntity<List<Paper>> getPapers() {
        List<Paper> papers = paperService.getAllPapers();
        return ResponseEntity.ok(papers);
    }

    // Also handle GET without trailing slash
    @GetMapping("")
    public ResponseEntity<List<Paper>> getPapersNoSlash() {
        List<Paper> papers = paperService.getAllPapers();
        return ResponseEntity.ok(papers);
    }

    // Test endpoint to check if controller is working
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Paper controller is working!");
    }
}
