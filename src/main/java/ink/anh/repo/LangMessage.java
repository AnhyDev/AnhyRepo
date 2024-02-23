package ink.anh.repo;

import ink.anh.api.lingo.lang.LanguageManager;

public class LangMessage extends LanguageManager {

    private static LangMessage instance = null;
    private static final Object LOCK = new Object();

    private LangMessage(GlobalManager manager) {
        super(manager, "lang");
    }

    public static LangMessage getInstance(GlobalManager manager) {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new LangMessage(manager);
                }
            }
        }
        return instance;
    }
}
