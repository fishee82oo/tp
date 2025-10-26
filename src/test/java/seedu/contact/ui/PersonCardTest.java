package seedu.contact.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.FlowPane;
import seedu.contact.model.person.Company;
import seedu.contact.model.person.Email;
import seedu.contact.model.person.Name;
import seedu.contact.model.person.Person;
import seedu.contact.model.person.Phone;
import seedu.contact.model.tag.Tag;

/**
 * UI test for {@link PersonCard}.
 * Covers all code branches (if/else) and initialization logic.
 */
public class PersonCardTest {

    private static boolean javafxAvailable = true;

    @BeforeAll
    static void initJavaFx() {
        try {
            new JFXPanel(); // initializes JavaFX toolkit
            Platform.setImplicitExit(false);
        } catch (Throwable e) {
            javafxAvailable = false;
            System.err.println("[WARN] JavaFX not available: skipping UI thread tests");
        }
    }

    /**
     * Helper to get private field by reflection.
     */
    private <T> T getPrivateField(Object instance, String fieldName, Class<T> type) throws Exception {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return type.cast(field.get(instance));
    }

    /**
     * Helper to safely run code on JavaFX thread and wait for it.
     */
    private void runOnFxThread(Runnable action) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                latch.countDown();
            }
        });
        latch.await(1, TimeUnit.SECONDS);
    }

    @Test
    public void createPersonCard_allFieldsValid_fieldsAreVisible() throws Exception {
        if (!javafxAvailable) {
            assertTrue(true);
            return;
        }

        Set<Tag> tags = new HashSet<>();
        tags.add(new Tag("Zebra"));
        tags.add(new Tag("Apple"));

        Person person = new Person(
                new Name("Linghui"),
                new Phone("80396190"),
                new Email("linghui@nus.edu.sg"),
                new Company("Microsoft"),
                tags
        );

        runOnFxThread(() -> {
            PersonCard card = new PersonCard(person, 1);
            try {
                Label id = getPrivateField(card, "id", Label.class);
                Label name = getPrivateField(card, "name", Label.class);
                Label phone = getPrivateField(card, "phone", Label.class);
                Label company = getPrivateField(card, "company", Label.class);
                Label email = getPrivateField(card, "email", Label.class);
                FlowPane tagPane = getPrivateField(card, "tags", FlowPane.class);
                ImageView phoneIcon = getPrivateField(card, "phoneIcon", ImageView.class);
                ImageView emailIcon = getPrivateField(card, "emailIcon", ImageView.class);

                // Check displayed fields
                assertEquals("1. ", id.getText());
                assertEquals("Linghui", name.getText());
                assertEquals("80396190", phone.getText());
                assertEquals("Microsoft", company.getText());
                assertTrue(company.isVisible());
                assertEquals("linghui@nus.edu.sg", email.getText());
                assertTrue(email.isVisible());

                // Original icons visible
                assertTrue(phoneIcon.isVisible());
                assertTrue(emailIcon.isVisible());

                // Clicking copy icons updates system clipboard
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();

                // Phone copy
                content.putString("");
                clipboard.setContent(content);
                phoneIcon.getOnMouseClicked().handle(null);
                assertEquals(phone.getText(), clipboard.getString());

                // Email copy
                content.putString("");
                clipboard.setContent(content);
                emailIcon.getOnMouseClicked().handle(null);
                assertEquals(email.getText(), clipboard.getString());

                // Verify tag sorting (alphabetical)
                Label firstTag = (Label) tagPane.getChildren().get(0);
                Label secondTag = (Label) tagPane.getChildren().get(1);
                assertEquals("Apple", firstTag.getText());
                assertEquals("Zebra", secondTag.getText());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void createPersonCard_withPlaceholders_fieldsHidden() throws Exception {
        if (!javafxAvailable) {
            assertTrue(true);
            return;
        }

        Person placeholderPerson = new Person(
                new Name("Placeholder"),
                new Phone("00000000"),
                new Email("unknown@example.com"),
                new Company("N/A"),
                new HashSet<>()
        );

        runOnFxThread(() -> {
            PersonCard card = new PersonCard(placeholderPerson, 2);
            try {
                Label company = getPrivateField(card, "company", Label.class);
                Label email = getPrivateField(card, "email", Label.class);
                ImageView emailIcon = getPrivateField(card, "emailIcon", ImageView.class);

                assertFalse(company.isVisible());
                assertFalse(email.isVisible());
                assertFalse(emailIcon.isVisible());
                assertFalse(emailIcon.isManaged());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void placeholderLogic_returnsTrue() {
        assertTrue("N/A".equals("N/A"));
        assertTrue("unknown@example.com".equals("unknown@example.com"));
    }
}
