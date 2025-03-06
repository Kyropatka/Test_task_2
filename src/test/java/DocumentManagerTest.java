import com.gmail.deniska1406sme.DocumentManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentManagerTest {

    private DocumentManager documentManager;

    @BeforeEach
    public void setUp() {
        documentManager = new DocumentManager();
    }

    @Test
    public void testSave_GeneratedIdAndCreated(){

        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("author1")
                .name("Denys Smel")
                .build();

        DocumentManager.Document document = DocumentManager.Document.builder()
                .id("")
                .title("Title")
                .content("Content")
                .author(author)
                .created(null)
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);

        assertNotNull(savedDocument.getId(), "id must be generated");
        assertFalse(savedDocument.getId().isEmpty(), "generated id must not be empty");
        assertNotNull(String.valueOf(savedDocument.getCreated()), "created must be generated");
    }

    @Test
    public void testSave_UsesExistingId(){

        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("author1")
                .name("Denys Smel")
                .build();

        String existingId = "doc-123";
        DocumentManager.Document document = DocumentManager.Document.builder()
                .id(existingId)
                .title("Title")
                .content("Content")
                .author(author)
                .created(Instant.now())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);

        assertEquals(existingId, savedDocument.getId(), "id must not be changed");
    }

    @Test
    public void testFindById(){

        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("author1")
                .name("Denys Smel")
                .build();

        DocumentManager.Document document = DocumentManager.Document.builder()
                .id("")
                .title("Test Title")
                .content("Test Content")
                .author(author)
                .created(null)
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);

        Optional<DocumentManager.Document> retrieved = documentManager.findById(savedDocument.getId());
        assertTrue(retrieved.isPresent(), "document must exist");
        assertEquals(savedDocument.getId(), retrieved.get().getId(), "id must match");

        Optional<DocumentManager.Document> notFound = documentManager.findById("non-existent");
        assertFalse(notFound.isPresent(), "document must not exist");
    }

    @Test
    public void testSearch_ByTitlePrefix(){

        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("author1")
                .name("Denys Smel")
                .build();

        DocumentManager.Document doc1 = DocumentManager.Document.builder()
                .id("1")
                .title("Test Document")
                .content("Some content")
                .author(author)
                .created(Instant.now())
                .build();

        DocumentManager.Document doc2 = DocumentManager.Document.builder()
                .id("2")
                .title("Another Document")
                .content("Different content")
                .author(author)
                .created(Instant.now())
                .build();

        documentManager.save(doc1);
        documentManager.save(doc2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Test"))
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);
        assertEquals(1, results.size(), "results size must be 1");
        assertEquals("Test Document", results.get(0).getTitle(), "title must match");
    }

    @Test
    public void testSearch_ByContent(){
        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("author1")
                .name("Denys Smel")
                .build();

        DocumentManager.Document doc1 = DocumentManager.Document.builder()
                .id("1")
                .title("Doc One")
                .content("This is a sample content with keyword")
                .author(author)
                .created(Instant.now())
                .build();

        DocumentManager.Document doc2 = DocumentManager.Document.builder()
                .id("2")
                .title("Doc Two")
                .content("Content without key")
                .author(author)
                .created(Instant.now())
                .build();

        documentManager.save(doc1);
        documentManager.save(doc2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .containsContents(List.of("keyword"))
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);
        assertEquals(1, results.size(), "results size must be 1");
        assertTrue(results.get(0).getContent().contains("keyword"));
    }

    @Test
    public void testSearch_ByAuthor(){
        DocumentManager.Author author1 = DocumentManager.Author.builder()
                .id("author1")
                .name("Denys Smel")
                .build();

        DocumentManager.Author author2 = DocumentManager.Author.builder()
                .id("author2")
                .name("Denys Prog")
                .build();

        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .id("1")
                .title("Doc One")
                .content("Content One")
                .author(author1)
                .created(Instant.now())
                .build();

        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .id("2")
                .title("Doc Two")
                .content("Content Two")
                .author(author2)
                .created(Instant.now())
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .authorIds(List.of("author1"))
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);
        assertEquals(1, results.size(), "results size must be 1");
        assertEquals("author1", results.get(0).getAuthor().getId(), "author1 must match");
    }

    @Test
    public void testSearch_ByCreated(){
        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("author1")
                .name("Denys Smel")
                .build();

        Instant now = Instant.now();
        Instant past = now.minusSeconds(3600);
        Instant future = now.plusSeconds(3600);

        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .id("1")
                .title("Doc One")
                .content("Content One")
                .author(author)
                .created(past)
                .build();

        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .id("2")
                .title("Doc Two")
                .content("Content Two")
                .author(author)
                .created(now)
                .build();

        DocumentManager.Document document3 = DocumentManager.Document.builder()
                .id("3")
                .title("Doc Three")
                .content("Content Three")
                .author(author)
                .created(future)
                .build();

        documentManager.save(document1);
        documentManager.save(document2);
        documentManager.save(document3);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .createdFrom(now)
                .createdTo(future)
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);
        assertEquals(2, results.size(), "results size must be 2");
    }

}
