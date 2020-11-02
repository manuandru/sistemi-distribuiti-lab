package sd.lab.agency.behaviour.impl;

import sd.lab.agency.behaviour.Behaviour;
import sd.lab.agency.Agent;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class Wait implements Behaviour {

    private final Duration duration;
    private boolean started;
    private OffsetDateTime clock;
    private boolean ended;

    public Wait(Duration duration) {
        this.duration = duration;
    }

    @Override
    public Behaviour deepClone() {
        return new Wait(duration);
    }

    @Override
    public void execute(Agent agent) {
        if (started) {
            if (getElapsedMillis() >= duration.toMillis()) {
                ended = true;
            }
        } else {
            started = true;
            clock = OffsetDateTime.now();
        }
    }

    @Override
    public boolean isOver() {
        return ended;
    }

    private long getElapsedMillis() {
        return ChronoUnit.MILLIS.between(clock, OffsetDateTime.now());
    }
}
