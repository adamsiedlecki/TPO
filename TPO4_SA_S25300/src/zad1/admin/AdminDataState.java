package zad1.admin;

import java.util.*;

public class AdminDataState {

    public  Set<String> allTopics = new HashSet<>();
    public Map<String, List<String>> newsOnTopics = new HashMap<>();

    public boolean adminWantsToUpdateTopics = false;
    public  Set<String> newsChanged = new HashSet<>();
}
