package seedu.contact;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.contact.commons.core.Config;
import seedu.contact.commons.core.LogsCenter;
import seedu.contact.commons.core.Version;
import seedu.contact.commons.exceptions.DataLoadingException;
import seedu.contact.commons.util.ConfigUtil;
import seedu.contact.commons.util.StringUtil;
import seedu.contact.logic.Logic;
import seedu.contact.logic.LogicManager;
import seedu.contact.model.ContactBook;
import seedu.contact.model.Model;
import seedu.contact.model.ModelManager;
import seedu.contact.model.ReadOnlyContactBook;
import seedu.contact.model.ReadOnlyUserPrefs;
import seedu.contact.model.UserPrefs;
import seedu.contact.model.util.SampleDataUtil;
import seedu.contact.storage.ContactBookStorage;
import seedu.contact.storage.JsonContactBookStorage;
import seedu.contact.storage.JsonUserPrefsStorage;
import seedu.contact.storage.Storage;
import seedu.contact.storage.StorageManager;
import seedu.contact.storage.UserPrefsStorage;
import seedu.contact.ui.Ui;
import seedu.contact.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final Version VERSION = new Version(0, 2, 2, true);

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    protected Config config;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing ContactBook ]===========================");
        super.init();

        AppParameters appParameters = AppParameters.parse(getParameters());
        config = initConfig(appParameters.getConfigPath());
        initLogging(config);

        UserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(config.getUserPrefsFilePath());
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        ContactBookStorage contactBookStorage = new JsonContactBookStorage(userPrefs.getContactBookFilePath());
        storage = new StorageManager(contactBookStorage, userPrefsStorage);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic);
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s contact book and {@code userPrefs}. <br>
     * The data from the sample contact book will be used instead if {@code storage}'s contact book is not found,
     * or an empty contact book will be used instead if errors occur when reading {@code storage}'s contact book.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        logger.info("Using data file : " + storage.getContactBookFilePath());

        Optional<ReadOnlyContactBook> ContactBookOptional;
        ReadOnlyContactBook initialData;
        try {
            ContactBookOptional = storage.readContactBook();
            if (!ContactBookOptional.isPresent()) {
                logger.info("Creating a new data file " + storage.getContactBookFilePath()
                        + " populated with a sample ContactBook.");
            }
            initialData = ContactBookOptional.orElseGet(SampleDataUtil::getSampleContactBook);
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getContactBookFilePath() + " could not be loaded."
                    + " Will be starting with an empty ContactBook.");
            initialData = new ContactBook();
        }

        return new ModelManager(initialData, userPrefs);
    }

    private void initLogging(Config config) {
        LogsCenter.init(config);
    }

    /**
     * Returns a {@code Config} using the file at {@code configFilePath}. <br>
     * The default file path {@code Config#DEFAULT_CONFIG_FILE} will be used instead
     * if {@code configFilePath} is null.
     */
    protected Config initConfig(Path configFilePath) {
        Config initializedConfig;
        Path configFilePathUsed;

        configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

        if (configFilePath != null) {
            logger.info("Custom Config file specified " + configFilePath);
            configFilePathUsed = configFilePath;
        }

        logger.info("Using config file : " + configFilePathUsed);

        try {
            Optional<Config> configOptional = ConfigUtil.readConfig(configFilePathUsed);
            if (!configOptional.isPresent()) {
                logger.info("Creating new config file " + configFilePathUsed);
            }
            initializedConfig = configOptional.orElse(new Config());
        } catch (DataLoadingException e) {
            logger.warning("Config file at " + configFilePathUsed + " could not be loaded."
                    + " Using default config properties.");
            initializedConfig = new Config();
        }

        //Update config file in case it was missing to begin with or there are new/unused fields
        try {
            ConfigUtil.saveConfig(initializedConfig, configFilePathUsed);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }
        return initializedConfig;
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(UserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (!prefsOptional.isPresent()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting ContactBook " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping ContactBook ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
