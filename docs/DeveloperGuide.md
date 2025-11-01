---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# FastCard Developer Guide

<!-- * Table of Contents -->
## **Table of Contents**

- [Acknowledgements](#acknowledgements)
- [Setting Up, Getting Started](#setting-up-getting-started)
- [Design](#design)
    - [Architecture](#architecture)
    - [UI Component](#ui-component)
    - [Logic Component](#logic-component)
    - [Model Component](#model-component)
    - [Storage Component](#storage-component)
    - [Common Classes](#common-classes)
- [Implementation](#implementation)
    - [Sort Feature](#sort-feature)
    - [Filter Feature](#filter-feature)
    - [Command Recall Feature](#command-recall-feature)
    - [Delete Feature](#delete-feature)
    - [Edit Feature](#edit-feature)
    - [Add Feature (with Basic Information)](#add-contact-with-basic-information)
    - [Add Feature (with Detailed Information)](#add-contact-with-full-information)
- [Documentation, Logging, Testing, Configuration, Devops](#documentation-logging-testing-configuration-dev-ops)
- [Appendix: Requirements](#appendix-requirements)
    - [Product Scope](#product-scope)
    - [User Stories](#user-stories)
    - [Use Cases](#use-cases)
    - [Non-Functional Requirements](#non-functional-requirements)
    - [Glossary](#glossary)
- [Appendix: Instructions for manual testing](#appendix-instructions-for-manual-testing)

<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* This project is largely based on and a fork of the original [AB3 codebase](https://github.com/nus-cs2103-AY2526S1/tp)

* This documentation page was generated using [MarkBind](https://markbind.org/)

* Libraries used: [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson), [JUnit5](https://github.com/junit-team/junit5)

--------------------------------------------------------------------------------------------------------------------

## **Setting Up, Getting Started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2526S1-CS2103T-F11-4/tp/blob/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2526S1-CS2103T-F11-4/tp/blob/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete Alice Pauline`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point).

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI Component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2526S1-CS2103T-F11-4/tp/blob/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonDetailPanel`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2526S1-CS2103T-F11-4/tp/blob/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2526S1-CS2103T-F11-4/tp/blob/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic Component

**API** : [`Logic.java`](https://github.com/AY2526S1-CS2103T-F11-4/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete Alice Pauline")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete Alice Pauline` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model Component
**API** : [`Model.java`](https://github.com/AY2526S1-CS2103T-F11-4/tp/blob/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the company book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' (e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list changes).
* stores the currently 'focused' `Person` objects (e.g., target of view detail command) as a separate _focused_ person which is exposed to outsiders as an unmodifiable `SimpleObjectProperty<Person>` that can be 'observed' (e.g. the UI can be bound to this object so that the UI automatically updates when the focus person changes).
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* stores a `CommandHistory` object that keeps track of the commands a user inputs during runtime. This is exposed to the outside as a `ReadOnlyCommandHistory` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage Component

**API** : [`Storage.java`](https://github.com/AY2526S1-CS2103T-F11-4/tp/blob/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both company book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common Classes

Classes used by multiple components are in the `seedu.company.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.


### Sort Feature

Sorting is facilitated by `SortCommand` and `SortCommandParser`, following these steps:

1. **User input parsing**: `SortCommandParser#parse()` tokenizes the user input into an `ArgumentMultimap` containing the field and order values, then validates the tokens and creates a `SortCommand` object with the parsed parameters.

2. **Comparator Creation**: `SortCommand#execute()` calls `SortCommand#getComparator()` which creates the appropriate `Comparator<Person>` based on the specified field (name/tag) and order (ascending/descending).

3. **Model update**: `SortCommand` invokes `Model#sortPersons(comparator)` to trigger the sorting operation.

4. **Sorting execution**: The sort operation cascades through:
    * `Model#sortPersons(comparator)` &rarr; `AddressBook#sortPersons(comparator)` &rarr; `UniquePersonList#sortPersons(comparator)`

5. **Result**: A `CommandResult` is returned to display the success message to the user.

The sequence diagram below shows how the sort operation works:
<puml src="diagrams/SortSequenceDiagram.puml" width="100%" />

### Filter Feature
The filtering mechanism is facilitated by `FilterCommand` and `FilterCommandParser`, following these steps:

1. **User input parsing**: `FilterCommandParser#parse()` tokenizes the user input into an `ArgumentMultimap` containing tag values, then validates the tokens through `FilterCommandParser#checkValidTokens()`. The parser converts all tags to lowercase, removes duplicates, and creates a `TagsContainTagPredicate` object with the processed tags. A `FilterCommand` object is then instantiated with this predicate.

2. **Validation checks**: The parser performs several validations:
    * Ensures that at least one `PREFIX_TAG` token exists
    * Verifies no input exists between "filter" and the first prefix
    * Confirms no empty tags are present
    * Validates all tag names are alphanumeric and at most 30 characters
<br>
3. **Model update**: `FilterCommand#execute()` invokes `Model#updateFilteredPersonList(predicate)` to apply the filtering operation.

4. **Filtering execution**: The model updates its filtered contact list by applying the `TagsContainTagPredicate`, which matches any contact whose tags contain at least one of the specified tags (case-insensitive matching).

5. **Result**: A `CommandResult` is returned to display the success message to the user. The success message contains the number of contacts found that match the filter criteria.

The sequence diagram below shows how the filter operation works:
<puml src="diagrams/FilterSequenceDiagram.puml" width="100%" />

The activity diagram below depicts the execution flow of the filter command:
<puml src="diagrams/FilterActivityDiagram.puml" width="100%" />

### Delete Feature
Deleting contacts is facilitated by `DeleteCommand` and `DeleteCommandParser`, following these steps:

1. **User input parsing**: `DeleteCommandParser#parse()` trims the user input and first attempts to interpret it as an index
   via `ParserUtil.parseIndex()`. If parsing the index fails, it falls back to `ParserUtil.parseName()` to treat the argument as
   a name, throwing a `ParseException` if both attempts fail.

2. **Target resolution**: The resulting `DeleteCommand` records whether it will operate on an index or a name and, during
   execution, routes to the corresponding helper (`executeDeleteByIndex` or `executeDeleteByName`).

3. **Model lookup**: For index-based deletes, the command retrieves the target from `Model#getFilteredPersonList()` and ensures
   the index is within bounds. For name-based deletes, it scans `Model#getAddressBook().getPersonList()` for case-insensitive
   matches to the provided name.

4. **Disambiguation**: If multiple contacts share the same name, the command updates the filtered list through
   `Model#updateFilteredPersonList(...)` so the UI displays only the matching entries, then prompts the user to delete the
   intended contact by index.

5. **Deletion and feedback**: Once a single target is identified, `Model#deletePerson(person)` removes it from storage, and the
   command returns a `CommandResult` summarizing the deleted contact.

The sequence diagram below shows how the filter operation works:
<puml src="diagrams/DeleteSequenceDiagram.puml" width="100%" />

The activity diagram below depicts the execution flow of the filter command:
<puml src="diagrams/DeleteActivityDiagram.puml" width="100%" />

### Edit Feature
Editing contacts is facilitated by `EditCommand` and `EditCommandParser`, following these steps:

1. **User input parsing**: `EditCommandParser#parse()` tokenizes the user input into an `ArgumentMultimap` containing the target identifier (index or name) and field values to update. The parser first attempts to interpret the preamble as an index via `ParserUtil.parseIndex()`. If parsing fails, it treats the preamble as a name reference.

2. **Validation checks**: The parser performs several validations:
   * Ensures that at least one field to edit is provided
   * Verifies no duplicate prefixes for singular fields (name, phone, email, company, detail)
   * Checks that `PREFIX_TAG` (overwrite) is not used together with `PREFIX_TAG_ADD` or `PREFIX_TAG_DELETE`
   * Validates that tags to add or delete are not empty

3. **Target resolution**: During execution, `EditCommand#execute()` determines whether to operate on an index or a name:
   * For index-based edits, the command retrieves the target from `Model#getFilteredPersonList()` and ensures the index is within bounds.
   * For name-based edits, it normalizes the name (case-insensitive, multiple spaces collapsed) and scans the filtered list for matches.

4. **Disambiguation**: If multiple contacts share the same name, the command updates the filtered list through `Model#updateFilteredPersonList(...)` to display only matching entries, then prompts the user to edit by index instead.

5. **Tag handling**: The edit command supports three tag modification modes:
   * **Overwrite mode** (`t/`): Replaces all existing tags with the specified tags
   * **Addition mode** (`t+/`): Adds specified tags to existing tags
   * **Deletion mode** (`t-/`): Removes specified tags from existing tags (validates that tags exist before deletion)

6. **Person creation and validation**: The command creates an edited person through `createEditedPerson()`, which applies all field updates including tag modifications. It then validates:
   * The edited person is not a duplicate of another existing contact
   * The edited email (if changed) is not used by another contact
   * All tags to be deleted actually exist on the person

7. **Model update and feedback**: Once validation passes, `Model#setPerson(personToEdit, editedPerson)` replaces the original person with the edited version. The filtered list is updated to show only the edited contact, and a `CommandResult` is returned with the success message displaying the updated contact details.

The sequence diagram below shows how the edit operation works:
<puml src="diagrams/EditSequenceDiagram.puml" width="100%" />

### Command Recall Feature
The repeat mechanism is facilitated by `CommandHistory` Model and `LogicManager`.

The Sequence diagram for a `getPreviousCommand` Operation:
Sequence is similar for `getNextCommand` but in reverse.

1. **Key Pressed**: `CommandBox` handles the event with a call to `navigateCommandHistory(x)` with `x` being '-1' for previous command, and `1' for next command in history.

2. **Command Bubbles Down**: A series of function calls are then passed through the Logic Component and the Model Component

3. **Get Last Nth Command**: `CommandHistory` gets the last nth command based on its current value of `currCommandIndex` and returns the respective string value of the command saved in history. Subsequently, `currCommandIndex` is incremented till `n` (where `n` is the current size of the history list)

4. **Update CommandBox**: Target Command String gets returned back and `CommandBox` updates its text field to show the command, along with setting the cursor to end of line.

<puml src="diagrams/CommandHistoryDiagram.puml" width="100%" />


### Add Contact with Basic Information
The add operation is facilitated by the `AddCommandBasic` class within the Logic and Model components.

The Sequence diagram for an **Add Basic Command** operation is shown below.

1. **User Input**: The user enters the command `addbasic n/NAME p/PHONE` in the `CommandBox` (e.g., `addbasic n/Jadon p/88880000`).

2. **Command Parsing and Execution**:  
   The input is passed from the `CommandBox` to the `MainWindow`, which then invokes the `LogicManager` to execute the command.  
   `LogicManager` constructs an `AddCommandBasic` object with a `Person` instance containing the specified name and phone number.

3. **Model Update**:  
   The `AddCommandBasic` checks through the `ModelManager` to verify if the contact already exists.  
   If not found, the new contact is added to the model and stored in memory.

4. **Success Message**:  
   Upon successful addition, `Messages.format()` is called to format the result, and a `CommandResult` is returned to `LogicManager`.  
   The `MainWindow` then updates the UI to display the success message in the `CommandBox`.

<puml src="diagrams/AddCommandBasicDiagram.puml" width="100%" />
<puml src="diagrams/AddCommandBasicActivityDiagram.puml" width="100%" />


### Add Contact with Full Information

The **add** operation is facilitated by the `AddCommand` class within the **Logic** and **Model** components.

1. **User Input**
   The user enters the command:

   ```
   add n/NAME p/PHONE e/EMAIL c/COMPANY [t/TAG]...
   ```

   Example:

   ```
   add n/Alice p/88889999 e/alice@example.com c/NUS t/friend
   ```

2. **Command Parsing and Execution**
   The command string is passed from the `CommandBox` to the `MainWindow`, which forwards it to the `LogicManager` for execution.
   The `LogicManager` then constructs an `AddCommand` object with a `Person` instance containing the specified details (`name`, `phone`, `email`, `company`, and optional `tags`).

3. **Validation and Model Update**
   The `AddCommand` performs a comprehensive validation process:

    * Checks whether the **command format** is valid and any **required fields** (`n/`, `p/`, `e/`, `c/`) are missing.
    * Checks whether **field values** are valid (e.g., name and company character rules, valid email syntax, phone number format, valid tags).
    * Checks whether a **duplicate person** (same name and phone) already exists.
    * Checks whether the **email** already exists in another contact.

   If a validation error or duplicate is detected, the corresponding error message is returned to the UI.
   Otherwise, the new person is added to the `ModelManager`, updating the in-memory address book.

4. **Storage Update**
   The `LogicManager` calls `saveAddressBook(addressBook)` in the **Storage** component, which serializes and writes the updated address book data to the local storage file.

5. **Result Display**
   Upon successful addition, a `CommandResult` is created with a formatted success message (via `Messages.format()`),
   and returned through the `LogicManager` back to the `MainWindow`, which updates the UI to display the message.
   
<puml src="diagrams/AddCommandSequenceDiagram.puml" width="100%" />
<puml src="diagrams/AddCommandActivityDiagram.puml" width="100%" />



## **Documentation, Logging, Testing, Configuration, Dev-Ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product Scope

**Target user profile**:

* Is a sales and procurement professional
* Regularly interacts with clients or suppliers
* Has a need to manage a significant number of clients efficiently
* Prefers desktop apps over other forms of software
* Can type fast
* Prefers typing to mouse interactions
* Is reasonably comfortable using CLI apps

**Value proposition**: Enable sales and procurement professionals engaged in regular business communication to efficiently capture, organize, and recall contact details in real time, minimizing information loss and improving follow-up accuracy.


### User Stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                          | I want to …​                                                                | So that I can…​                                                             |
|----------|----------------------------------|-----------------------------------------------------------------------------|-----------------------------------------------------------------------------|
| `* * *`  | new user                         | see a user guide                                                            | can learn how to use the application efficiently                            |
| `* * *`  | sales / procurement professional | add a contact with just a name and phone number                             | save time during meetings and fill in the details later when convenient     |
| `* * *`  | sales / procurement professional | record multiple contact methods for a contact such as phone number or email | have multiple channels for communication                                    |
| `* * *`  | sales / procurement professional | assign tags to my contacts                                                  | easily categorize different types of clients                                |
| `* * *`  | sales / procurement professional | delete a contact in my contact list                                         | remove contacts that are no longer prospects                                |
| `* * *`  | sales / procurement professional | list all contacts in my contact list                                        | verify that all my important contacts have been added                       |
| `* * *`  | sales / procurement professional | sort my contacts based on a field (e.g name, tags)                          | view relevant clients easily or rank clients easily                         |
| `* * *`  | sales / procurement professional | filter my contacts by tags                                                  | quickly find relevant clients and manage my outreach more efficiently       |
| `* * *`  | sales / procurement professional | access the application offline without internet                             | view and manage my contacts even without internet connectivity              |
| `* * *`  | forgetful user                   | search for contacts with part of their name                                 | find a specific contact even if I only remember part of their name          |
| `* *`    | lazy user                        | edit an existing contact                                                    | update information for a contact and not have to remove and add the contact |
| `* *`    | efficient user                   | navigate through my past commands                                           | save time when adding multiple contacts with similar information            |
| `* *`    | sales / procurement professional | edit a specific contact's information                                       | keep the information of my contacts up to date                              |
| `* *`    | sales / procurement professional | add or edit notes for existing contacts                                     | keep track of important information from conversations and meetings         |
| `* *`    | sales / procurement professional | view the full details and notes of a contact                                | quickly recall context before calling or meeting them                       |
| `* *`    | sales / procurement professional | export my contacts to a CSV file                                            | share contact information with colleagues or import into other applications |
| `* *`    | organized user | backup my contacts to a file                                                | keep a local copy for safekeeping or migration purposes                     |
| `* *`    | CLI-oriented user | exit the application via a command                                          | exit the application without using my mouse                                 |
| `* *`    | careless user     | have a safeguard when clearing my contacts                                  | prevent accidental deletion of all my contacts                              |

### Use Cases

(For all use cases below, the **System** is `FastCard` and the **Actor** is the `user`, unless specified otherwise)

**Use case: UC01 - Delete a contact**

**MSS**

1.  User requests to delete a contact by name.
2.  System finds the contact with that name.
3.  System deletes the contact.

    Use case ends.

**Extensions**

* 2a. More than one contact is found with that specific name.

    * 2a1. System lists the matching contacts.
    * 2a2. User requests to delete one of the listed contacts by index.
    * 2a3. System deletes the contact.

      Use case ends.

* 2b. No contact has the provided name.

    * 2b1. System shows an error message.

      Use case resumes at step 1.

* 2c. The provided index does not exist in the displayed list.

    * 2c1. System shows an error message.

      Use case resumes at step 1.



**Use case: UC02 - Add a contact with basic information**

**MSS**

1.  User requests to add a contact with name and phone number
2.  System creates an entry of the above contact

    Use case ends.

**Extensions**

* 1a. The given number is invalid.

    * 1a1. System shows an error message.

      Use case resumes at step 1.


* 1b. The given number already exists.

   *  1b1. System shows an error message.

      Use case resumes at step 1.


**Use case: UC03 - Edit a contact's particulars**

**MSS**

1.  User requests to edit a contact's information by providing an index or name, along with the fields to update.
2.  System finds the contact in the displayed list.
3.  System validates the new information.
4.  System updates the contact's particulars with the new information.

    Use case ends.

**Extensions**

* 1a. User provides an invalid index (non-numeric, zero, negative, or exceeds list size).

    * 1a1. System shows an error message.

      Use case resumes at step 1.

* 1b. User does not provide any fields to edit.

    * 1b1. System shows an error message.

      Use case resumes at step 1.

* 2a. The provided name does not match any contact in the displayed list.

    * 2a1. System shows an error message.

      Use case resumes at step 1.

* 2b. Multiple contacts match the provided name.

    * 2b1. System filters and displays the matching contacts.
    * 2b2. System shows an error message asking user to use index instead.

      Use case resumes at step 1.

* 3a. The new information is invalid (e.g., invalid phone number format, invalid email format).

    * 3a1. System shows an error message with validation details.

      Use case resumes at step 1.

* 3b. The new information would create a duplicate contact.

    * 3b1. System shows an error message.

      Use case resumes at step 1.

* 3c. The new email already exists in another contact.

    * 3c1. System shows an error message.

      Use case resumes at step 1.

* 3d. User attempts to delete tags that don't exist on the contact.

    * 3d1. System shows an error message listing the non-existent tags.

      Use case resumes at step 1.

**Use case: UC04 - Sort contacts**

**MSS**
1.  User requests to sort contacts by a specific field and order.
2.  System validates the field and order parameters
3.  System sorts the contacts according to the specified criteria
4.  System displays the sorted contact list with a success message

    Use case ends.

**Extensions**
   
  * 1a. User does not provide both field and order parameters.

    * 1a1. System shows an error message.

      Use case resumes at step 1.

  * 1b. User provides input between "sort" and the first prefix.

    * 1b1. System shows an error message.

      Use case resumes at step 1.

  * 1c. User provides duplicate prefixes.

    * 1c1. System shows an error message.

      Use case resumes at step 1.

  * 2a. The provided field is invalid (not "name" or "tag").

    * 2a1. System shows an error message.

      Use case resumes at step 1.

  * 2b. The provided order is invalid (not "asc", "desc", "ascending", or "descending").

    * 2b1. System shows an error message.

      Use case resumes at step 1.

**Use case UC05 - Filter contacts by tags**

**MSS**
1.  User requests to filter contacts by one or more tags
2.  System validates the tag parameters.
3.  System filters the contacts to show only those containing any of the specified tags.
4.  System displays the filtered contact list with the count of matching contacts.

    Use case ends.

**Extensions**

  * 1a. User does not provide any tag parameters.

    * 1a1. System shows an error message.

      Use case resumes at step 1.

  * 1b. User provides input between "filter" and the first tag prefix.

    * 1b1. System shows an error message.

      Use case resumes at step 1.

  * 2a. One or more tags are empty.

    * 2a1. System shows an error message.

      Use case resumes at step 1.

  * 2b. One or more tags contain non-alphanumeric characters

    * 2b1. System shows an error message.

      Use case resumes at step 1.

  * 3a. No contacts match the specified tags.

    * 3a1. System displays an empty list with a count of 0 contact.

      Use case ends.

**Use case: UC06 - List contacts**

**MSS**
1. User enters the `list` command.
2. System displays all the contacts stored.

   Use case ends.

**Use case: UC07 - Get help**

**MSS**
1. User enters the `help` command.
2. System displays a pop-up window containing a link to the user guide.

   Use case ends.

**Use case: UC08 - View contact details**

**MSS**
1. User requests to view detailed information of a contact by providing an index.
2. System displays the detail panel showing full contact information including notes for the contact at the specified index.

   Use case ends.

**Extensions**

* 1a. User provides an invalid index (non-numeric, zero, negative, or exceeds list size).
    * 1a1. System shows an error message.
    
      Use case resumes at step 1.

* 1b. User provides more than one argument.
    * 1b1. System shows an error message with usage instructions.
    
      Use case resumes at step 1.

* 1c. User enters `view` without any parameters.
    * 1c1. System toggles the detail panel visibility.
    
      Use case ends.

**Use case: UC09 - Export contacts to CSV file**

**MSS**
1. User requests to export all contacts to a CSV file by providing a filename.
2. System validates the filename.
3. System creates a CSV file on the user's Desktop with the specified filename.
4. System writes all contact information (name, phone, email, company, tags) to the CSV file.
5. System displays a success message with the filename.

   Use case ends.

**Extensions**

* 1a. User does not provide a filename prefix.
    * 1a1. System shows an error message indicating filename is required.
    
      Use case resumes at step 1.

* 1b. User provides duplicate filename prefixes.
    * 1b1. System shows an error message indicating only one filename is allowed.
    
      Use case resumes at step 1.

* 2a. The filename is empty or contains only whitespace.
    * 2a1. System shows an error message.
    
      Use case resumes at step 1.

* 2b. The filename contains invalid characters (path separators, special characters).
    * 2b1. System shows an error message listing allowed characters.
    
      Use case resumes at step 1.

* 4a. System fails to write to the Desktop directory (e.g., permission issues).
    * 4a1. System shows an error message with failure details.
    
      Use case ends.

**Use case: UC10 - Recall previous nth command but overshoots**

**MSS**
1. User starts a new session of FastCard.
2. User enters a series of `k` valid commands (e.g. `add`).
3. User inputs keystroke mapped to 'recall previous Command' function `n` times.
4. System fills in the `(k - n)`th valid command stored in history.
5. User inputs keystroke mapped to 'recall next Command' function `i` times.
6. System fills in the `(k - n + i)`th valid command stored in history.
7. User continue normal usage.

   Use case ends.

**Extensions**
* 1a. User does not provide a valid command.
    * 1a1. System shows an error message indicating filename is required.
    * 1a2. System does not save that command to history
  
      Use case resumes at step 2.
  
* 2a. Number keystrokes `n` > `k` previous valid commands.
    * 2a1. System shows an error message indicating end of command history reached.
    * 2a2. System empties the command input field
  
      Use case resumes at step 5.

* 5a. Number of recall forward keystrokes `i` > `n` recall previous keystrokes.
    * 5a1. System empties the command input field
    * 5a2. subsequent forward keystroke(s) does nothing
  
      Use case resumes at step 7.



### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2.  Should be able to hold up to 1000 contacts without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  Should work offline without a need for an external database server.
5.  Data should be stored in a human editable text file to allow advanced users to manipulate the data directly through the data file.
6.  Should be portable without the need for an installer.
7.  System must ensure that long term data is not lost unexpectedly, even in cases of abrupt shutdowns or crashes
8.  Automated Testing should cover all core functionality implementations
9.  Error messages should be prominent and usually provide specific information regarding the issue

### Glossary

* **Actor**: The user interacting with FastCard in use cases.
* **CLI (Command Line Interface)**: Text-based interaction via the command input box.
* **Command**: A text instruction entered by the user in the command box (e.g., `add`, `edit`, `find`, `list`, `delete`, `clear`, `help`, `exit`).
* **Command box**: The text input field where users type commands.
* **CommandResult**: The outcome of executing a command, including the feedback message shown to the user.
* **Contact (Person)**: An entity representing a business contact with name, phone, email, company, and tags.
* **Contact book**: The collection of contacts managed by FastCard.
* **Data file**: The JSON file at `data/fastcard.json` storing contacts and tags.
* **Detail Pane**: A Split Pane on the main content area, toggled via the `view` command.
* **Duplicate contact**: A contact that conflicts with an existing one based on identity fields (e.g., same phone number).
* **FastCard**: The product name of this application.
* **Filtered list**: The subset of contacts currently matching a search or filter, shown in the UI and backed by the model's observable list.
* **GUI (Graphical User Interface)**: The JavaFX-based visual interface (e.g., `MainWindow`, `PersonListPanel`).
* **Help window**: A separate window displaying usage instructions, opened via the `help` command.
* **Home folder**: The directory where the FastCard `.jar` resides; used as the base for `data/` and `preferences.json`.
* **Index**: A 1-based position of a contact within the currently displayed list.
* **JSON**: Data format used for persistence (e.g., `data/fastcard.json`).
* **Logic**: Parses commands and executes them against the `Model`, returning a `CommandResult`.
* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Model**: Holds in-memory application data and preferences; exposes a filtered, observable list of contacts.
* **MSS (Main Success Scenario)**: The primary, exception-free flow of a use case.
* **NFR (Non-Functional Requirement)**: A quality constraint on the system (e.g., performance, portability).
* **ObservableList**: A JavaFX list implementation that notifies the UI of changes.
* **Parser / XYZCommandParser**: Classes that convert user input into executable command objects.
* **Person card**: The UI element representing a single contact in the list.
* **Prefix**: The short marker preceding a field in a command (e.g., `n/`, `p/`, `e/`, `a/`, `/`, `t/`).
* **Primary identifier**: The field(s) used to check contact identity (e.g., phone number) to prevent duplicates.
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **Result display**: The UI area showing the outcome messages of executed commands.
* **Sample data**: Default contacts provided on first launch to demonstrate core features.
* **Status bar**: The UI footer indicating summaries such as list counts and last update time.
* **Storage**: Persists and retrieves data (company book and user prefs) from JSON files.
* **System**: The application under discussion in use cases (i.e., FastCard).
* **Tag**: A short label attached to a contact for categorization (e.g., client, supplier, industry).
* **Undo/Redo**: Feature that reverts or reapplies recent changes to the company book using stored history.
* **UniquePersonList**: An internal list that enforces uniqueness for contacts.
* **User preferences**: Settings such as window size and file paths (read-only view exposed as `ReadOnlyUserPref`).


--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for Manual Testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and Shutdown

1. Initial launch

   -   Download the jar file and copy into an empty folder

   -   Double-click the jar file <br>
       Expected: Shows the GUI with a set of sample candidates. The window size may not be optimum.

2. Saving window preferences

   -   Resize the window to an optimum size. Move the window to a different location. Close the window.

   -   Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

### Listing all Contacts
1.  List all contacts in FastCard.

   -   Prerequisite: At least one contact exists in FastCard.

   -   Test case: `list`<br>
       Expected: All contacts are displayed.

### Deleting a Contact

1.  Deleting a contact while all contacts are being shown

    -  Prerequisites: List all contacts using the `list` command. Multiple contacts in the list.

    -  Test case: `delete Alice Pauline`<br>
       Expected: The contact named `Alice Pauline` is deleted when she is the only contact with that name. Details of the deleted contact shown in the status message.

    -  Test case: `delete 1`<br>
       Expected: First contact in the currently displayed list is deleted. Details of the deleted contact shown in the status message. 

    -  Test case: `delete 0`<br>
       Expected: No contact is deleted. Error details shown in the status message. Status bar remains the same.

    -  Test case: `delete Jadon Ye` when multiple Jadons exist<br>
       Expected: No contact is deleted. FastCard lists the matching contacts so that the user can delete the intended one by index.

    -  Other incorrect delete commands to try: `delete`, `delete x`, `delete Unknown Person`, `...` (where x is larger than the list size)<br>      Expected: Similar to previous.

### Editing a Contact

1. Editing a contact by index

    -  Prerequisites: List all contacts using the `list` command. Multiple contacts in the list.

    -  Test case: `edit 1 p/98765432`<br>
       Expected: Phone number of the first contact is updated to "98765432". Success message shows the edited contact details.

    -  Test case: `edit 1 e/newemail@example.com c/Google`<br>
       Expected: Email and company of the first contact are updated. Success message shows the edited contact details.

    -  Test case: `edit 1 n/John Tan p/91234567 e/john@example.com c/Microsoft d/Senior Manager t/client t/vip`<br>
       Expected: Multiple fields are updated simultaneously. All tags are replaced with "client" and "vip".

    -  Test case: `edit 0 p/98765432`<br>
       Expected: No contact is edited. Error message shows invalid index.

    -  Test case: `edit 999 p/98765432` (where 999 exceeds list size)<br>
       Expected: No contact is edited. Error message shows invalid index.

2. Editing a contact by name

    -  Prerequisites: At least one contact named "Alice Pauline" exists in the displayed list. No duplicates of this name exist.

    -  Test case: `edit Alice Pauline p/87654321`<br>
       Expected: Phone number of Alice Pauline is updated to "87654321". Success message shows the edited contact details.

    -  Test case: `edit Alice Pauline e/alice.new@example.com d/Marketing Director`<br>
       Expected: Email and detail of Alice Pauline are updated.

    -  Test case: `edit John Doe p/91234567` when multiple "John Doe" contacts exist<br>
       Expected: No contact is edited. The filtered list updates to show all matching "John Doe" contacts. Error message asks user to edit by index instead.

    -  Test case: `edit Nonexistent Person p/91234567`<br>
       Expected: No contact is edited. Error message shows that the person name does not match any displayed contact.

3. Tag operations

    -  Prerequisites: Contact at index 1 exists with tags "friend" and "colleague".

    -  Test case: `edit 1 t/client t/vip`<br>
       Expected: All existing tags are replaced with "client" and "vip". Success message shows the edited contact with new tags.

    -  Test case: `edit 1 t+/important`<br>
       Expected: Tag "important" is added to existing tags. Contact now has tags "friend", "colleague", and "important".

    -  Test case: `edit 1 t-/colleague`<br>
       Expected: Tag "colleague" is removed. Contact now has only "friend" tag remaining.

    -  Test case: `edit 1 t+/client t-/friend`<br>
       Expected: Tag "client" is added and "friend" is removed. Contact has "colleague" and "client" tags.

    -  Test case: `edit 1 t/`<br>
       Expected: All tags are removed from the contact. Success message shows contact with no tags.

4. Invalid edit commands

    -  Test case: `edit 1`<br>
       Expected: No contact is edited. Error message shows that at least one field to edit must be provided.

    -  Test case: `edit`<br>
       Expected: No contact is edited. Error message shows invalid command format with usage instructions.

    -  Test case: `edit 1 e/invalidemail`<br>
       Expected: No contact is edited. Error message shows invalid email format.

    -  Test case: `edit 1 t/client t+/vip`<br>
       Expected: No contact is edited. Error message shows that `t/` cannot be used together with `t+/` or `t-/`.

    -  Test case: `edit 1 t-/nonexistent`<br>
       Expected: No contact is edited. Error message shows that the tag "nonexistent" does not exist on this person and cannot be deleted.

    -  Test case: `edit 1 t+/`<br>
       Expected: No contact is edited. Error message shows that tags to add cannot be empty.

5. Duplicate detection

    -  Prerequisites: A contact "Alice Pauline" with phone "91234567" exists. Another contact "Bob" with email "bob@example.com" exists.

    -  Test case: `edit 2 n/Alice Pauline p/91234567`<br>
       Expected: No contact is edited. Error message shows that this person already exists in the contact book.

    -  Test case: `edit 1 e/bob@example.com`<br>
       Expected: No contact is edited. Error message shows that this email already exists in the contact book.

### Sorting Contacts

1.  Sorting contacts by name

    -  Prerequisites: Multiple contacts in the company book with different names.

    -  Test case: `sort f/name o/asc`<br>
       Expected: Contacts are sorted alphabetically by name in ascending order. Success message shows "Sorted all contact(s) by name in ascending order".

    -  Test case: `sort f/name o/desc`<br>
       Expected: Contacts are sorted alphabetically by name in descending order. Success message shows "Sorted all contact(s) by name in descending order".

    -  Test case: `sort f/name o/ascending`<br>
       Expected: Same as `sort f/name o/asc`.

    -  Test case: `sort f/name o/descending`<br>
       Expected: Same as `sort f/name o/desc`.

2. Sorting contacts by tag

    -  Prerequisites: Multiple contacts in the company book with different tags.

    -  Test case: `sort f/tag o/asc`<br>
       Expected: Contacts are sorted by their first tag alphabetically (case-insensitive) in ascending order. Contacts without tags appear first.

    -  Test case: `sort f/tag o/desc`<br>
       Expected: Contacts are sorted by their first tag alphabetically in descending order.

3. Invalid sort commands

    -  Test case: `sort f/name`<br>
       Expected: No sorting occurs. Error message shows invalid command format.

    -  Test case: `sort f/phone o/asc`<br>
       Expected: No sorting occurs. Error message shows invalid command format.

    -  Test case: `sort f/name o/random`<br>
       Expected: No sorting occurs. Error message shows invalid command format.

    -  Test case: `sort f/name o/asc f/tag`<br>
       Expected: No sorting occurs. Error message shows duplicate prefixes are not allowed.

    -  Test case: `sort random f/name o/asc`<br>
       Expected: No sorting occurs. Error message shows invalid command format.

### Filtering Contacts by Tags
1. Filtering contacts with single tag
   
    -  Prerequisites: Multiple contacts in the company book, some with the tag "friends", some without.

    -  Test case: `filter t/friends`<br>
       Expected: Only contacts with the "friends" tag are displayed. Status message shows the number of contact(s) listed (e.g., "3 contact(s) listed!").

    -  Test case: `filter t/FRIENDS`<br>
       Expected: Same as above. Tag matching is case-insensitive.

    -  Test case: `filter t/colleagues`<br>
       Expected: Only contacts with the "colleague" tag are displayed.
    
2. Filtering contacts with multiple tags

    -  Prerequisites: Multiple contacts with various tags (e.g., "friends", "family", "colleagues").

    -  Test case: `filter t/friends t/family`<br>
       Expected: Contacts with either "friends" OR "family" tags are displayed. Shows count of all matching contacts.

    -  Test case: `filter t/friends t/friends t/family`<br>
       Expected: Same as above. Duplicate tags are automatically removed.

3. Filtering with no matches
  
    -  Prerequisites: No contacts have the tag "nonexistent".

    -  Test case: `filter t/nonexistent`<br>
       Expected: Empty list is displayed. Status message shows "0 contact(s) listed!".

4. Invalid filter commands

    -  Test case: `filter`<br>
       Expected: No filtering occurs. Error message shows invalid command format with usage instructions.

    -  Test case: `filter friends`<br>
       Expected: Same as above.

    -  Test case: `filter t/`<br>
       Expected: Same as above.

5. Clearing the filter

    -  Test case: `list`
       Expected: All contacts are displayed again, removing the filter.

### Command Recall
1. Backward recall 

    - Prerequisites: Started FastCard and have used a series of `n` valid commands during current instance.
   
    - Test case: Press &uarr; arrow (n + 1) times 
    - Expected: Each `i`th keypress modifies the command box with the last `i`th valid command used. Up till the limit, where an error message showing End of History message, and command box is empty.

2. Forward recall

    - Prerequisites: Performed prior Backward recall manual test, and has the same instance running.
   
   - Test case: Press &darr; (n + 1) times
   - Expected: Each `i`th keypress modifies the command box with the last `n - i`th valid command used. Up till the current state in history, and the command box will just be empty

### View Contact Details
1. Toggle View Pane

    - Prerequisites: View Pane is not hidden or not populated. `view` command unused prior in current instance.
   
    - Test case: `view` twice.
    - Expected: (First) View pane comes into view with a guiding message on its usage. (Second) View pane gets hidden.

2. View details of a contact
    
    - Prerequisites: At least one contact in company book. 
   
    - Test case: `view 1`
    - Expected: The first contact in the current filtered list has their details laid out fully on the detail pane. 

3. Click to Copy

    - Prerequisites: Performed (2) 'View details of a contact test'
   
    - Test case: Click on one of the buttons beside an information
    - Expected: A 'copied' feedback is shown. Corresponding data is saved to clipboard.


### Saving Data

1. Dealing with missing/corrupted data files
   Precondition: `FastCard` application is closed

   1. Test case (Application Launch with Missing Data File):<br>
      Steps:
         * Navigate to the application's `data` folder
         * Delete `fastcard.json` if the file exists
         * Launch `FastCard` again
      Expected: Default list of contacts shown.
   
   1. Test case (Corrupted data file):<br>
      Steps:
         * Navigate to the application's `data` folder
         * Delete the name of any contact and save your changes
         * Launch `FastCard` again
      Expected: No contacts shown.

--------------------------------------------------------------------------------------------------------------------

### Appendix: Planned Enhancements

Team size: 5

1. **Make filter error messages more error-specific.**
Some errors from the filter command can be made more specific. We plan to make the error message mention the reason for the failure (invalid tag, empty tag, etc).
