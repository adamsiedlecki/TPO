package zad1.klient;

import java.util.*;

public class DataState {

    public  Set<String> allTopics = new HashSet<>();
    public  Set<String> userPickedTopics = new HashSet<>();
    public Map<String, List<String>> newsOnTopics = new HashMap<>();

    public boolean clientWantsToUpdateTopics = false;
}
