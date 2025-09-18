package com.example.auth.interfaces;

/**
 * Interface demonstrating basic capability - reading documents
 * All users should be able to read documents
 */
public interface Readable {
    
    /**
     * Read a document by its ID
     * @param documentId the ID of the document to read
     * @return true if reading was successful
     */
    boolean readDocument(Long documentId);
    
    /**
     * Get reading permissions for a user
     * @return array of document types this user can read
     */
    String[] getReadableDocumentTypes();
    
    /**
     * Check if user can read a specific document type
     * @param documentType the type of document
     * @return true if user can read this type
     */
    default boolean canRead(String documentType) {
        String[] readableTypes = getReadableDocumentTypes();
        for (String type : readableTypes) {
            if (type.equalsIgnoreCase(documentType)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get reading history count
     * @return number of documents read
     */
    int getReadCount();
}