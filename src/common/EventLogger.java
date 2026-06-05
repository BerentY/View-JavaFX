package common;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ORTAK DOSYA — Simülasyon olay kaydı.
 *
 * Controller olayları kaydeder.
 * View isteğe bağlı olarak gösterebilir.
 * Model bu sınıfı kullanmaz.
 *
 * Thread-safe değil — JavaFX Application Thread'de kullanılmalı.
 */
public class EventLogger {

    private static final int    MAX_LOG_SIZE = 200;
    private static final String TIME_FORMAT  = "HH:mm:ss";

    private static final EventLogger INSTANCE = new EventLogger();

    private final List<LogEntry> entries;

    private EventLogger() {
        this.entries = new ArrayList<>();
    }

    public static EventLogger getInstance() {
        return INSTANCE;
    }

    /** Yeni olay kaydeder */
    public void log(SimulationEvent event) {
        log(event, null);
    }

    /** Ek bilgiyle olay kaydeder */
    public void log(SimulationEvent event, String detail) {
        String time    = LocalTime.now().format(DateTimeFormatter.ofPattern(TIME_FORMAT));
        String message = event.getDisplayMessage();
        if (detail != null && !detail.isEmpty()) {
            message += " — " + detail;
        }
        entries.add(new LogEntry(time, event, message));

        // Log boyutunu sınırla
        if (entries.size() > MAX_LOG_SIZE) {
            entries.remove(0);
        }
    }

    /** Tüm log girişlerinin değiştirilemez kopyası */
    public List<LogEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    /** Son N girişi döndür */
    public List<LogEntry> getLastEntries(int n) {
        int from = Math.max(0, entries.size() - n);
        return Collections.unmodifiableList(entries.subList(from, entries.size()));
    }

    /** Logu temizle (simülasyon sıfırlanınca) */
    public void clear() {
        entries.clear();
    }

    // ── İç sınıf: Log satırı ────────────────────────────────

    public static class LogEntry {
        private final String          timestamp;
        private final SimulationEvent event;
        private final String          message;

        LogEntry(String timestamp, SimulationEvent event, String message) {
            this.timestamp = timestamp;
            this.event     = event;
            this.message   = message;
        }

        public String          getTimestamp() { return timestamp; }
        public SimulationEvent getEvent()     { return event; }
        public String          getMessage()   { return message; }

        @Override
        public String toString() {
            return "[" + timestamp + "] " + message;
        }
    }
}
