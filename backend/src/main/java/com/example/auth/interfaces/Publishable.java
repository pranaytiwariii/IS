package com.example.auth.interfaces;

/**
 * Interface demonstrating publishing capability
 * Committee members and senior users can publish documents
 */
public interface Publishable extends Writable {
    
    /**
     * Publish a document (make it publicly available)
     * @param documentId the ID of the document to publish
     * @return true if publishing was successful
     */
    boolean publishDocument(Long documentId);
    
    /**
     * Unpublish a document (remove from public access)
     * @param documentId the ID of the document to unpublish
     * @return true if unpublishing was successful
     */
    boolean unpublishDocument(Long documentId);
    
    /**
     * Review a document for publication
     * @param documentId the ID of the document to review
     * @param approved whether the document is approved for publication
     * @param comments reviewer comments
     * @return true if review was recorded successfully
     */
    boolean reviewDocument(Long documentId, boolean approved, String comments);
    
    /**
     * Get list of documents pending review
     * @return array of document IDs pending review
     */
    Long[] getPendingReviewDocuments();
    
    /**
     * Get publishing permissions
     * @return array of document types this user can publish
     */
    String[] getPublishableDocumentTypes();
    
    /**
     * Check if user can publish a specific document type
     * @param documentType the type of document
     * @return true if user can publish this type
     */
    default boolean canPublish(String documentType) {
        String[] publishableTypes = getPublishableDocumentTypes();
        for (String type : publishableTypes) {
            if (type.equalsIgnoreCase(documentType)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get count of documents published by this user
     * @return number of documents published
     */
    int getPublishedDocumentCount();
    
    /**
     * Get count of documents reviewed by this user
     * @return number of documents reviewed
     */
    int getReviewedDocumentCount();
}