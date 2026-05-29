package game.combate

class BattleLog {
    private final List<String> entries = []

    void add(String message) {
        entries << message
    }

    List<String> all() {
        entries.asImmutable()
    }

    String latest(String fallback = '') {
        entries ? entries.last() : fallback
    }

    void clear() {
        entries.clear()
    }
}
