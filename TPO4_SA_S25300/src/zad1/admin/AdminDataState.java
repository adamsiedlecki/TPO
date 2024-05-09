package zad1.admin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AdminDataState {

    public  List<String> allTopics = new ArrayList<>();
    public Map<String, List<String>> newsOnTopics = new ConcurrentHashMap<>();

    public boolean adminWantsToUpdateTopics = false;
    public  Set<String> newsChanged = new HashSet<>();
}
