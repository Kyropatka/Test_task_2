package com.gmail.deniska1406sme;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc.
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    Map<String, Document> storage = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        String id = document.getId();

        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
            document.setId(id);
        }

        if (document.getCreated() == null) {
            document.setCreated(Instant.now());
        }

        storage.put(id, document);

        return storage.get(id);
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        return storage.values().stream()
                .filter(document -> matchesTitlePrefixes(document, request.getTitlePrefixes()))
                .filter(document -> matchesContents(document, request.getContainsContents()))
                .filter(document -> matchesAuthorIds(document, request.getAuthorIds()))
                .filter(document -> matchesCreatedFrom(document, request.getCreatedFrom()))
                .filter(document -> matchesCreatedTo(document, request.getCreatedTo()))
                .collect(Collectors.toList());
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {

        return Optional.ofNullable(storage.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }

    private boolean matchesTitlePrefixes(Document document, List<String> titlePrefixes) {
        if (titlePrefixes == null || titlePrefixes.isEmpty()) {
            return true;
        }
        String title = document.getTitle();
        return title != null && titlePrefixes.stream().anyMatch(title::startsWith);
    }

    private boolean matchesContents(Document document, List<String> containsContents) {
        if (containsContents == null || containsContents.isEmpty()) {
            return true;
        }
        String content = document.getContent();
        return content != null && containsContents.stream().anyMatch(content::contains);
    }

    private boolean matchesAuthorIds(Document document, List<String> authorIds) {
        if (authorIds == null || authorIds.isEmpty()) {
            return true;
        }
        return document.getAuthor() != null && authorIds.contains(document.getAuthor().getId());
    }

    private boolean matchesCreatedFrom(Document document, Instant createdFrom) {
        if (createdFrom == null) {
            return true;
        }
        return document.getCreated() != null && !document.getCreated().isBefore(createdFrom);
    }

    private boolean matchesCreatedTo(Document document, Instant createdTo) {
        if (createdTo == null) {
            return true;
        }
        return document.getCreated() != null && !document.getCreated().isAfter(createdTo);
    }
}