package com.example.auth.interfaces;

/**
 * Interface demonstrating writing capability
 * Authors and some advanced users can write documents
 */
public interface Writable extends Readable {
    
    /**
     * Create a new document
     * @param title the document title
     * @param content the document content
     * @return the ID of the created document
     */
    Long createDocument(String title, String content);
    
    /**
     * Edit an existing document
     * @param documentId the ID of the document to edit
     * @param newContent the new content
     * @return true if edit was successful
     */
    boolean editDocument(Long documentId, String newContent);
    
    /**
     * Delete a document
     * @param documentId the ID of the document to delete
     * @return true if deletion was successful
     */
    boolean deleteDocument(Long documentId);
    
    /**
     * Get writing permissions for a user
     * @return array of document types this user can write
     */
    String[] getWritableDocumentTypes();
    
    /**
     * Check if user can write a specific document type
     * @param documentType the type of document
     * @return true if user can write this type
     */
    default boolean canWrite(String documentType) {
        String[] writableTypes = getWritableDocumentTypes();
        for (String type : writableTypes) {
            if (type.equalsIgnoreCase(documentType)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get creation count
     * @return number of documents created by this user
     */
    int getCreatedDocumentCount();
    
    /**
     * Check if user owns a document
     * @param documentId the document ID
     * @return true if user owns this document
     */
    boolean ownsDocument(Long documentId);
}