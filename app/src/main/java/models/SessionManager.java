package models;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {
    private static SessionManager instance; //синглтон
    private Session currentSession;
    private List<Session> sessionHistory = new ArrayList<>();

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void startSession(Station station) {
        String sessionId = "session_" + System.currentTimeMillis();
        currentSession = new Session(sessionId, station);
    }

    public void stopCurrentSession(double powerConsumed) {
        if (currentSession != null) {
            currentSession.endSession(powerConsumed);
            sessionHistory.add(currentSession);
            currentSession = null;
        }
    }

    public Session getCurrentSession() {
        return currentSession;
    }

    public List<Session> getSessionHistory() {
        return new ArrayList<>(sessionHistory);
    }
}
