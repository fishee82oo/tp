package seedu.contact.ui;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.contact.model.person.Person;


/**
 * An UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label company;
    @FXML
    private Label email;
    @FXML
    private FlowPane tags;
    @FXML
    private ImageView phoneIcon;
    @FXML
    private ImageView emailIcon;
    @FXML
    private ImageView companyIcon;
    @FXML
    private ImageView phoneCopyIcon;
    @FXML
    private ImageView emailCopyIcon;


    /**
     * Creates a {@code PersonCode} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        // Only show company if it is non-empty
        // coverage:ignore-start
        if ("N/A".equals(person.getCompany().value)) {
            company.setManaged(false);
            company.setVisible(false);
        } else {
            company.setText(person.getCompany().value);
        }
        // coverage:ignore-end

        // Only show email if it is non-empty
        // coverage:ignore-start
        if ("unknown@example.com".equals(person.getEmail().value)) {
            email.setManaged(false);
            email.setVisible(false);
        } else {
            email.setText(person.getEmail().value);
        }
        // coverage:ignore-end
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));

        // Show Icons only if label are visible
        phoneIcon.visibleProperty().bind(phone.textProperty().isNotEqualTo("$phone"));
        emailIcon.visibleProperty().bind(email.textProperty().isNotEqualTo("$email"));
        //  Using company now and bind visibility to non-placeholder text
        companyIcon.visibleProperty().bind(company.textProperty().isNotEqualTo("$company"));

        // Right-side copy icons: visibility and handlers
        phoneCopyIcon.visibleProperty().bind(phone.textProperty().isNotEqualTo("$phone"));
        emailCopyIcon.visibleProperty().bind(email.visibleProperty());

        // Keep layout tight when email is hidden
        emailIcon.managedProperty().bind(email.visibleProperty());
        emailCopyIcon.managedProperty().bind(email.visibleProperty());
        configureCopyIcon(phoneCopyIcon, phone);
        configureCopyIcon(emailCopyIcon, email);
    }

    private void configureCopyIcon(ImageView icon, Label sourceLabel) {
        icon.setPickOnBounds(true);
        icon.setCursor(Cursor.HAND);
        Tooltip.install(icon, new Tooltip("Copy"));
        icon.setOnMouseClicked(event -> copyToClipboard(sourceLabel.getText()));
    }

    private void copyToClipboard(String value) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(value);
        clipboard.setContent(content);
    }
}
