package zad1.admin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class AdminDataState {

    public  Set<String> allTopics = new ConcurrentSkipListSet<>();
    public Map<String, List<String>> newsOnTopics = new ConcurrentHashMap<>();

    public boolean adminWantsToUpdateTopics = false;
    public  Set<String> newsChanged = new ConcurrentSkipListSet<>();
}
