package com.example.auth.entity.inheritance;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Policy Document concrete class extending AbstractDocument
 * Another concrete implementation showing different behavior
 */
@Entity
@Table(name = "policy_documents")
@DiscriminatorValue("POLICY_DOCUMENT")
public class PolicyDocument extends AbstractDocument {
    
    @Size(max = 50)
    private String policyNumber;
    
    @Size(max = 100)
    private String department;
    
    @Column(name = "effective_date")
    private LocalDateTime effectiveDate;
    
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;
    
    @Column(name = "compliance_level")
    private String complianceLevel; // "MANDATORY", "RECOMMENDED", "OPTIONAL"
    
    @ElementCollection
    @CollectionTable(name = "policy_stakeholders", joinColumns = @JoinColumn(name = "policy_id"))
    @Column(name = "stakeholder")
    private List<String> stakeholders = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "policy_approvers", joinColumns = @JoinColumn(name = "policy_id"))
    @Column(name = "approver_name")
    private List<String> approvers = new ArrayList<>();
    
    @Size(max = 1000)
    private String summary;
    
    @Column(name = "version_number")
    private String versionNumber = "1.0";
    
    @Column(name = "review_frequency_months")
    private Integer reviewFrequencyMonths = 12;
    
    // Default constructor
    public PolicyDocument() {
        super();
    }
    
    // Constructor
    public PolicyDocument(String title, String content, String authorName, Long authorId,
                         String policyNumber, String department, String complianceLevel) {
        super(title, content, authorName, authorId);
        this.policyNumber = policyNumber;
        this.department = department;
        this.complianceLevel = complianceLevel;
        this.effectiveDate = LocalDateTime.now().plusDays(30); // Default 30 days from creation
    }
    
    // Implementation of abstract methods from AbstractDocument
    
    @Override
    public String getDocumentType() {
        return "Policy Document";
    }
    
    @Override
    public boolean validateContent() {
        // Policy documents have different validation requirements
        if (!isContentValid()) return false; // Use inherited method
        
        // Additional validation for policy documents
        return policyNumber != null && !policyNumber.trim().isEmpty() &&
               department != null && !department.trim().isEmpty() &&
               complianceLevel != null && !complianceLevel.trim().isEmpty() &&
               approvers.size() >= 1 && // At least one approver required
               getContentWordCount() >= 100; // Minimum 100 words for policies
    }
    
    @Override
    public String formatForDisplay() {
        return String.format("""
            === POLICY DOCUMENT FORMATTING ===
            Policy Number: %s
            Department: %s
            Compliance Level: %s
            Version: %s
            Effective Date: %s
            Expiration Date: %s
            Review Frequency: %d months
            Approvers: %s
            Stakeholders: %s
            Summary: %s
            """,
            policyNumber != null ? policyNumber : "Not assigned",
            department != null ? department : "Not specified",
            complianceLevel != null ? complianceLevel : "Not defined",
            versionNumber,
            effectiveDate != null ? effectiveDate.toLocalDate().toString() : "Not set",
            expirationDate != null ? expirationDate.toLocalDate().toString() : "No expiration",
            reviewFrequencyMonths,
            approvers.isEmpty() ? "None" : String.join(", ", approvers),
            stakeholders.isEmpty() ? "None" : String.join(", ", stakeholders),
            summary != null ? (summary.length() > 100 ? summary.substring(0, 100) + "..." : summary) : "Not provided"
        );
    }
    
    @Override
    public String getCategory() {
        return "Organizational Policy";
    }
    
    // Policy document specific methods
    
    public void addApprover(String approver) {
        if (approver != null && !approver.trim().isEmpty()) {
            approvers.add(approver);
            this.setUpdatedAt(LocalDateTime.now());
        }
    }
    
    public void addStakeholder(String stakeholder) {
        if (stakeholder != null && !stakeholder.trim().isEmpty()) {
            stakeholders.add(stakeholder);
            this.setUpdatedAt(LocalDateTime.now());
        }
    }
    
    public void updateVersion(String newVersion) {
        this.versionNumber = newVersion;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public boolean isCurrentlyEffective() {
        LocalDateTime now = LocalDateTime.now();
        return effectiveDate != null && effectiveDate.isBefore(now) &&
               (expirationDate == null || expirationDate.isAfter(now));
    }
    
    public boolean needsReview() {
        if (reviewFrequencyMonths == null || getUpdatedAt() == null) return false;
        
        LocalDateTime nextReview = getUpdatedAt().plusMonths(reviewFrequencyMonths);
        return LocalDateTime.now().isAfter(nextReview);
    }
    
    public void extendExpiration(int additionalMonths) {
        if (expirationDate == null) {
            expirationDate = LocalDateTime.now().plusMonths(additionalMonths);
        } else {
            expirationDate = expirationDate.plusMonths(additionalMonths);
        }
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public void markForReview() {
        // Add keyword to indicate review needed
        this.addKeyword("review-required"); // Use inherited method
    }
    
    public String getPolicyStatus() {
        if (!validateContent()) return "Invalid";
        if (!isCurrentlyEffective()) return "Not Effective";
        if (needsReview()) return "Needs Review";
        return "Active";
    }
    
    public String getPolicyDocumentProfile() {
        return String.format("""
            === POLICY DOCUMENT PROFILE ===
            %s
            
            === POLICY-SPECIFIC DETAILS ===
            Policy Number: %s
            Department: %s
            Version: %s
            Compliance Level: %s
            Policy Status: %s
            Currently Effective: %s
            Needs Review: %s
            Approval Status: %d approvers
            Stakeholder Involvement: %d stakeholders
            Review Schedule: Every %d months
            """,
            getCompleteInfo(), // Use inherited template method
            policyNumber,
            department,
            versionNumber,
            complianceLevel,
            getPolicyStatus(),
            isCurrentlyEffective(),
            needsReview(),
            approvers.size(),
            stakeholders.size(),
            reviewFrequencyMonths
        );
    }
    
    // Method demonstrating abstract class inheritance with different implementation
    public String getAbstractInheritanceDemo() {
        return String.format("""
            === ABSTRACT CLASS INHERITANCE DEMONSTRATION ===
            Concrete Class: PolicyDocument
            Extends: AbstractDocument (same abstract class as ResearchPaper)
            
            === DIFFERENT IMPLEMENTATIONS OF ABSTRACT METHODS ===
            getDocumentType(): "%s" (vs ResearchPaper's "Research Paper")
            validateContent(): %s (policy-specific: approvers, compliance level)
            formatForDisplay(): Policy-specific formatting (vs research formatting)  
            getCategory(): "%s" (vs ResearchPaper's "Academic Research")
            
            === SAME INHERITED CONCRETE METHODS ===
            getCompleteInfo(): %s (same template method pattern)
            publish(): %s (same publishing logic)
            addKeyword(): Available (same implementation)
            getStatus(): "%s" (same status logic)
            getContentWordCount(): %d words (same counting logic)
            
            === SAME INHERITED PROPERTIES ===
            ID: %d
            Title: %s
            Author: %s (ID: %d)
            Created: %s
            Keywords: %s
            Views: %d
            
            === POLICY DOCUMENT SPECIFIC PROPERTIES ===
            Policy Number: %s
            Department: %s
            Compliance Level: %s
            Version: %s
            Effective Date: %s
            Expiration Date: %s
            Approvers: %d
            Stakeholders: %d
            Currently Effective: %s
            Needs Review: %s
            
            === COMPARISON WITH ResearchPaper ===
            Both extend AbstractDocument but implement abstract methods differently:
            - ResearchPaper focuses on academic validation (peer review, citations)
            - PolicyDocument focuses on organizational compliance (approvers, effective dates)
            - Both use same inherited concrete methods but different abstract implementations
            """,
            getDocumentType(),
            validateContent(),
            getCategory(),
            "Uses same template method pattern",
            getIsPublished() ? "Published" : "Can publish",
            getStatus(),
            getContentWordCount(),
            getId() != null ? getId() : 0,
            getTitle() != null ? getTitle() : "Not set",
            getAuthorName() != null ? getAuthorName() : "Not set",
            getAuthorId() != null ? getAuthorId() : 0,
            getCreatedAt() != null ? getCreatedAt().toLocalDate().toString() : "Not set",
            getKeywords().isEmpty() ? "None" : String.join(", ", getKeywords()),
            getViewCount(),
            policyNumber,
            department,
            complianceLevel,
            versionNumber,
            effectiveDate != null ? effectiveDate.toLocalDate().toString() : "Not set",
            expirationDate != null ? expirationDate.toLocalDate().toString() : "No expiration",
            approvers.size(),
            stakeholders.size(),
            isCurrentlyEffective(),
            needsReview()
        );
    }
    
    // Getters and Setters
    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public LocalDateTime getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDateTime effectiveDate) { this.effectiveDate = effectiveDate; }
    
    public LocalDateTime getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDateTime expirationDate) { this.expirationDate = expirationDate; }
    
    public String getComplianceLevel() { return complianceLevel; }
    public void setComplianceLevel(String complianceLevel) { this.complianceLevel = complianceLevel; }
    
    public List<String> getStakeholders() { return stakeholders; }
    public void setStakeholders(List<String> stakeholders) { this.stakeholders = stakeholders; }
    
    public List<String> getApprovers() { return approvers; }
    public void setApprovers(List<String> approvers) { this.approvers = approvers; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public String getVersionNumber() { return versionNumber; }
    public void setVersionNumber(String versionNumber) { this.versionNumber = versionNumber; }
    
    public Integer getReviewFrequencyMonths() { return reviewFrequencyMonths; }
    public void setReviewFrequencyMonths(Integer reviewFrequencyMonths) { this.reviewFrequencyMonths = reviewFrequencyMonths; }
}